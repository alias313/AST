package practica4;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Const;
import util.TCPSegment;
import util.SimNet;

public class SimNet_Monitor implements SimNet {

  protected CircularQueue<TCPSegment> queue;
  protected Lock mon;
  protected Condition qFull, qFree;

  public SimNet_Monitor() {
    queue  = new CircularQueue<>(Const.SIMNET_QUEUE_SIZE);
    mon = new ReentrantLock();
    qFree = mon.newCondition();
    qFull = mon.newCondition();
  }

  @Override
  public void send(TCPSegment seg) {
    mon.lock();
    try {
      //System.out.println("Sent segment: " + seg.toString());
        while (queue.full()) {
            qFull.awaitUninterruptibly();
        }
        qFree.signalAll();
        queue.put(seg);
    } finally {
        mon.unlock();
    }
  }

  @Override
  public TCPSegment receive() {
    try {
        mon.lock();
        while (queue.empty()) {
            qFree.awaitUninterruptibly();
        }
        qFull.signalAll();
        return queue.get();
    } finally {
        mon.unlock();
    }
 }

  @Override
  public int getMTU() {
    throw new UnsupportedOperationException("Not supported yet. NO cal completar fins a la pràctica 3...");
  }

}
