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
    byte[] segmentData = segment.getData();
    
    for (int i = 0; i < length; i++) {
        if (segmentData.length == i) {
            return i;
        }
        
        data[offset+i] = segmentData[i];
    }
    
    return length;
  }
}
