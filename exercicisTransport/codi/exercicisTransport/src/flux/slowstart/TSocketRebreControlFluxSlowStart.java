package flux.slowstart;

import ast.util.CircularQueue;
import xarxa.Comms;
import xarxa.Segment;
import xarxa.TSocket;
import xarxa.Xarxa;

public class TSocketRebreControlFluxSlowStart extends TSocket {

    protected int NextToBeRcv;
    protected CircularQueue cuaRecepcio;

    public TSocketRebreControlFluxSlowStart(Xarxa x) {
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
