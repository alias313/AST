package practica1.Protocol;

import util.TCPSegment;
import util.TSocket_base;
import util.SimNet;

public class TSocketRecv extends TSocket_base {

  public TSocketRecv(SimNet network) {
    super(network);
  }

  @Override
  public int receiveData(byte[] data, int offset, int length) {
    TCPSegment segment = super.network.receive();
    int segmentLength = segment.getDataLength();
    System.arraycopy(segment.getData(), 0, data, offset, segmentLength);
    return segment.getDataLength();
  }
}
