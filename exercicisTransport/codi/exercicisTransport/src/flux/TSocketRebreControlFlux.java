package flux;

import ast.util.CircularQueue;
import xarxa.Comms;
import xarxa.Segment;
import xarxa.TSocket;
import xarxa.Xarxa;

public class TSocketRebreControlFlux extends TSocket {

    protected int seguentARebre;
    protected CircularQueue<Object> cuaRecepcio;

    public TSocketRebreControlFlux(Xarxa x) {
        super(x);
        cuaRecepcio = new CircularQueue<Object>(Comms.MIDA_CUA_RECEPCIO);
    }

    @Override
    public Object rebre() {
        Object tmp;
        mon.lock();
        try {
            while(cuaRecepcio.empty()){
                appCV.awaitUninterruptibly();
            }
            tmp = cuaRecepcio.get();
            return tmp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            mon.unlock();
        }
    }

    @Override
    public void processarMissatge(Segment segment) {
        mon.lock();
        try {
            cuaRecepcio.put(segment.getDades());
            appCV.signal();
            seguentARebre = segment.getNumSeq() + 1;                  
            Segment seg = new Segment(Comms.ACK, seguentARebre, cuaRecepcio.free(), null);
            xarxa.enviar(seg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mon.unlock();
        }
    }
}
