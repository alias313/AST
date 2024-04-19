package practica3;

import java.util.Random;
import util.Const;
import util.Log;
import util.TCPSegment;

public class SimNet_Loss extends practica2.Protocol.SimNet_Monitor {

  private double lossRate;
  private Random rand;
  private Log log;

  public SimNet_Loss(double lossRate) {
    this.lossRate = lossRate;
    rand = new Random(Const.SEED);
    log = Log.getLog();
  }

  @Override
  public void send(TCPSegment seg) {
    if (rand.nextDouble() > lossRate) {
        //super.send(seg);
    }
    super.send(seg);
  }

  @Override
  public int getMTU() {
    return util.Const.MTU_ETHERNET;
  }
}
