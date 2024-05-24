package practica6;

import java.util.Iterator;
import practica1.CircularQ.CircularQueue;
import practica4.Protocol;
import util.Const;
import util.TCPSegment;
import util.TSocket_base;

public class TSocket extends TSocket_base {

    // Sender variables:
    protected int MSS;
    protected int snd_sndNxt;
    protected int snd_rcvNxt;
    protected int snd_rcvWnd;
    protected int snd_cngWnd;
    protected int snd_minWnd;
    protected CircularQueue<TCPSegment> snd_unacknowledged_segs;
    protected boolean zero_wnd_probe_ON;

    // Receiver variables:
    protected int rcv_rcvNxt;
    protected CircularQueue<TCPSegment> rcv_Queue;
    protected int rcv_SegConsumedBytes;

    protected TSocket(Protocol p, int localPort, int remotePort) {
        super(p.getNetwork());
        this.localPort = localPort;
        this.remotePort = remotePort;
        p.addActiveTSocket(this);
        // init sender variables:
        MSS = p.getNetwork().getMTU() - Const.IP_HEADER - Const.TCP_HEADER;
        MSS = 10;
        // init receiver variables:
        rcv_Queue = new CircularQueue<>(Const.RCV_QUEUE_SIZE);
        snd_rcvWnd = Const.RCV_QUEUE_SIZE;
        snd_cngWnd = 3;
        snd_minWnd = Math.min(snd_rcvWnd, snd_cngWnd);
        snd_unacknowledged_segs = new CircularQueue<>(snd_cngWnd);
    }

    // -------------  SENDER PART  ---------------
    @Override
    public void sendData(byte[] data, int offset, int length) {
        lock.lock();
        try {
            int bytesQuedenPerEnviar = length;
            while (bytesQuedenPerEnviar > 0) {
                snd_minWnd = Math.min(snd_rcvWnd, snd_cngWnd);
                while ((snd_sndNxt == snd_rcvNxt + snd_minWnd) && snd_rcvWnd > 0 ||
                zero_wnd_probe_ON || snd_unacknowledged_segs.full()) {
                    //log.printRED("ATUREM FIL");
                    appCV.awaitUninterruptibly();
                }
                if (snd_rcvWnd > 0) {
                    int numBytesAPosarSegment = Math.min(MSS, bytesQuedenPerEnviar);
                    TCPSegment seg = segmentize(data, offset, numBytesAPosarSegment);
                    network.send(seg);
                    snd_unacknowledged_segs.put(seg);
                    snd_sndNxt++;
                    bytesQuedenPerEnviar -= numBytesAPosarSegment;
                    offset += numBytesAPosarSegment;
                } else {
                    int numBytesAPosarSegment = 1;
                    TCPSegment seg = segmentize(data, offset, numBytesAPosarSegment);
                    snd_unacknowledged_segs.put(seg);
                    log.printBLACK("----- zero-window probe ON -----");
                    zero_wnd_probe_ON = true;
                    bytesQuedenPerEnviar -= numBytesAPosarSegment;
                    offset += numBytesAPosarSegment;
                    snd_sndNxt++;
                }
                startRTO();
            }
        } finally {
            lock.unlock();
        }
    }

    protected TCPSegment segmentize(byte[] data, int offset, int length) {
        TCPSegment seg = new TCPSegment();
        byte[] finalData = new byte[length];

        System.arraycopy(data, offset, finalData, 0, length);
        seg.setData(finalData);
        seg.setPsh(true);
        seg.setSeqNum(snd_sndNxt);
        seg.setDestinationPort(remotePort);
        seg.setSourcePort(localPort);
        return seg;
    }

    @Override
    protected void timeout() {
        lock.lock();
        try {
            if (snd_unacknowledged_segs != null) {
                //log.printGREEN("TIMEOUT");
                if (zero_wnd_probe_ON) {
                    TCPSegment tmp = snd_unacknowledged_segs.peekFirst();
                    //log.printBLUE("Espai lliure finestra: " + snd_rcvWnd);
                    log.printPURPLE("0-wnd probe: " + tmp);
                    network.send(tmp);
                } else {
                    //log.printBLUE("Espai lliure finestra: " + snd_rcvWnd);
                    Iterator<TCPSegment> it = snd_unacknowledged_segs.iterator();
                    while (it.hasNext()) {
                        TCPSegment seg = it.next();
                        log.printPURPLE("retrans: " + seg.toString());
                        network.send(seg);
                    }
                }
                startRTO();
                appCV.signal();
              }
        } finally {
            lock.unlock();
        }
    }

