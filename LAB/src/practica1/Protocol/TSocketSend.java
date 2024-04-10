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
    byte[] copyData= new byte[length];
    for (int i = 0; i < length; i++) {
        copyData[i] = data[offset+i];
    }
    segment.setData(copyData);
    segment.setPsh(true);
    super.network.send(segment);
  }
}
