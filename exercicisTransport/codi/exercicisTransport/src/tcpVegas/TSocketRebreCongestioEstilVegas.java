/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpVegas;

import ast.util.CircularQueue;
import xarxa.Comms;
import xarxa.Segment;
import xarxa.SegmentDelayed;
import xarxa.TSocket;
import xarxa.Xarxa;

public class TSocketRebreCongestioEstilVegas extends TSocket{
    protected CircularQueue cuaRecepcio;

    public TSocketRebreCongestioEstilVegas(Xarxa x) {
        super(x);
        cuaRecepcio = new CircularQueue<>(Comms.MIDA_CUA_RECEPCIO);
    }

    public Object rebre() {
        mon.lock();
        try {
            throw new RuntimeException("Part a completar");

            
            
            
            
        } finally {                   
            mon.unlock();
        }

    }


    public void processarMissatge(Segment missatge) {
        mon.lock();
        try{
            throw new RuntimeException("Part a completar");


            
            
            
            

        } catch (Exception e) { System.out.println(e);
        } finally {
            mon.unlock();
        }
    }
}