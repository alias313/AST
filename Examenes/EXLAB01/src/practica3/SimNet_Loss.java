package practica3;

import java.util.Random;
import utils.Const;
import utils.Log;
import utils.TCPSegment;

public class SimNet_Loss extends practica2.Protocol.SimNet_Monitor {

    private final double lossRate;
    private final Random rand;
    private final Log log;

    public SimNet_Loss(double lossRate) {
        this.lossRate = lossRate;
        rand = new Random(Const.SEED);
        log = Log.getLog();
    }

    @Override
    public void send(TCPSegment seg) {
        //if (rand.nextDouble() > lossRate) {
            super.send(seg);
        //}
    }

    @Override
    public int getMTU() {
        return Const.MTU_ETHERNET;
    }
}
