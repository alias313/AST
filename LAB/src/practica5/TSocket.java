package practica5;

import javax.swing.text.Segment;

import practica1.CircularQ.CircularQueue;
import practica4.Protocol;
import util.Const;
import util.TSocket_base;
import util.TCPSegment;

public class TSocket extends TSocket_base {

  // Sender variables:
  protected int MSS;
  protected int snd_sndNxt;
  protected int snd_rcvWnd;
  protected int snd_rcvNxt;
  protected TCPSegment snd_UnacknowledgedSeg;
  protected boolean zero_wnd_probe_ON;

  // Receiver variables:
  protected CircularQueue<TCPSegment> rcv_Queue;
  protected int rcv_SegConsumedBytes;
  protected int rcv_rcvNxt;

  protected TSocket(Protocol p, int localPort, int remotePort) {
    super(p.getNetwork());
    this.localPort = localPort;
    this.remotePort = remotePort;
    p.addActiveTSocket(this);
    // init sender variables
    MSS = p.getNetwork().getMTU() - Const.IP_HEADER - Const.TCP_HEADER;
    // init receiver variables
    rcv_Queue = new CircularQueue<>(Const.RCV_QUEUE_SIZE);
    snd_rcvWnd = Const.RCV_QUEUE_SIZE;
  }

  // -------------  SENDER PART  ---------------
  @Override
  public void sendData(byte[] data, int offset, int length) {
    lock.lock();
    try {
      int sent = 0;
      while (length > sent) {
        int to_send = Math.min(length - sent, MSS);
        TCPSegment sndSegment = segmentize(data, offset + sent, to_send);
        sndSegment.setSourcePort(localPort);
        sndSegment.setDestinationPort(remotePort);  
        network.send(sndSegment);
        snd_sndNxt++;
        appCV.awaitUninterruptibly();
        sent += to_send;
      }
      } finally {
      lock.unlock();
    }
  }

  protected TCPSegment segmentize(byte[] data, int offset, int length) {
    TCPSegment seg = new TCPSegment();
    seg.setPsh(true);
    seg.setSeqNum(snd_sndNxt);
    seg.setData(data, offset, length);
    return seg;
  }

  @Override
  protected void timeout() {
    lock.lock();
    try {
      throw new RuntimeException("//Completar...");
    } finally {
      lock.unlock();
    }
  }

  // -------------  RECEIVER PART  ---------------
  @Override
  public int receiveData(byte[] buf, int offset, int maxlen) {
    lock.lock();
    try {
      int bytesConsumed = 0;
      while (rcv_Queue.empty()) {
        appCV.awaitUninterruptibly();
      }
      while (bytesConsumed < maxlen && !rcv_Queue.empty()) {
        bytesConsumed += consumeSegment(buf, offset+bytesConsumed, maxlen-bytesConsumed);
      }
      return bytesConsumed;
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
    TCPSegment seg = new TCPSegment();
    seg.setSourcePort(localPort);
    seg.setDestinationPort(remotePort);
    seg.setAck(true);
    seg.setAckNum(rcv_rcvNxt);
    seg.setWnd(rcv_Queue.free());
    network.send(seg);
  }

  // -------------  SEGMENT ARRIVAL  -------------
  @Override
  public void processReceivedSegment(TCPSegment rseg) {
    lock.lock();
    try{
      if (rseg.isPsh()) {
          // variables de recv
          printRcvSeg(rseg);
          rcv_Queue.put(rseg);
          appCV.signal();
          rcv_rcvNxt = rseg.getSeqNum() + 1;
          sendAck();  
      } else if (rseg.isAck()) {
          // variables de snd
          printRcvSeg(rseg);
          appCV.signal();
      }
    } finally {
      lock.unlock();
    }
  }
}
