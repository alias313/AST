/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpVegas;

import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import xarxa.Comms;
import xarxa.Segment;
import xarxa.SegmentDelayed;
import xarxa.TSocket;
import xarxa.Xarxa;


public class TSocketEnviarCongestioEstilVegas extends TSocket{
    protected int seguentEnviar, seguentASerReconegut,
                   finestraCongestio;
    protected HashMap<Integer, Long> tempsEnviament;
    
    
    public TSocketEnviarCongestioEstilVegas(Xarxa x) {
        super(x);
        tempsEnviament = new HashMap();
        finestraCongestio = 4;
                
    }

    public void enviar(Object c) {
        try {
        mon.lock();
        // Per simular diferents retards de segment fer servir:
        // SegmentDelayed segment = new SegmentDelayed(Comms.DADES,seguentEnviar, 0, c, random());
        // tempsEnviament.put(seguentEnviar, System.currentTimeMillis());
        
            throw new RuntimeException("Part a completar");

            
            
            
            
               
        } catch (Exception ex) { System.out.println(ex); }
        finally {
            mon.unlock();
        }
    }

    @Override
    public void processarMissatge(Segment segment) {
        mon.lock();
        try{
            throw new RuntimeException("Part a completar");

            
            
            
        }finally{    
            mon.unlock();
        }
    }
    
    private void actualitzaFinestraCongestio(int numSeq){
            throw new RuntimeException("Part a completar");
            
            
            
            
        
//        System.out.println("Seg : " + numSeq + " enviat : " + tempsSegEnviat +
//                " actual : " + tempsActual + " dif : " + difTemps + 
//                " finestra congestio " + finestraCongestio);    
    }
    
    private int  random(){
            Random r = new Random();
            int inf = Comms.RTT_INF;
            int sup = Comms.RTT_SUP;
            int res = r.nextInt(sup-inf) + inf;
            return res;
    }
}
