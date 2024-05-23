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
    rcv_Queue = new CircularQueue<>(2);
    snd_rcvWnd = 2;
  }

  // -------------  SENDER PART  ---------------
  @Override
  public void sendData(byte[] data, int offset, int length) {
    lock.lock();
    try {
      int bytesQuedenPerEnviar = length;
      while (bytesQuedenPerEnviar > 0) {
        while ((snd_sndNxt == snd_rcvNxt + snd_rcvWnd) && snd_rcvWnd > 0 ||
        zero_wnd_probe_ON) {
          //System.out.println("Aturem el fil");
          //System.out.println("Condició finestra zero:" + zero_wnd_probe_ON);
          //log.printBLUE("Tamany finestra: " + snd_rcvWnd);
          //System.out.println("Condició tamany finestra: " + snd_sndNxt + " = " + (snd_rcvNxt + "+" + snd_rcvWnd));
          appCV.awaitUninterruptibly();
        }
        if (snd_rcvWnd > 0) {
          int numBytesAPosarSegment = Math.min(MSS, bytesQuedenPerEnviar);
          TCPSegment seg = segmentize(data, offset, numBytesAPosarSegment);
          network.send(seg);
          snd_UnacknowledgedSeg = seg;
          snd_sndNxt++;
          bytesQuedenPerEnviar -= numBytesAPosarSegment;
          offset += numBytesAPosarSegment;
        } else {
          int numBytesAPosarSegment = 1;
          snd_UnacknowledgedSeg = segmentize(data, offset, numBytesAPosarSegment);
          log.printBLACK("----- zero-window probe ON -----");
          zero_wnd_probe_ON = true;
          bytesQuedenPerEnviar -= numBytesAPosarSegment;
          offset += numBytesAPosarSegment;
          snd_sndNxt++;
        }
        startRTO();
      }
    } finally {
      lock.unlock();
    }
  }

  protected TCPSegment segmentize(byte[] data, int offset, int length) {
    TCPSegment seg = new TCPSegment();
    byte[] finalData = new byte[length];
    for (int i = 0; i < length; i++) {
      finalData[i] = data[offset + i];
    }
    seg.setData(finalData);
    seg.setPsh(true);
    seg.setSeqNum(snd_sndNxt);
    seg.setDestinationPort(remotePort);
    seg.setSourcePort(localPort);
    return seg;
  }

  @Override
  protected void timeout() {
    lock.lock();
    try {
      if (snd_UnacknowledgedSeg != null) {
        if (zero_wnd_probe_ON) {
        //log.printBLUE("Espai lliure finestra: " + snd_rcvWnd);
        log.printPURPLE("0-wnd probe:" + snd_UnacknowledgedSeg);
        network.send(snd_UnacknowledgedSeg);
        } else {
        //log.printBLUE("Espai lliure finestra: " + snd_rcvWnd);
        log.printPURPLE("retrans" + snd_UnacknowledgedSeg);
        network.send(snd_UnacknowledgedSeg);
        }
        appCV.signal();
        startRTO();
      }    
    } finally {
      lock.unlock();
    }
  }

  // -------------  RECEIVER PART  ---------------
  @Override
  public int receiveData(byte[] buf, int offset, int maxlen) {
    lock.lock();
    try {
      while (rcv_Queue.empty()) {
        //log.printBLACK("Cua buida");
        appCV.awaitUninterruptibly();
      }
      int totalbytes = 0;
      int maximBytes = maxlen;
      while (totalbytes < maxlen && !rcv_Queue.empty()) {
        int agafats = consumeSegment(buf, offset, maximBytes);
        totalbytes += agafats;
        offset += agafats;
        maximBytes -= agafats;
      }
      return totalbytes;
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
    ack.setDestinationPort(remotePort);
    ack.setSourcePort(localPort);
    ack.setWnd(snd_rcvWnd);
    network.send(ack);
    snd_UnacknowledgedSeg = ack;
  }

  // -------------  SEGMENT ARRIVAL  -------------
  @Override
  public void processReceivedSegment(TCPSegment rseg) {
    lock.lock();
    try{
      if (rseg.isPsh()) {
        // REBEM UN SEGMENT
        //log.printBLACK("Igualtat:" + rseg.getSeqNum() + "=" + rcv_rcvNxt);
        if (rseg.getSeqNum() != rcv_rcvNxt) {
          System.out.println("Segment perdut" + rseg);
          sendAck();
          appCV.signal();
          return;
        }
        rcv_Queue.put(rseg);
        //log.printBLACK("Rebo el segment amb seqNum:" + rseg.getSeqNum());
        rcv_rcvNxt = rseg.getSeqNum() + 1;
        super.printRcvSeg(rseg);
        snd_rcvWnd = Math.min(1, rcv_Queue.free());
        sendAck();
        //System.out.println("Tamany finestra després de processar segment: " + snd_rcvWnd);
        appCV.signal();
      } else if (rseg.isAck()) {
        // REBEM UN ACK
        stopRTO();
        //log.printBLACK("Igualtat:" + rseg.getAckNum() + "=" + snd_sndNxt);
        if (snd_sndNxt != rseg.getAckNum()) {
          log.printRED("ACK no desitjat!");
        } else {
          //log.printBLUE("Tamany finestra: " + snd_rcvWnd);
          super.printRcvSeg(rseg);
          snd_rcvWnd = rseg.getWnd();
          snd_rcvNxt = rseg.getAckNum();
          if (zero_wnd_probe_ON) {
            log.printBLACK("----- zero-window probe OFF -----");
            zero_wnd_probe_ON = false;
          }
          appCV.signal();
        }
      }
    } finally {
      lock.unlock();
    }
  }
}
