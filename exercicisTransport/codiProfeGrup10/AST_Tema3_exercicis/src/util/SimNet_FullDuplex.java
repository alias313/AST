package util;

import java.util.Random;

public class SimNet_FullDuplex {

  protected SimNet_monitor inst_left, inst_right;
  protected Peer left, right;
  protected double lossRatio;	// in [0, 1)
  private Random rand;

  public SimNet_FullDuplex(double lossRatio) {
    this.lossRatio = lossRatio;
    inst_left  = new SimNet_monitor(Cons.SIMNET_QUEUE_SIZE);
    inst_right = new SimNet_monitor(Cons.SIMNET_QUEUE_SIZE);
    left  = new Peer();
    right = new Peer();
    rand = new Random(Cons.SEED);
  }

  public SimNet_FullDuplex() {
    this(0.0);
  }

  public SimNet getSndEnd() {
    return left;
  }

  public SimNet getRcvEnd() {
    return right;
  }

  public class Peer implements SimNet {

    @Override
    public void send(TCPSegment seg) {
      
      if (rand.nextDouble() < lossRatio) {
        try{Thread.sleep(200);}
        catch(Exception e){e.printStackTrace();}
        System.err.println("Segment lost : " + seg);
        return;
      }
      
      if (this == left) {
        inst_right.send(seg);
      } else {
        inst_left.send(seg);
      }
    }

    @Override
    public TCPSegment receive() {

      try {Thread.sleep((int) (Cons.RTT / 5.0));}
      catch (Exception e) {e.printStackTrace();}

      if (this == left) {
        return inst_left.receive();
      } else {
        return inst_right.receive();
      }
    }

    @Override
    public int getMTU() {
      throw new UnsupportedOperationException("Not supported yet.");
    }

  }

}
