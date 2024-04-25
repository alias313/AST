package c_flux;

import util.CircularQueue;
import util.Cons;
import util.TCPSegment;
import util.TSocket;
import util.SimNet;

public class TSocketRebre extends TSocket {

  protected int seguentARebre;
  protected CircularQueue<Integer> cuaRecepcio;

  public TSocketRebre(SimNet ch) {
    super(ch);
    cuaRecepcio = new CircularQueue(Cons.MIDA_CUA_RECEPCIO);
  }

  @Override
  public int rebre() {
    int tmp;
    lk.lock();
    try {
      while(cuaRecepcio.empty()){
          appCV.awaitUninterruptibly();
      }
      tmp = cuaRecepcio.get();
      return tmp;
      
    } finally {
      lk.unlock();
    }
    //return tmp;
  }

  @Override
  public void processarSegment(TCPSegment seg) {
    lk.lock();
    try {
      System.out.println("\t\t\t\t\t\treceiver - rebut psh : "+seg.getSeqNum());
      
      cuaRecepcio.put(seg.getData());
      appCV.signal();
      seguentARebre = seg.getSeqNum() + 1;
      sendAck();
      
    } finally {
      lk.unlock();
    }
  }
  
  private void sendAck(){
    TCPSegment seg = new TCPSegment();
    seg.setAck(true);
    seg.setAckNum(seguentARebre);
    seg.setWnd(cuaRecepcio.free());
    network.send(seg);
  }
}
