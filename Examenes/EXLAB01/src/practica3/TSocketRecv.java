package practica3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import practica1.CircularQ.CircularQueue;
import utils.Const;
import utils.TCPSegment;
import utils.TSocket_base;
import utils.SimNet;
import utils.Receiver;

public class TSocketRecv extends TSocket_base {
    
    protected Thread thread;
    protected CircularQueue<TCPSegment> rcvQueue;
    protected TCPSegment segConsuming;
    protected int rcvNext, rcvSegConsumedBytes;
    protected Lock mon;
    protected Condition potConsumir;
    
    public TSocketRecv(SimNet net) {
        super(net);
        rcvQueue = new CircularQueue<>(Const.RCV_QUEUE_SIZE);
        mon = new ReentrantLock();
        potConsumir = mon.newCondition();
        segConsuming = new TCPSegment();
        new ReceiverTask().start();
    }
    
    @Override
    public int receiveData(byte[] buf, int offset, int length) {
        try {
            int tmp;
            mon.lock();
            System.out.println("RECIEVE" + rcvQueue.toString());
            while (rcvQueue.empty()) {
                System.out.println("-----------------HE ENTRAT--------------");
                potConsumir.awaitUninterruptibly();
                System.out.println("-----------------HE SORTIT--------------");
            }
            tmp = consumeSegment(buf);
            System.out.println("-------------------HE CONSUMIT------------------");
            return tmp;
        } catch (Exception ex) {
            System.out.println(ex);
            return 1;
        } finally {
            mon.unlock();
        }
    }
    
    protected int consumeSegment(byte[] buf) {
        TCPSegment seg = rcvQueue.get();
        System.arraycopy(seg.getData(), 0, buf, 0, seg.getDataLength());
        return seg.getDataLength();

/*         System.out.println("-----------------HE ENTRAT A CONSUMIR--------------");
        if (rcvSegConsumedBytes == 0) {
            segConsuming = rcvQueue.get();
        }
        System.out.println(segConsuming.toString());
        int minLength = Math.min(buf.length, segConsuming.getDataLength() - rcvSegConsumedBytes);
        System.arraycopy(segConsuming.getData(), rcvSegConsumedBytes, buf, 0, minLength);
        for (int i = 0; i < minLength; i++) {
            System.out.println(segConsuming.getData()[i + rcvSegConsumedBytes]);
        }
        rcvSegConsumedBytes += minLength;
        System.out.println("minLength: " + minLength);
        if (rcvSegConsumedBytes == segConsuming.getDataLength()) {
            rcvSegConsumedBytes = 0;
        }
        return minLength;
 */    }
    
    @Override
    public void processReceivedSegment(TCPSegment rseg) {
        try {
            mon.lock();
            //System.out.println("PROCESS" + rseg.toString());
            if (!rcvQueue.full()) {
                rcvQueue.put(rseg);
                potConsumir.signal();
                rcvNext = rseg.getSeqNum() + 1;
            }

            TCPSegment tseg = new TCPSegment();
            tseg.setAck(true);
            tseg.setAckNum(rcvNext);
            tseg.setWnd(rcvQueue.free());
            network.send(tseg);
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            mon.unlock();
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
