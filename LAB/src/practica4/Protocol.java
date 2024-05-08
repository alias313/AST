package practica4;

import util.Protocol_base;
import util.TCPSegment;
import util.SimNet;
import util.TSocket_base;

public class Protocol extends Protocol_base {

    public Protocol(SimNet network) {
      super(network);
    }

    protected void ipInput(TCPSegment seg) {
        TSocket_base socketOut = getMatchingTSocket(seg.getDestinationPort(), seg.getSourcePort());

        if (socketOut != null) {
            socketOut.processReceivedSegment(seg);
        }
    }

    protected TSocket_base getMatchingTSocket(int localPort, int remotePort) {
        lk.lock();
        try {
            for (TSocket_base s : activeSockets) {
                if (s.localPort == localPort && s.remotePort == remotePort) {
                    return s;
                }
            }
            return null;
        } finally {
            lk.unlock();
        }
    }
}
