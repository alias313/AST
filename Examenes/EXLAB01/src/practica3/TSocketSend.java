package practica3;

import utils.Const;
import utils.TCPSegment;
import utils.TSocket_base;
import utils.SimNet;

public class TSocketSend extends TSocket_base {

    protected int MSS;       // Maximum Segment Size
    protected int segmentNumber;

    public TSocketSend(SimNet net) {
        super(net);
        //MSS = net.getMTU() - Const.IP_HEADER - Const.TCP_HEADER;
        MSS = 13;
    }

    @Override
    public void sendData(byte[] data, int offset, int length) {
        TCPSegment seg = new TCPSegment();
        segmentNumber = 0;
        while (length > MSS) {
            seg = segmentize(data, offset + segmentNumber*MSS, length);
            network.send(seg);
            length = length - MSS;
            segmentNumber++;
        }
        
        seg = segmentize(data, offset, length);
        network.send(seg);
    }

    protected TCPSegment segmentize(byte[] data, int offset, int length) {
        TCPSegment segment = new TCPSegment();
        segment.setPsh(true);
        segment.setSeqNum(segmentNumber);
        segment.setData(data, offset, length);
        return segment;
    }

}
