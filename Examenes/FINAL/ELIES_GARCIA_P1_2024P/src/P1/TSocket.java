package P1;

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
    protected int snd_next_retrans, snd_fin_retrans;

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
        snd_rcvWnd = Const.RCV_QUEUE_SIZE;
        snd_cngWnd = 1;
        snd_minWnd = Math.min(snd_rcvWnd, snd_cngWnd);
        snd_unacknowledged_segs = new CircularQueue(64);

        // init receiver variables:
        rcv_Queue = new CircularQueue<>(Const.RCV_QUEUE_SIZE);
    }

    // -------------  SENDER PART  ---------------
    @Override
    public void sendData(byte[] data, int offset, int length) {
        lock.lock();
        try {
            int enviats = 0;
            while (length > enviats) {
                while (snd_sndNxt - snd_rcvNxt >= snd_minWnd) {
                    appCV.awaitUninterruptibly();
                }
                int a_posar = Math.min(length - enviats, MSS);

                TCPSegment seg = segmentize(data, offset + enviats, a_posar);
                snd_unacknowledged_segs.put(seg);

                network.send(seg);
                if (snd_unacknowledged_segs.size() == 1) {
                    startRTO();
                }
                snd_sndNxt++;
                enviats += a_posar;
            }

        } finally {
            lock.unlock();
        }
    }

    protected TCPSegment segmentize(byte[] data, int offset, int length) {
        TCPSegment seg = new TCPSegment();
        seg.setPsh(true);
        seg.setSourcePort(localPort);
        seg.setDestinationPort(remotePort);
        seg.setSeqNum(snd_sndNxt);
        seg.setData(data, offset, length);
        return seg;
    }

    @Override
    protected void timeout() {
        lock.lock();
        try {
            if (!snd_unacknowledged_segs.empty()) {

                // s'activa el mecanisme slow-start:
                // s'ha de prendre nota dels segments a retransmetre,
                // d'inici només es retransmeten els segments permesos
                // pel mecanisme slow-start.
                
                // En expirar el temporitzador, s’ha d’actualitzar la mida de la finestra de congestio al valor que determina el
                // mecanisme slow-start i s’han de retransmetre tants segments pendents de reconeixement com permeti aquesta
                // finestra

                snd_cngWnd = 1;
                retrans(snd_cngWnd);
            }
        } finally {
            lock.unlock();
        }
    }

    private void retrans(int max_num_retrans) {

        // d'acord amb el maxim de retransmissions permeses (max_num_retrans)
        // s'ha de continuar amb les retransmissions pendents. 
        int retrans_pendents = snd_fin_retrans - snd_next_retrans;
        while (retrans_pendents <= max_num_retrans && snd_next_retrans != snd_fin_retrans) {
            Iterator<TCPSegment> it = snd_unacknowledged_segs.iterator();
            while (it.hasNext()) {
                TCPSegment seg = it.next();
                if (snd_next_retrans < seg.getSeqNum()) {
                    network.send(seg);
                    retrans_pendents++;
                    log.printPURPLE("retrans: " + seg.toString());
                    log.printRED("snd_next_retrans: " + snd_next_retrans);
                    log.printRED("seg.getSeqNum: " + seg.getSeqNum());
                } else {
                    it.remove();
                    log.printRED("remove: " + seg.toString());

                }
            }
            log.printRED("snd_next_retrans: " + snd_next_retrans);
            log.printRED("snd_fin_retrans: " + snd_fin_retrans);

            log.printRED("retrans_pendents: " + retrans_pendents);
            log.printRED("max_num_retrans: " + max_num_retrans);

        }
    }

    // -------------  RECEIVER PART  ---------------
    @Override
    public int receiveData(byte[] buf, int offset, int maxlen) {
        lock.lock();
        try {
            while (rcv_Queue.empty()) {
                appCV.awaitUninterruptibly();
            }
            int agafats = 0;
            while (maxlen > agafats && !rcv_Queue.empty()) {
                agafats += consumeSegment(buf, offset + agafats, maxlen - agafats);
            }
            return agafats;
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
        ack.setSourcePort(localPort);
        ack.setDestinationPort(remotePort);
        ack.setAckNum(rcv_rcvNxt);
        ack.setWnd(rcv_Queue.free());
        network.send(ack);
    }

    // -------------  SEGMENT ARRIVAL  -------------
    @Override
    public void processReceivedSegment(TCPSegment rseg) {

        lock.lock();
        try {

            printRcvSeg(rseg);

            if (rseg.isAck() && rseg.getAckNum() > snd_rcvNxt) {

                // a mes a mes de les accions pertinents, s’actualitza el valor de la mida de la finestra
                // de congestio segons especifica el mecanisme slow-start

                snd_next_retrans = rseg.getAckNum();
                snd_fin_retrans = snd_unacknowledged_segs.size() + 1;

                // Si hi ha segments pendents de retransmissio a la cua
                // snd unacknowledged segs, se n’han de retransmetre tants com aquest ACK habilita.
                if (!snd_unacknowledged_segs.empty()) {
                    System.out.println(snd_unacknowledged_segs.toString());
                    retrans(rseg.getAckNum());
                }

                snd_cngWnd *= 2;

            }
            if (rseg.isPsh() && !rcv_Queue.full()) {

                if (rseg.getSeqNum() == rcv_rcvNxt) {
                    rcv_Queue.put(rseg);
                    rcv_rcvNxt++;
                    appCV.signal();
                }

                sendAck();

            }

        } finally {
            lock.unlock();
        }
    }

}
