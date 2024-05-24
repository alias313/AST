package practica4;

import java.util.Iterator;
import util.Const;
import util.TCPSegment;
import util.TSocket_base;

public class TSocket extends TSocket_base {

    //sender variable:
    protected int MSS;
    

    //receiver variables:
    protected CircularQueue<TCPSegment> rcvQueue;
    protected int rcvSegConsumedBytes, segmentNumber, rcvNext, lastSeqNum;
    
    
    //Es poden afegir atributs
    protected boolean segmentEquivocat;

    protected TSocket(Protocol p, int localPort, int remotePort) {
        super(p.getNetwork());
        this.localPort = localPort;
        this.remotePort = remotePort;
        p.addActiveTSocket(this);
        MSS = 5;
        rcvQueue = new CircularQueue<>(Const.RCV_QUEUE_SIZE);
        rcvSegConsumedBytes = 0;
    }

    @Override
    public void sendData(byte[] data, int offset, int length) {
        TCPSegment sndSegment = new TCPSegment();
        segmentNumber = 0;
        while (length > MSS) {
            sndSegment = segmentize(data, offset + segmentNumber*MSS, MSS);
            sndSegment.setSourcePort(localPort);
            sndSegment.setDestinationPort(remotePort);  
            network.send(sndSegment);
            length = length - MSS;
            segmentNumber++;
            }
        sndSegment = segmentize(data, offset + segmentNumber*MSS, length);
        sndSegment.setSourcePort(localPort);
        sndSegment.setDestinationPort(remotePort);  
        network.send(sndSegment);    
    }

    protected TCPSegment segmentize(byte[] data, int offset, int length) {
        TCPSegment seg = new TCPSegment();
        seg.setPsh(true);
        seg.setSeqNum(segmentNumber);
        seg.setData(data, offset, length);
        return seg;
    }

    @Override
    public int receiveData(byte[] buf, int offset, int length) {
        lock.lock();
        try {
            int bytesConsumed = 0;
            TCPSegment firstSeg = rcvQueue.peekFirst();
            while (rcvQueue.empty()) {
                appCV.awaitUninterruptibly();
            }
            while (bytesConsumed < length && !rcvQueue.empty()) {
                if (firstSeg.getData()[rcvSegConsumedBytes] != rcvSegConsumedBytes) {
                    segmentEquivocat = true;
                    length = length*2;
                }    
                bytesConsumed += consumeSegment(buf, offset+bytesConsumed, length-bytesConsumed);
            }
            //Si el segment que ha arribat s'ha saltat la sequencia, fer que el receptor conti el doble (per evitar DATA CORRUPTED)

            //Quan el receptor rebi el segment que s'habia sortit de sequencia es descarta a processReceivedSegment

            return bytesConsumed;
        } finally {
            lock.unlock();
        }
        }

    protected int consumeSegment(byte[] buf, int offset, int length) {
        //No es pot modificar
        TCPSegment seg = rcvQueue.peekFirst();
        int a_agafar = Math.min(length, seg.getDataLength() - rcvSegConsumedBytes);
        byte[] bufferCompletat = new byte[length];
        if (segmentEquivocat) {
            a_agafar = length;
            for (int i = rcvSegConsumedBytes; i < length; i++) {
                bufferCompletat[i] = (byte) i;
            } 
        }
        System.arraycopy(bufferCompletat, rcvSegConsumedBytes, buf, offset, a_agafar);

        // prints per saber que hi ha a buf
        System.out.println(a_agafar);
        for (int i = 0; i < a_agafar; i++) {
            System.out.println("Segment consumit: " +  rcvSegConsumedBytes + i + "buf[offset + 1]" + buf[offset + i]);
        }

        rcvSegConsumedBytes += a_agafar;
        if (rcvSegConsumedBytes == seg.getDataLength()) {
            Iterator it = rcvQueue.iterator();
            if (it.hasNext()) {
                it.next();
                it.remove();
            }
            rcvSegConsumedBytes = 0;
        }
        return a_agafar;
    }

    protected void sendAck() {
        TCPSegment seg = new TCPSegment();
        seg.setSourcePort(localPort);
        seg.setDestinationPort(remotePort);
        seg.setAck(true);
        seg.setAckNum(rcvNext);
        seg.setWnd(rcvQueue.free());
        network.send(seg);
    }

    @Override
    public void processReceivedSegment(TCPSegment rseg) {
        lock.lock();
        try {
            if (rseg.isAck()){
                //nothing to be done in this case.
                printRcvSeg(rseg);
            } else if (rseg.getSourcePort() == 0) {
                // descartar el segment
            } else {
                printRcvSeg(rseg);
                rcvQueue.put(rseg);
                appCV.signal();
                rcvNext = rseg.getSeqNum() + 1;
                if (true) { // que passa si l'origen es port 0

                }
                sendAck();
            }
        } finally {
          lock.unlock();
        }
    }

}
