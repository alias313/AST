package util;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SimNet_monitor implements SimNet {

  protected CircularQueue<TCPSegment> queue;
  protected ReentrantLock lock;
  protected Condition putCnd;
  protected Condition getCnd;

  public SimNet_monitor(int N) {
    queue  = new CircularQueue<>(N);
    lock   = new ReentrantLock();
    putCnd = lock.newCondition();
    getCnd = lock.newCondition();
  }

  @Override
  public void send(TCPSegment seg) {
    lock.lock();
    try {
      while (queue.full()) {
        putCnd.awaitUninterruptibly();
      }
      queue.put(seg);
      getCnd.signal();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public TCPSegment receive() {
    TCPSegment tmp;
    lock.lock();
    try {
      while (queue.empty()) {
        getCnd.awaitUninterruptibly();
      }
      putCnd.signal();
      tmp = queue.get();
    } finally {
      lock.unlock();
    }
    return tmp;
  }

  @Override
  public int getMTU() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
