package practica3;

import utils.Const;
import utils.TCPSegment;
import utils.TSocket_base;
import utils.SimNet;

public class TSocketSend extends TSocket_base {

    protected int MSS;       // Maximum Segment Size

    public TSocketSend(SimNet net) {
        super(net);
        MSS = net.getMTU() - Const.IP_HEADER - Const.TCP_HEADER;
        MSS = 13;
        System.out.println(MSS / 2 - 1);
        MSS = MSS / 2 - 1;
    }

    @Override
    public void sendData(byte[] data, int offset, int length) {
        int enviat = 0;
        while (length > enviat) {
            int a_enviar = Math.min(length - enviat, MSS);
            TCPSegment seg = segmentize(data, offset + enviat, a_enviar);
            System.out.println("snd --> " + seg);
            network.send(seg);
            enviat += a_enviar;
        }
    }

    protected TCPSegment segmentize(byte[] data, int offset, int length) {
        TCPSegment seg = new TCPSegment();
        seg.setPsh(true);
        byte[] dades = new byte[length*2];
        System.arraycopy(data, offset, dades, 0, length);  
        seg.setData(dades);
        return seg;
    }

}
