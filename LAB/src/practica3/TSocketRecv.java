package practica3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import practica1.CircularQ.CircularQueue;
import util.Const;
import util.TCPSegment;
import util.TSocket_base;
import util.SimNet;

public class TSocketRecv extends TSocket_base {

  //protected Thread thread;
  protected CircularQueue<TCPSegment> rcvQueue;
  protected int rcvSegConsumedBytes, rcvNext;

  public TSocketRecv(SimNet network) {
    super(network);
    rcvQueue = new CircularQueue<>(Const.RCV_QUEUE_SIZE);
    new ReceiverTask().start();
  }

  @Override
  public int receiveData(byte[] buf, int offset, int length) {
    lock.lock();
    try {
      int bytesConsumed = 0;
      while (rcvQueue.empty()) {
        appCV.awaitUninterruptibly();
      }
      while (bytesConsumed < length && !rcvQueue.empty()) {
        bytesConsumed += consumeSegment(buf, offset+bytesConsumed, length-bytesConsumed);
      }
      return bytesConsumed;
    } finally {
      lock.unlock();
    }
  }

  protected int consumeSegment(byte[] buf, int offset, int length) {
    TCPSegment seg = rcvQueue.peekFirst();
    int a_agafar = Math.min(length, seg.getDataLength() - rcvSegConsumedBytes);
    System.arraycopy(seg.getData(), rcvSegConsumedBytes, buf, offset, a_agafar);
    rcvSegConsumedBytes += a_agafar;
    if (rcvSegConsumedBytes == seg.getDataLength()) {
      rcvQueue.get();
      rcvSegConsumedBytes = 0;
    }
    return a_agafar;
  }

  @Override
  public void processReceivedSegment(TCPSegment rseg) {
    lock.lock();
    try {
      //System.out.println("PROCESS" + rseg.toString());
      if (!rcvQueue.full()) {
        rcvQueue.put(rseg);
        this.printRcvSeg(rseg);
        appCV.signal();
        //rcvNext = rseg.getSeqNum() + 1;
      }

/*       TCPSegment tseg = new TCPSegment();
      tseg.setAck(true);
      tseg.setAckNum(rcvNext);
      tseg.setWnd(rcvQueue.free());
      network.send(tseg);
 */    } finally {
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
