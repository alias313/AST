package labCon;

import practica1.CircularQ.CircularQueue;
import util.Const;
import util.TCPSegment;
import util.TSocket_base;


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
      throw new RuntimeException("//Completar...");
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
            throw new RuntimeException("//Completar...");
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

}
