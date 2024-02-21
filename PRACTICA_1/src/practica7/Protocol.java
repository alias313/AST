package practica7;

import util.Protocol_base;
import util.TCPSegment;
import util.SimNet;
import util.TSocket_base;

public class Protocol extends Protocol_base {

  protected Protocol(SimNet network) {
    super(network);
  }

  public void ipInput(TCPSegment segment) {
    throw new RuntimeException("//Completar...");
  }

  protected TSocket_base getMatchingTSocket(int localPort, int remotePort) {
    lk.lock();
    try {
      throw new RuntimeException("//Completar...");
    } finally {
      lk.unlock();
    }
  }

}
