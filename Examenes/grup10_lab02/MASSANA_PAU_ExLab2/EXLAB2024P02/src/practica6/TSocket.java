package practica6;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimerTask;
import practica1.CircularQ.CircularQueue;
import practica4.Protocol;
import util.Const;
import util.TCPSegment;
import util.TSocket_base;

public class TSocket extends TSocket_base {

    // Sender variables:
    protected int MSS;
    protected int snd_sndNxt=0;
    protected int snd_rcvNxt=0;
    protected int snd_rcvWnd;
    protected int snd_cngWnd;
    protected int snd_minWnd;
    protected Map<Integer, TimerTask> scheduler;
    protected boolean zero_wnd_probe_ON;

    // Receiver variables:
    protected int rcv_rcvNxt=0;
    int rcv_fora_ordre=0;
    protected CircularQueue<TCPSegment> rcv_Queue;
    protected int rcv_SegConsumedBytes;
    protected Map<Integer, TCPSegment> out_of_order_segs;

    protected TSocket(Protocol p, int localPort, int remotePort) {
        super(p.getNetwork());
        this.localPort = localPort;
        this.remotePort = remotePort;
        p.addActiveTSocket(this);
        
        // init sender variables:
        MSS = p.getNetwork().getMTU() - Const.IP_HEADER - Const.TCP_HEADER;
        snd_rcvWnd = Const.RCV_QUEUE_SIZE;
        snd_cngWnd = 3;
        snd_minWnd = Math.min(snd_rcvWnd, snd_cngWnd);
        scheduler = new HashMap<>();
        
        // init receiver variables:
        rcv_Queue = new CircularQueue<>(Const.RCV_QUEUE_SIZE);
        out_of_order_segs = new HashMap<>();
    }

    // -------------  SENDER PART  ---------------
    @Override
    public void sendData(byte[] data, int offset, int length) {
        lock.lock();
        try {
            int a_posar=0;
            for (int enviats = 0; enviats < length; enviats += a_posar){
                while(this.snd_sndNxt - this.snd_rcvNxt >= this.snd_minWnd) {
                    this.appCV.awaitUninterruptibly();
                }

                TCPSegment seg;
                a_posar = Math.min(length - enviats, this.MSS);
                seg = this.segmentize(data, offset + enviats, a_posar);
                seg.setSeqNum(snd_sndNxt);
                seg.setPsh(true);
                TimerTask tsk= startRTO(seg); 
                
                seg.setDestinationPort(remotePort);
                seg.setSourcePort(localPort);
                int seq_num=seg.getSeqNum();
                this.scheduler.put(seq_num, tsk);
                this.printSndSeg(seg);
                this.network.send(seg);
                ++this.snd_sndNxt;
                
                
                   

                
            }    
        } finally {
            lock.unlock();
        }
    }

    protected TCPSegment segmentize(byte[] data, int offset, int length) {
        TCPSegment seg = new TCPSegment();
        seg.setPsh(true);
        seg.setSourcePort(this.localPort);
        seg.setDestinationPort(this.remotePort);
        seg.setSeqNum(this.snd_sndNxt);
        seg.setData(data, offset, length);
        return seg;
    }

    @Override
    protected void timeout(TCPSegment seg) {
        lock.lock();
        try {
            TimerTask tsk=startRTO(seg);
            this.scheduler.remove(seg.getSeqNum());
            this.scheduler.put(seg.getSeqNum(), tsk);
            this.network.send(seg);
            this.log.printPURPLE("retrans: " + seg);
            
            
        } finally {
            lock.unlock();
        }
    }

    // -------------  RECEIVER PART  ---------------
    @Override
    public int receiveData(byte[] buf, int offset, int maxlen) {
        lock.lock();
        try {
            
            while(this.rcv_Queue.empty()) {
                this.appCV.awaitUninterruptibly();
            }

            int agafats;
            for(agafats = 0; maxlen > agafats && !this.rcv_Queue.empty();
                    agafats += this.consumeSegment(buf, offset + agafats, maxlen - agafats)) {
            }

            
            return agafats;            
        } finally {
            lock.unlock();
        }
    }

    protected int consumeSegment(byte[] buf, int offset, int length) {
        TCPSegment seg = rcv_Queue.peekFirst();
        int a_agafar = Math.min(length, seg.getDataLength() - rcv_SegConsumedBytes);
        System.arraycopy(seg.getData(), rcv_SegConsumedBytes, buf, offset, a_agafar);
        rcv_SegConsumedBytes += a_agafar;
        if (rcv_SegConsumedBytes == seg.getDataLength()) {
            rcv_Queue.get();
            rcv_SegConsumedBytes = 0;
        }
        return a_agafar;
    }

    protected void sendAck() {
        TCPSegment ack = new TCPSegment();
        ack.setAck(true);
        ack.setSourcePort(this.localPort);
        ack.setDestinationPort(this.remotePort);
        ack.setAckNum(this.rcv_rcvNxt);
        ack.setWnd(this.rcv_Queue.free());
        this.network.send(ack);
    }

    // -------------  SEGMENT ARRIVAL  -------------
    public void processReceivedSegment(TCPSegment rseg) {

        lock.lock();
        try {
            this.printRcvSeg(rseg);
            if(rseg.isAck()){          
                int seq_num_segment_rebut_al_reviever=rseg.getAckNum()-1;
                stopRTO(scheduler.get(seq_num_segment_rebut_al_reviever));
                scheduler.remove(seq_num_segment_rebut_al_reviever);
                
                this.snd_rcvNxt = rseg.getAckNum();
                this.snd_rcvWnd = rseg.getWnd();
                this.snd_minWnd = Math.max(1, Math.min(this.snd_rcvWnd, this.snd_cngWnd));
            }else if(rseg.isPsh()){
                if(rcv_Queue.full()){
                    log.printRED("Cua plena");
                }else{
                    if (rseg.getSeqNum() == rcv_rcvNxt) {
                        this.rcv_Queue.put(rseg);
                        appCV.signal();
                        rcv_fora_ordre++;
                        rcv_rcvNxt++;

                    } else {
                        this.out_of_order_segs.put(rseg.getSeqNum(), rseg);
                    }
                    
                    sendAck();
                    
                    Iterator<Integer> ite = out_of_order_segs.keySet().iterator();
                    
                    while (ite.hasNext()) {
                        int seq = ite.next();
                        if (rcv_fora_ordre ==seq) {
                            TCPSegment s=this.out_of_order_segs.get(seq);
                            rcv_Queue.put(s);
                            appCV.signal();
                            ite.remove();
                            rcv_fora_ordre++;
                            log.printGREEN("Posat fora d'ordre segment:" + s);
                        }
                    }
                    
                   
                }
                
                
                
            }
            
            
            
        } finally {
            lock.unlock();
        }
    }

}
