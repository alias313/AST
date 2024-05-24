package labCon;

import practica1.CircularQ.CircularQueue;
import util.Const;
import util.TCPSegment;
import util.TSocket_base;


public class TSocket extends TSocket_base {

  protected Protocol proto;

  protected int state;
  protected CircularQueue<TSocket> acceptQueue;
  

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
      throw new RuntimeException("//Completar...");
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
          throw new RuntimeException("//Completar...");
        }
      }
    } finally {
      lock.unlock();
    }
  }
  
    @Override
    protected void timeout() {
        // Completar
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
            throw new RuntimeException("//Completar...");
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
          if (rseg.isFin()) {
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

}
