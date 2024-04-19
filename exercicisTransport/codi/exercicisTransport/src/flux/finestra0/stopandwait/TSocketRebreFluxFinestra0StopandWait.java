package flux.finestra0.stopandwait;

import ast.util.CircularQueue;
import xarxa.Comms;
import xarxa.Segment;
import xarxa.TSocket;
import xarxa.Xarxa;

public class TSocketRebreFluxFinestra0StopandWait extends TSocket {

    protected int seguentARebre;
    protected CircularQueue cuaRecepcio;

    public TSocketRebreFluxFinestra0StopandWait(Xarxa x) {
        super(x);
        cuaRecepcio = new CircularQueue(Comms.MIDA_CUA_RECEPCIO);
    }

    @Override
    public Object rebre() {
        mon.lock();
        try {
            throw new RuntimeException("Part a completar");
            
 
            
            
            
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
            throw new RuntimeException("Part a completar");
            
            
            
            
            
            
            
            
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mon.unlock();
        }
    }
}
