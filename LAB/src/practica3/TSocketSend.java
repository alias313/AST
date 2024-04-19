package practica3;

import util.Const;
import util.TCPSegment;
import util.TSocket_base;
import util.SimNet;

public class TSocketSend extends TSocket_base {

  protected int MSS;       // Maximum Segment Size

  public TSocketSend(SimNet network) {
    super(network);
    MSS = network.getMTU() - Const.IP_HEADER - Const.TCP_HEADER;
  }

  @Override
  public void sendData(byte[] data, int offset, int length) {
    TCPSegment segment = new TCPSegment();
    int segmentNumber = 0;
    while (length > MSS) {
        segment.setData(data, offset + segmentNumber*MSS, MSS);
        segment.setPsh(true);
        network.send(segment);
    }
        
        segment.setData(data, offset+segmentNumber*MSS, length);
        segment.setPsh(true);
        network.send(segment);

  }

  protected TCPSegment segmentize(byte[] data, int offset, int length) {
    // setData ya copia el array y no pasa por referencia el array
    // asi que no tendrás el error de escribir mientras lees, porque
    // es una red simulada
    return null;
  }

}
