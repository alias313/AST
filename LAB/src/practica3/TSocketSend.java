package practica3;

import util.Const;
import util.TCPSegment;
import util.TSocket_base;
import util.SimNet;

public class TSocketSend extends TSocket_base {

  protected int MSS;       // Maximum Segment Size
  protected int segmentNumber;

  public TSocketSend(SimNet network) {
    super(network);
    MSS = network.getMTU() - Const.IP_HEADER - Const.TCP_HEADER;
  }

  @Override
  public void sendData(byte[] data, int offset, int length) {
    TCPSegment segment = new TCPSegment();
    segmentNumber = 0;
    while (length > MSS) {
      segment = segmentize(data, offset + segmentNumber*MSS, MSS);
      network.send(segment);
      length = length - MSS;
      segmentNumber++;
    }
    segment = segmentize(data, offset + segmentNumber*MSS, length);
    network.send(segment);
  }

  protected TCPSegment segmentize(byte[] data, int offset, int length) {
    // Esta funcion era nececaria en versiones anteriores del codigo pero
    // setData ya copia el array y no pasa por referencia el array como antes
    // asi que no tendrás el error de escribir mientras lees, que ocurria porque
    // es una red simulada
    TCPSegment seg = new TCPSegment();
    seg.setPsh(true);
    seg.setSeqNum(segmentNumber);
    seg.setData(data, offset, length);
    return seg;
  }

}
