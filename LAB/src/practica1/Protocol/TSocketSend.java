    package practica1.Protocol;

import util.TCPSegment;
import util.TSocket_base;
import util.SimNet;

public class TSocketSend extends TSocket_base {

  public TSocketSend(SimNet network) {
    super(network);
  }

  @Override
  public void sendData(byte[] data, int offset, int length) {
    TCPSegment segment = new TCPSegment();
    segment.setData(data, offset, length);
    segment.setPsh(true);
    super.network.send(segment);
  }
}
