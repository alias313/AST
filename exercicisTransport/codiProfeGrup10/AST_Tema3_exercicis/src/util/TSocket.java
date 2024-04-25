package util;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TSocket {

  protected SimNet network;

  protected ReentrantLock lk;
  protected Condition appCV;

  protected Timer timer;
  protected TimerTask tascaTimer;

  public TSocket(SimNet net) {
    this.network = net;
    lk = new ReentrantLock();
    appCV = lk.newCondition();
    new Thread(new PartReceptora()).start();
  }
    
  public void inicia(){throw new RuntimeException("a completar");}
  public void espera(){throw new RuntimeException("a completar");}
  public void tanca(){throw new RuntimeException("a completar");}
  public void enviar(int val){throw new RuntimeException("a completar");}
  public int  rebre(){throw new RuntimeException("a completar");}
  public void processarSegment(TCPSegment seg){throw new RuntimeException("a completar");}
  
  protected void timeout() {throw new RuntimeException("a completar");}
  protected void timeout(TCPSegment seg) {throw new RuntimeException("a completar");}

  class PartReceptora implements Runnable {

    public void run() {
      while (true) {
        TCPSegment seg = network.receive();
        processarSegment(seg);
      }
    }
  }
    
  protected void startRTO() {
    if (tascaTimer != null) {
      tascaTimer.cancel();
    }
    tascaTimer = new TimerTask() {
      @Override
      public void run() {
        timeout();
      }
    };
    timer.schedule(tascaTimer, Cons.RTO);
  }

  protected void stopRTO() {
    if (tascaTimer != null) {
      tascaTimer.cancel();
    }
    tascaTimer = null;
  }
  
  protected TimerTask startRTO(TCPSegment seg) {
    
    class myTimerTask extends TimerTask{
      TCPSegment seg;
      myTimerTask(TCPSegment seg){
        this.seg = seg;
      }
      @Override
      public void run() {
        timeout(seg);
      }
    }
    
    TimerTask tascaTimer = new myTimerTask(seg);
    timer.schedule(tascaTimer, Cons.RTO);
    return tascaTimer;
  }
  protected void stopRTO(TimerTask tascaTimer) {
    if (tascaTimer != null) {
      tascaTimer.cancel();
    }
  }
    
}
