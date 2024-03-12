package practica2.Protocol;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import practica1.CircularQ.CircularQueue;
import util.Const;
import util.TCPSegment;
import util.SimNet;

public class SimNet_Monitor implements SimNet {

  protected CircularQueue<TCPSegment> queue;
  //Completar

  public SimNet_Monitor() {
    queue  = new CircularQueue<>(Const.SIMNET_QUEUE_SIZE);
    //Completar
  }

  @Override
  public void send(TCPSegment seg) {
    throw new RuntimeException("//Completar...");
  }

  @Override
  public TCPSegment receive() {
    throw new RuntimeException("//Completar...");
  }

  @Override
  public int getMTU() {
    throw new UnsupportedOperationException("Not supported yet. NO cal completar fins a la pràctica 3...");
  }

}
