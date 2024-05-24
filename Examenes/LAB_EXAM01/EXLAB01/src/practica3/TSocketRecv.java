package practica3;

import practica1.CircularQ.CircularQueue;
import utils.Const;
import utils.TCPSegment;
import utils.TSocket_base;
import utils.SimNet;

public class TSocketRecv extends TSocket_base {
    
    protected Thread thread;
    protected CircularQueue<TCPSegment> rcvQueue;
    protected int totals, gastats;    
    protected byte[] buffer = new byte[network.getMTU() - Const.IP_HEADER - Const.TCP_HEADER];
    //Completar...
    
    public TSocketRecv(SimNet net) {
        super(net);
        rcvQueue = new CircularQueue<>(Const.RCV_QUEUE_SIZE);
        new ReceiverTask().start();
    }
    
    @Override
    public int receiveData(byte[] buf, int offset, int length) {
        this.lock.lock();
        try{
            while(rcvQueue.empty() && totals == 0){
                appCV.awaitUninterruptibly();
            }
            int agafats = 0;
            
            if(totals == 0){
                totals = consumeSegment(buffer);
            }
            while(length>agafats){
                int actuals = Math.min(length-agafats, totals - gastats);
                System.arraycopy(buffer, gastats, buf, offset+agafats, actuals);
                agafats+=actuals;
                gastats+=actuals;
                if (gastats == totals) {
                    gastats = 0;
                    totals = 0;
                    if (!rcvQueue.empty()) {
                        totals = consumeSegment(buffer);
                    }
                }
            }
            return agafats;
        }finally{
            lock.unlock();
        }
    }
    
    protected int consumeSegment(byte[] buf) {
        TCPSegment seg = rcvQueue.get();
        System.arraycopy(seg.getData(), 0, buf, 0, seg.getDataLength());
        return seg.getDataLength();
    }
    
    @Override
    public void processReceivedSegment(TCPSegment rseg) {
        lock.lock();
        try {
            printRcvSeg(rseg);
            if (rseg.isPsh()) {
                if (rcvQueue.full()) {
                    log.printRED("\t\t\t\t\t\t\t\tQueue full, discarded segment!!!");
                    return;
                }
                rseg.setData(rseg.getData(), 0, rseg.getDataLength() / 2);
                rcvQueue.put(rseg);
                appCV.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }
    
    class ReceiverTask extends Thread {
        
        @Override
        public void run() {
            while (true) {
                TCPSegment rseg = network.receive();
                processReceivedSegment(rseg);
            }
        }
    }
}
