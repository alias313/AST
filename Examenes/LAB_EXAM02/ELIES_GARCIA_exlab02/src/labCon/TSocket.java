package labCon;

import practica1.CircularQ.CircularQueue;
import util.Const;
import util.TCPSegment;
import util.TSocket_base;


public class TSocket extends TSocket_base {

  protected Protocol proto;

  protected int state;
  protected CircularQueue<TSocket> acceptQueue;
  protected TCPSegment unacknowledgedSeg;
  

  int numRetrans = 3;

  // States of FSM:
  protected final static int  CLOSED      = 0,
                              LISTEN      = 1,
                              SYN_SENT    = 2,
                              ESTABLISHED = 3,
                              FIN_WAIT    = 4,
                              CLOSE_WAIT  = 5;

  protected TSocket(Protocol p, int localPort, int remotePort) {
    super(p.getNetwork());
    proto = p;
    this.localPort = localPort;
    this.remotePort = remotePort;
    state = CLOSED;
    p.addActiveTSocket(this);
  }

  @Override
  public void connect() {
    lock.lock();
    try {
      unacknowledgedSeg = sendSYN();
      state = SYN_SENT;
      log.printGREEN("\t\t\t\t PORT: " + localPort + " ##### STATE SYN_SENT #####");
      startRTO();
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void close() {
    lock.lock();
    try {
      switch (state) {
        case ESTABLISHED:
        case CLOSE_WAIT: {
          if (state == ESTABLISHED) {
            unacknowledgedSeg = sendFIN();
            state = FIN_WAIT;
            log.printGREEN("\t\t\t\t PORT: " + localPort + " ##### STATE FIN_WAIT #####");
            startRTO();
            while (state != CLOSED) {
              appCV.awaitUninterruptibly();
            }  
          } else if (state == CLOSE_WAIT) {
            unacknowledgedSeg = sendFIN();
            startRTO();
            while (state != CLOSED) {
              appCV.awaitUninterruptibly();
            }  
          }
          break;
        }
      }
    } finally {
      lock.unlock();
    }
  }
  
    @Override
    protected void timeout() {
      lock.lock();
      try {
        if (unacknowledgedSeg != null) {
          if (unacknowledgedSeg.isFin()){
            if (numRetrans == 0) {
              stopRTO();
              state = CLOSED;
              if (state == FIN_WAIT) appCV.signal();
              log.printGREEN("\t\t\t\t PORT: " + localPort + " ##### STATE CLOSED REACHED RETRANS LIMIT #####");
            }
          }
          if (state != CLOSED) {
            log.printPURPLE("retrans: " + unacknowledgedSeg.toString());
            network.send(unacknowledgedSeg);
            if (unacknowledgedSeg.isFin()) numRetrans--;
            startRTO();
          }
        }
      } finally {
        lock.unlock();
      }
      }  

  /**
   * Segment arrival.
   *
   */
  @Override
  public void processReceivedSegment(TCPSegment rseg) {
    lock.lock();
    try {

      printRcvSeg(rseg);

      switch (state) {

        case SYN_SENT: {
          if (rseg.isSyn()) {
            state = ESTABLISHED;
            log.printGREEN("\t\t\t\t PORT: " + localPort + " ##### STATE ESTABLISHED #####");

            stopRTO();
            // continues from connect()
            //appCV.signal();
          }
          break;
        }
        
        case ESTABLISHED:
        case FIN_WAIT:
        case CLOSE_WAIT: {
          if (rseg.isPsh()) {
            if (state == ESTABLISHED || state == FIN_WAIT) {
              // Here should go the segment's data processing.
            } else {
              // This should not occur, since a FIN has been 
              // received from the remote side. 
              // Ignore the data segment.
            }
          }
          if (rseg.isSyn()) {
            if (state  == ESTABLISHED) {
              sendSYN();
            }
          }
          if (rseg.isFin()) {
            switch (state) {
              case ESTABLISHED:
                state = CLOSE_WAIT;
                log.printGREEN("\t\t\t\t PORT: " + localPort + " ##### STATE CLOSE_WAIT #####");
                appCV.signal();
                break;
              case FIN_WAIT:
                stopRTO();
                state = CLOSED;
                log.printGREEN("\t\t\t\t PORT: " + localPort + " ##### STATE CLOSED FROM FIN_WAIT#####");
                appCV.signal();
                break;
              default:
                break;
            }
          }
          break;
        }
      }
    } finally {
      lock.unlock();
    }
  }

  protected void printRcvSeg(TCPSegment rseg) {
    log.printBLACK("    rcvd: " + rseg);
  }

  protected void printSndSeg(TCPSegment rseg) {
    log.printBLACK("    sent: " + rseg);
  }
  
  protected void printRetSeg(TCPSegment rseg) {
        if (rseg.getSourcePort() < 50) {
            log.printPURPLE("    sent: " + rseg );
        } else {
            log.printPURPLE("\t\t\t\t\t\t\t    sent: " + rseg );
        }
  }

  public TCPSegment sendSYN(){
    TCPSegment seg = new TCPSegment();
    seg.setSourcePort(localPort);
    seg.setDestinationPort(remotePort);
    seg.setSyn(true);
    network.send(seg);
    return seg;
  }
  
  public void sendSYN_ACK(int numSeq, int numAck){
    TCPSegment seg = new TCPSegment();
    seg.setSourcePort(localPort);
    seg.setDestinationPort(remotePort);
    seg.setSyn(true);
    seg.setAck(true);
    seg.setSeqNum(numSeq);
    seg.setAckNum(numAck);
    network.send(seg);
  }
  
  public void sendACK(int numACK){
    TCPSegment seg = new TCPSegment();
    seg.setSourcePort(localPort);
    seg.setDestinationPort(remotePort);
    seg.setAck(true);
    seg.setAckNum(numACK);
    network.send(seg);
  }
  
  public void sendPSH(int numSeq, byte[] data, int offset, int length){
    TCPSegment seg = new TCPSegment();
    seg.setSourcePort(localPort);
    seg.setDestinationPort(remotePort);
    seg.setPsh(true);
    seg.setSeqNum(numSeq);
    seg.setData(data, offset, length);
    network.send(seg);
  }
  
  public TCPSegment sendFIN(){
    TCPSegment seg = new TCPSegment();
    seg.setFin(true);
    seg.setSourcePort(localPort);
    seg.setDestinationPort(remotePort);
    network.send(seg);
    return seg;
  }

}