    // -------------  RECEIVER PART  ---------------
    @Override
    public int receiveData(byte[] buf, int offset, int maxlen) {
        lock.lock();
        try {
            while (rcv_Queue.empty()) {
                log.printGREEN("Cua buida");
                appCV.awaitUninterruptibly();
            }
            int totalbytes = 0;
            int maximBytes = maxlen;
            while (totalbytes < maxlen && !rcv_Queue.empty()) {
                int agafats = consumeSegment(buf, offset, maximBytes);
                totalbytes += agafats;
                offset += agafats;
                maximBytes -= agafats;
            }
            return totalbytes;
        } finally {
            lock.unlock();
        }
    }

    protected int consumeSegment(byte[] buf, int offset, int length) {
        TCPSegment seg = rcv_Queue.peekFirst();
        int a_agafar = Math.min(length, seg.getDataLength() - rcv_SegConsumedBytes);
        System.arraycopy(seg.getData(), rcv_SegConsumedBytes, buf, offset, a_agafar);
        rcv_SegConsumedBytes += a_agafar;
        if (rcv_SegConsumedBytes == seg.getDataLength()) {
            rcv_Queue.get();
            rcv_SegConsumedBytes = 0;
        }
        return a_agafar;
    }

    protected void sendAck() {
        TCPSegment ack = new TCPSegment();
        ack.setAck(true);
        ack.setAckNum(rcv_rcvNxt);
        ack.setDestinationPort(remotePort);
        ack.setSourcePort(localPort);
        ack.setWnd(snd_rcvWnd);
        network.send(ack);
        startRTO();
    }

    // -------------  SEGMENT ARRIVAL  -------------
    public void processReceivedSegment(TCPSegment rseg) {

        lock.lock();
        try {
            if (rseg.isPsh()) {
                // REBEM UN SEGMENT
                //log.printBLUE("Num de segment esperat: " + rcv_rcvNxt);
                if (!rcv_Queue.full()) {
                    if (rseg.getSeqNum() == rcv_rcvNxt) {
                        rcv_Queue.put(rseg);
                        rcv_rcvNxt++;
                        appCV.signal();
                    }
                    sendAck();
                }
                //System.out.println("Tamany finestra després de processar segment: " + snd_rcvWnd);
                appCV.signal();
            } else if (rseg.isAck()) {
                // REBEM UN ACK
                if (snd_unacknowledged_segs.empty()) {
                    // nada que hacer
                } else if (rseg.getAckNum() >= (snd_unacknowledged_segs.peekFirst().getSeqNum() + 1)) {
                    stopRTO();
                    super.printRcvSeg(rseg);
                    snd_rcvWnd = rseg.getWnd();
                    snd_rcvNxt = rseg.getAckNum();
                    Iterator<TCPSegment> it = snd_unacknowledged_segs.iterator();
                    while (it.hasNext()) {
                        TCPSegment tmp = it.next();
                        if (tmp.getSeqNum() <= (rseg.getAckNum() - 1)) {
                            it.remove();
                        }
                    }
                    if (zero_wnd_probe_ON) {
                        log.printBLACK("----- zero-window probe OFF -----");
                        zero_wnd_probe_ON = false;
                    }
                } else {
                    //log.printRED("Num Ack esperat: " + (snd_unacknowledged_segs.peekFirst().getSeqNum() + 1));
                    //log.printRED(rseg.toString());
                }

                unacknowledgedSegments_content();
                appCV.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    private void unacknowledgedSegments_content() {
        Iterator<TCPSegment> ite = snd_unacknowledged_segs.iterator();
        log.printBLACK("\n-------------- content begins  --------------");
        while (ite.hasNext()) {
            log.printBLACK(ite.next().toString());
        }
        log.printBLACK("-------------- content ends    --------------\n");
    }
}
