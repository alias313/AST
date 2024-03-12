package practica4;

import practica1.CircularQ.CircularQueue;
import util.Const;
import util.TCPSegment;
import util.TSocket_base;

public class TSocket extends TSocket_base {

  //sender variable:
  protected int MSS;

  //receiver variables:
  protected CircularQueue<TCPSegment> rcvQueue;
  protected int rcvSegConsumedBytes;

  protected TSocket(Protocol p, int localPort, int remotePort) {
    super(p.getNetwork());
    this.localPort  = localPort;
    this.remotePort = remotePort;
    p.addActiveTSocket(this);
    MSS = network.getMTU() - Const.IP_HEADER - Const.TCP_HEADER;
    rcvQueue = new CircularQueue<>(Const.RCV_QUEUE_SIZE);
    rcvSegConsumedBytes = 0;
  }

  @Override
  public void sendData(byte[] data, int offset, int length) {
    throw new RuntimeException("//Completar...");
  }

  protected TCPSegment segmentize(byte[] data, int offset, int length) {
    throw new RuntimeException("//Completar...");
  }

  @Override
  public int receiveData(byte[] buf, int offset, int length) {
    lock.lock();
    try {
      throw new RuntimeException("//Completar...");
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

  protected void sendAck() {
    throw new RuntimeException("//Completar...");
  }

  @Override
  public void processReceivedSegment(TCPSegment rseg) {

    lock.lock();
    try {     
      if (rseg.isAck()){
        //nothing to be done in this exercise.
      }
      throw new RuntimeException("//Completar...");
    } finally {
      lock.unlock();
    }
  }

}
