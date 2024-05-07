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
        if (Math.random() < lossRate) {
            if (seg.getDataLength() % 2 == 0) {
                byte[] dades = new byte[seg.getDataLength() + 1];
                System.arraycopy(seg.getData(), 0, dades, 0, seg.getDataLength() / 2);
                dades[seg.getDataLength() / 2] = (byte) rand.nextInt();
                System.arraycopy(seg.getData(), seg.getDataLength() / 2, dades, seg.getDataLength() / 2 + 1, seg.getDataLength() / 2);
                seg.setData(dades);
            } else {
                seg.getData()[seg.getDataLength() / 2] = (byte) rand.nextInt();
            }
            log.printRED("\t\t +++++++++ SEGMENT MODIFICAT: " + seg + " +++++++++\n");
        }
        super.send(seg);
    }

    @Override
    public int getMTU() {
        return Const.MTU_ETHERNET;
    }
}
