package d_flux.finestra0.slowstart.arq;

import java.util.Timer;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import util.Cons;
import util.TCPSegment;
import util.TSocket;
import util.SimNet;

public class TSocketEnviar extends TSocket {

    protected int seguentEnviar, seguentASerReconegut;
    protected int finestraRecepcio, finestraCongestio, finestraPermesa;
    protected List<TCPSegment> snd_unacknowledged_segs;
    protected boolean sondeig_ON;

    public TSocketEnviar(SimNet ch) {
        super(ch);
        finestraRecepcio = Cons.MIDA_CUA_RECEPCIO;
        finestraCongestio = 1;
        finestraPermesa = Math.min(finestraRecepcio, finestraCongestio);
        snd_unacknowledged_segs = new ArrayList<>();
        timer = new Timer();
    }

    @Override
    public void enviar(int data) {
        lk.lock();
        try {

            while (((seguentEnviar >= seguentASerReconegut + finestraPermesa)
                    && finestraRecepcio > 0) || sondeig_ON) {
                appCV.awaitUninterruptibly();
            }

            TCPSegment seg = segmentize(data);
            snd_unacknowledged_segs.add(seg);
            seguentEnviar++;

            if (finestraRecepcio > 0) {
                network.send(seg);
            } else {
                sondeig_ON = true;
            }
            if (snd_unacknowledged_segs.size() == 1) {
                startRTO();
            }

        } finally {
            lk.unlock();
        }
    }

    private TCPSegment segmentize(int data) {
        TCPSegment seg = new TCPSegment();
        seg.setPsh(true);
        seg.setSeqNum(seguentEnviar);
        seg.setData(data);
        return seg;
    }

    @Override
    public void processarSegment(TCPSegment ack) {
        lk.lock();
        try {

            if (ack.getAckNum() > seguentASerReconegut) {

                ListIterator<TCPSegment> ite = snd_unacknowledged_segs.listIterator();
                while (ite.hasNext()) {
                    TCPSegment seg = ite.next();
                    if (seg.getSeqNum() < ack.getAckNum()) {
                        ite.remove();
                    }
                }
                
                if(snd_unacknowledged_segs.isEmpty()){
                    stopRTO();
                }
                else {
                    startRTO();
                }

                if (sondeig_ON) {
                    sondeig_ON = false;
                }
                finestraCongestio   += ack.getAckNum() - seguentASerReconegut;
                seguentASerReconegut = ack.getAckNum();
                finestraRecepcio     = ack.getWnd();
                finestraPermesa      = Math.min(finestraRecepcio, finestraCongestio);
                appCV.signal();
            }
            
            System.out.println("\t\t\t\t\t\tsender   - rebut ack -> ack: " 
                    + ack.getAckNum()+ ", wnd: " + ack.getWnd() 
                    + ", cwnd: " + finestraCongestio);

        } finally {
            lk.unlock();
        }
    }

    @Override
    protected void timeout() {
        lk.lock();
        try {
            
            if (!sondeig_ON){
                finestraCongestio = 1;
            }
            
            for (TCPSegment seg : snd_unacknowledged_segs) {
                System.out.println("sender - des de timeout enviat : " + seg.getSeqNum());
                network.send(seg);
            }
            startRTO();
        } finally {
            lk.unlock();
        }
    }

}
