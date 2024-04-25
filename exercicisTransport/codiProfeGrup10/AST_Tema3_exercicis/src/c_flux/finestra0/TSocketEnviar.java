package c_flux.finestra0;

import java.util.Timer;
import util.Cons;
import util.TCPSegment;
import util.TSocket;
import util.SimNet;

public class TSocketEnviar extends TSocket {

  protected int seguentEnviar, seguentASerReconegut, finestraRecepcio;
  protected TCPSegment segmentSondeig;
  protected boolean sondeig_ON;

  public TSocketEnviar(SimNet ch) {
    super(ch);
    finestraRecepcio = Cons.MIDA_CUA_RECEPCIO;
    timer = new Timer();
  }

  @Override
  public void enviar(int data) {
    lk.lock();
    try {
        
        while(((seguentEnviar == seguentASerReconegut + finestraRecepcio) &&
                finestraRecepcio>0) || sondeig_ON){
            appCV.awaitUninterruptibly();
        }
        
        if(finestraRecepcio>0){
            TCPSegment seg = segmentize(data);
            network.send(seg);
            seguentEnviar++;
        }
        else{
            segmentSondeig = segmentize(data);
            seguentEnviar++;
            startRTO();
            sondeig_ON = true;
        }
      
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
      if(sondeig_ON){
          stopRTO();
          sondeig_ON = false;
      }
      seguentASerReconegut = ack.getAckNum();
      finestraRecepcio     = ack.getWnd();
      appCV.signal();
            
    } finally {
      lk.unlock();
    }
  }

  @Override
  protected void timeout() {
    lk.lock();
    try {
      System.out.println("sender - sondeig enviat : " + segmentSondeig.getSeqNum());
      network.send(segmentSondeig);
      startRTO();
    } finally {
      lk.unlock();
    }
  }

}
