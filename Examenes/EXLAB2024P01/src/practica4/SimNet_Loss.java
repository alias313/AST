package practica4;

import java.util.HashMap;
import java.util.Random;
import util.Const;
import util.Log;
import util.TCPSegment;

public class SimNet_Loss extends practica4.SimNet_Monitor {

    private final double lossRate;
    private final Random rand;
    private final Log log;
    //Es poden afegir atributs

    public SimNet_Loss(double lossRate) {
        this.lossRate = lossRate;
        rand = new Random(Const.SEED);
        log = Log.getLog();
    }

    @Override
    public void send(TCPSegment seg) {
        try {
            mon.lock();
            if (rand.nextDouble() > lossRate) {
                super.send(seg);
            } else {
                System.out.println("\t\t +++++++++ SEGMENT MODIFICAT: " + seg.toString() + " +++++++++");
                seg.setSourcePort(0);
                super.send(seg);
            }
            } finally {
            mon.unlock();
        }
    }

    @Override
    public int getMTU() {
        return Const.MTU_ETHERNET;
    }
}
