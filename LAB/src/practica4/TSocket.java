package practica4;

import practica1.CircularQ.CircularQueue;
import util.Const;
import util.TCPSegment;
import util.TSocket_base;

public class TSocket extends TSocket_base {

  //sender variable:
  protected int MSS, rcvNext;

  //receiver variables:
  protected CircularQueue<TCPSegment> rcvQueue;
  protected int rcvSegConsumedBytes, segmentNumber;

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
    TCPSegment segment = new TCPSegment();
    segmentNumber = 0;
    while (length > MSS) {
      segment = segmentize(data, offset + segmentNumber*MSS, MSS);
      segment.setSourcePort(localPort);
      segment.setDestinationPort(remotePort);  
      network.send(segment);
      length = length - MSS;
      segmentNumber++;
    }
    segment = segmentize(data, offset + segmentNumber*MSS, length);
    segment.setSourcePort(localPort);
    segment.setDestinationPort(remotePort);  
    network.send(segment);
  }

  protected TCPSegment segmentize(byte[] data, int offset, int length) {
    TCPSegment seg = new TCPSegment();
    seg.setPsh(true);
    seg.setSeqNum(segmentNumber);
    seg.setData(data, offset, length);
    return seg;
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

  protected void sendAck() {
    TCPSegment seg = new TCPSegment();
    seg.setSourcePort(localPort);
    seg.setDestinationPort(remotePort);
    seg.setAck(true);
    seg.setAckNum(rcvNext);
    seg.setWnd(rcvQueue.free());
    network.send(seg);
  }

  @Override
  public void processReceivedSegment(TCPSegment rseg) {

    lock.lock();
    try {     
      if (rseg.isAck()){
        //nothing to be done in this case.
      } else {
        rcvQueue.put(rseg);
        appCV.signal();
        rcvNext = rseg.getSeqNum() + 1;
        sendAck();
      }
    } finally {
      lock.unlock();
    }
  }

}
