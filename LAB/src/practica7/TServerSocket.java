package practica7;

import java.util.HashMap;

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

public class TServerSocket extends TSocket_base {

  protected Protocol proto;

  protected int state;
  protected CircularQueue<TSocket> acceptQueue;


  // States of FSM:
  protected final static int  CLOSED      = 0,
                              LISTEN      = 1,
                              SYN_SENT    = 2,
                              ESTABLISHED = 3,
                              FIN_WAIT    = 4,
                              CLOSE_WAIT  = 5;

  protected TServerSocket(Protocol p, int localPort) {
    super(p.getNetwork());
    proto = p;
    this.localPort = localPort;
    state = CLOSED;
    p.addListenTSocket(this);
    listen();
  }

  @Override
  public void listen() {
    lock.lock();
    try {
      acceptQueue = new CircularQueue<>(Const.LISTEN_QUEUE_SIZE);
      state = LISTEN;
      proto.addListenTSocket(this);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public TSocket accept() {
    TSocket sc;
    lock.lock();
    try {
      while (acceptQueue.empty()) {
        appCV.awaitUninterruptibly();
      }
      sc = acceptQueue.get();
      return sc;
    } finally {
      lock.unlock();
    }
  }


  /**
   * Segment arrival.
   *
   */
  public void processReceivedSegment(TCPSegment rseg) {
    lock.lock();
    try {

      printRcvSeg(rseg);

      switch (state) {
        case LISTEN: {
          if (rseg.isSyn()) {
            TSocket dispatchSocket = new TSocket(proto, localPort, rseg.getSourcePort());
            acceptQueue.put(dispatchSocket);
            dispatchSocket.sendSYN();
            dispatchSocket.state = ESTABLISHED;
            // thread continues from accept method
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
    log.printBLACK("\t\t\t\t\t\t\t    rcvd: " + rseg);
  }

  protected void printSndSeg(TCPSegment rseg) {
    log.printBLACK("\t\t\t\t\t\t\t    sent: " + rseg);
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
