package practica6;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import practica1.CircularQ.CircularQueue;
import practica4.Protocol;
import util.Const;
import util.TCPSegment;
import util.TSocket_base;

public class TSocket extends TSocket_base {

    protected int MSS;
    protected int snd_sndNxt;
    protected int snd_rcvNxt;
    protected int snd_rcvWnd;
    protected int snd_cngWnd;
    protected int snd_minWnd;
    protected Map<Integer, TimerTask> scheduler;
    protected boolean zero_wnd_probe_ON;

    protected int rcv_rcvNxt;
    protected CircularQueue<TCPSegment> rcv_Queue;
    protected int rcv_SegConsumedBytes;
    protected Map<Integer, TCPSegment> out_of_order_segs;

    protected TSocket(Protocol p, int localPort, int remotePort) {
        super(p.getNetwork());
        this.localPort = localPort;
        this.remotePort = remotePort;
        p.addActiveTSocket(this);
        
        MSS = p.getNetwork().getMTU() - Const.IP_HEADER - Const.TCP_HEADER;
        snd_rcvWnd = Const.RCV_QUEUE_SIZE;
        snd_cngWnd = 3;
        snd_minWnd = Math.min(snd_rcvWnd, snd_cngWnd);
        scheduler = new HashMap<>();
        rcv_Queue = new CircularQueue<>(Const.RCV_QUEUE_SIZE);
        out_of_order_segs = new HashMap<>();
    }

    @Override
    public void sendData(byte[] data, int offset, int length) {
        lock.lock();
        try {
            TCPSegment segment;
            int quedenPerEnviar = length;
            int bytesAPosar;

            while (quedenPerEnviar > 0) {
                while ((snd_sndNxt - snd_rcvNxt) >= snd_minWnd && snd_rcvWnd > 0 || zero_wnd_probe_ON) {
                    appCV.awaitUninterruptibly();
                }

                if (snd_rcvWnd > 0) {
                    bytesAPosar = Math.min(quedenPerEnviar, MSS);

                    segment = segmentize(data, offset, bytesAPosar);
                    offset += bytesAPosar;
                    quedenPerEnviar -= bytesAPosar;

                    segment.setSeqNum(snd_sndNxt);
                    network.send(segment);
                    this.printSndSeg(segment);

                    TimerTask timer = startRTO(segment);
                    scheduler.put(segment.getSeqNum(), timer);

                    snd_sndNxt++;
                } else {
                    bytesAPosar = 1;
                    segment = segmentize(data, offset, bytesAPosar);
                    offset++;
                    quedenPerEnviar--;

                    segment.setSeqNum(snd_sndNxt);
                    network.send(segment);
                    this.printSndSeg(segment);

                    TimerTask timer = startRTO(segment);
                    scheduler.put(segment.getSeqNum(), timer);
                    snd_sndNxt++;
                    zero_wnd_probe_ON = true;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    protected TCPSegment segmentize(byte[] data, int offset, int length) {
        TCPSegment segment = new TCPSegment();
        byte[] segmentData = new byte[length];
        System.arraycopy(data, offset, segmentData, 0, length);
        segment.setData(segmentData);
        segment.setPsh(true);
        segment.setSourcePort(localPort);
        segment.setDestinationPort(remotePort);
        return segment;
    }

    @Override
    protected void timeout(TCPSegment seg) {
        lock.lock();
        try {
            if (scheduler.containsKey(seg.getSeqNum())) {
                if (zero_wnd_probe_ON){
                    log.printPURPLE("0-wnd probe: " + seg);
                }
                else{
                    log.printPURPLE("retrans: " + seg);
                }
                network.send(seg);
                //this.printSndSeg(seg);

                TimerTask timerTask = startRTO(seg);
                scheduler.put(seg.getSeqNum(), timerTask);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int receiveData(byte[] buf, int offset, int maxlen) {
        lock.lock();
        try {
            while (rcv_Queue.empty()) {
                appCV.awaitUninterruptibly();
            }
            int consumar = 0;
            while (!rcv_Queue.empty() && consumar < maxlen) {
                consumar += consumeSegment(buf, offset + consumar, maxlen - consumar);
            }
            return consumar;
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
        ack.setAckNum(rcv_rcvNxt);
        ack.setSourcePort(localPort);
        ack.setDestinationPort(remotePort);
        ack.setWnd(rcv_Queue.free());
        network.send(ack);
    }

    @Override
    public void processReceivedSegment(TCPSegment rseg) {
        lock.lock();
        try {
            if (rseg.isAck()) {
                if (rseg.getAckNum() > snd_rcvNxt) {
                    int ackNum = rseg.getAckNum();
                    while (scheduler.containsKey(snd_rcvNxt) && snd_rcvNxt < ackNum) {
                        TimerTask timerTask = scheduler.remove(snd_rcvNxt);
                        if (timerTask != null) {
                            stopRTO(timerTask);
                        }
                        snd_rcvNxt++;
                    }
                    snd_rcvWnd = rseg.getWnd();
                    snd_minWnd = Math.min(snd_rcvWnd, snd_cngWnd);

                    if (zero_wnd_probe_ON) {
                        zero_wnd_probe_ON = false;
                    }
                    appCV.signal();
                }
            }
            if (rseg.isPsh()) {
                if (rseg.getSeqNum() == rcv_rcvNxt) {
                    if (!rcv_Queue.full()) {
                        rcv_Queue.put(rseg);
                        rcv_rcvNxt++;
                        sendAck();
                        printRcvSeg(rseg);
                        appCV.signal();
                    }
                } else if (rseg.getSeqNum() > rcv_rcvNxt) {
                    out_of_order_segs.put(rseg.getSeqNum(), rseg);
                    sendAck();
                    printRcvSeg(rseg);
                }
            }
            while (out_of_order_segs.containsKey(rcv_rcvNxt)) {
                TCPSegment segmentcua = out_of_order_segs.remove(rcv_rcvNxt);
                if (!rcv_Queue.full()) {
                    rcv_Queue.put(segmentcua);
                    rcv_rcvNxt++;
                    sendAck();
                    printRcvSeg(rseg);
                    appCV.signal();
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
