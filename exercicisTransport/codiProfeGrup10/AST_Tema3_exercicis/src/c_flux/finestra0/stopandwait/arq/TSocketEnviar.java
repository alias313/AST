package c_flux.finestra0.stopandwait.arq;

import java.util.Timer;
import util.Cons;
import util.TCPSegment;
import util.TSocket;
import util.SimNet;

public class TSocketEnviar extends TSocket {

  protected int seguentEnviar, seguentASerReconegut;
  protected int finestraRecepcio, finestraStopAndWait, finestraPermesa;
  protected TCPSegment segmentTimeout;
  protected boolean sondeig_ON;

  public TSocketEnviar(SimNet ch) {
    super(ch);
    finestraRecepcio    = Cons.MIDA_CUA_RECEPCIO;
    finestraStopAndWait = 1;
    finestraPermesa     = Math.min(finestraRecepcio, finestraStopAndWait);
    timer = new Timer();
  }

  @Override
  public void enviar(int data) {
    lk.lock();
    try {
        
        while(((seguentEnviar == seguentASerReconegut + finestraPermesa) &&
                finestraRecepcio>0) || sondeig_ON){
            appCV.awaitUninterruptibly();
        }
        
        segmentTimeout = segmentize(data);
        seguentEnviar++;
        
        if(finestraRecepcio>0){
            network.send(segmentTimeout);   
        }
        else{
            sondeig_ON = true;
        }
        
        startRTO();
      
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
      
      stopRTO();
      
      if(sondeig_ON){
          sondeig_ON = false;
      }
      seguentASerReconegut = ack.getAckNum();
      finestraRecepcio     = ack.getWnd();
      finestraPermesa      = Math.min(finestraRecepcio, finestraStopAndWait);
      appCV.signal();
            
    } finally {
      lk.unlock();
    }
  }

  @Override
  protected void timeout() {
    lk.lock();
    try {
      System.out.println("sender - des de timeout enviat : " + segmentTimeout.getSeqNum());
      network.send(segmentTimeout);
      startRTO();
    } finally {
      lk.unlock();
    }
  }
  
}
