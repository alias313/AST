package practica7;

import practica1.CircularQ.CircularQueue;
import util.Const;
import util.TCPSegment;
import util.TSocket_base;

/**
 * Connection oriented Protocol Control Block.
 *
 * Each instance of TSocket maintains all the status of an endpoint.
 * 
 * Interface for application layer defines methods for passive/active opening and for closing the connection.
 * Interface lower layer defines methods for processing of received segments and for sending of segments.
 * We assume an ideal lower layer with no losses and no errors in packets.
 *
 * State diagram:<pre>
                              +---------+
                              |  CLOSED |-------------
                              +---------+             \
                           LISTEN  |                   \
                           ------  |                    | CONNECT
                                   V                    | -------
                              +---------+               | snd SYN
                              |  LISTEN |               |
                              +---------+          +----------+
                                   |               | SYN_SENT |
                                   |               +----------+
                         rcv SYN   |                    |
                         -------   |                    | rcv SYN
                         snd SYN   |                    | -------
                                   |                    |
                                   V                   /
                              +---------+             /
                              |  ESTAB  |<------------
                              +---------+
                       CLOSE    |     |    rcv FIN
                      -------   |     |    -------
 +---------+          snd FIN  /       \                    +---------+
 |  FIN    |<-----------------           ------------------>|  CLOSE  |
 |  WAIT   |------------------           -------------------|  WAIT   |
 +---------+          rcv FIN  \       /   CLOSE            +---------+
                      -------   |      |  -------
                                |      |  snd FIN 
                                V      V
                              +----------+
                              |  CLOSED  |
                              +----------+
 * </pre>
 *
 * @author AST's teachers
 */

public class TSocket extends TSocket_base {

  protected Protocol proto;

  protected int state;
  protected CircularQueue<TSocket> acceptQueue;
  protected boolean isWorker;

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
      sendSYN(localPort, remotePort);
      state = SYN_SENT;
      while (state != ESTABLISHED) {
        appCV.awaitUninterruptibly();
      }
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void close() {
    lock.lock();
    try {
      if (state == CLOSED) {
        isWorker = true;
        appCV.awaitUninterruptibly();
      }
      switch (state) {
        case ESTABLISHED:
          sendFIN(localPort, remotePort);
          state = FIN_WAIT;
          while (state != CLOSED) {
            appCV.awaitUninterruptibly();
          }
          break;
        case CLOSE_WAIT: {
          if (isWorker) sendFIN(80, remotePort);
          else sendFIN(localPort, remotePort);
          state = CLOSED;
          break;
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
      // dispatched socket does not initiate connection on test
      if (state == CLOSED) { 
        state = ESTABLISHED;
      }

      switch (state) {

        case SYN_SENT: {
          if (rseg.isSyn()) {
            state = ESTABLISHED;
            appCV.signal();
          }
          break;
        }
        case ESTABLISHED:
          if (rseg.isFin()) {
            state = CLOSE_WAIT;
            appCV.signal();
          }
          break;
        case FIN_WAIT:
          if (rseg.isFin()) {
            state = CLOSED;
            appCV.signal();
          }
          break;
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
          if (rseg.isFin()) {
            appCV.signal();
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

  private void sendSYN(int localPort, int remotePort){
    TCPSegment seg = new TCPSegment();
    seg.setSourcePort(localPort);
    seg.setDestinationPort(remotePort);
    seg.setSyn(true);
    network.send(seg);
  }
  
  private void sendSYN_ACK(int localPort, int remotePort, int numSeq, int numAck){
    TCPSegment seg = new TCPSegment();
    seg.setSourcePort(localPort);
    seg.setDestinationPort(remotePort);
    seg.setSyn(true);
    seg.setAck(true);
    seg.setSeqNum(numSeq);
    seg.setAckNum(numAck);
    network.send(seg);
  }
  
  private void sendACK(int localPort, int remotePort, int numACK){
    TCPSegment seg = new TCPSegment();
    seg.setSourcePort(localPort);
    seg.setDestinationPort(remotePort);
    seg.setAck(true);
    seg.setAckNum(numACK);
    network.send(seg);
  }
  
  private void sendPSH(int localPort, int remotePort, int numSeq, byte[] data, int offset, int length){
    TCPSegment seg = new TCPSegment();
    seg.setSourcePort(localPort);
    seg.setDestinationPort(remotePort);
    seg.setPsh(true);
    seg.setSeqNum(numSeq);
    seg.setData(data, offset, length);
    network.send(seg);
  }
  
  private void sendFIN(int localPort, int remotePort){
    TCPSegment seg = new TCPSegment();
    seg.setFin(true);
    seg.setSourcePort(localPort);
    seg.setDestinationPort(remotePort);
    network.send(seg);
  }
}
