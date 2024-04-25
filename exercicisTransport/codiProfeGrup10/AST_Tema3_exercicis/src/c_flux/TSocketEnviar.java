package c_flux;

import util.Cons;
import util.TCPSegment;
import util.TSocket;
import util.SimNet;

public class TSocketEnviar extends TSocket {

  protected int seguentEnviar, seguentASerReconegut, finestraRecepcio;

  public TSocketEnviar(SimNet ch) {
    super(ch);
    finestraRecepcio = Cons.MIDA_CUA_RECEPCIO;
  }

  @Override
  public void enviar(int data) {
    lk.lock();
    try {
        
        while(seguentEnviar == seguentASerReconegut + finestraRecepcio){
            appCV.awaitUninterruptibly();
        }
      
        TCPSegment seg = segmentize(data);
        network.send(seg);
        seguentEnviar++;
      
    } finally {
      lk.unlock();
    }
  }
  
  private TCPSegment segmentize(int data) {
    TCPSegment seg = new TCPSegment();
    seg.setPsh(true);
    seg.setSeqNum(seguentEnviar);
    seg.setData(data);
    return seg;
  }

  @Override
  public void processarSegment(TCPSegment ack) {
    lk.lock();
    try {
      System.out.println("\t\t\t\t\t\tsender   - rebut ack -> ack: " 
                         + ack.getAckNum()+ ", wnd: " + ack.getWnd());
      
      seguentASerReconegut = ack.getAckNum();
      finestraRecepcio     = ack.getWnd();
      appCV.signal();
            
    } finally {
      lk.unlock();
    }
  }
}
