/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcpVegas;

import xarxa.Emissor;
import xarxa.EstablirXarxaRetardDelayQueue;
import xarxa.Receptor;


public class TCPVegas {

    public static void main(String[] args) {
        EstablirXarxaRetardDelayQueue xarxa = new EstablirXarxaRetardDelayQueue();
        TSocketEnviarCongestioEstilVegas socketEnviar = 
                             new TSocketEnviarCongestioEstilVegas(xarxa.getExtrem(0));
        TSocketRebreCongestioEstilVegas socketRebre = 
                             new TSocketRebreCongestioEstilVegas(xarxa.getExtrem(1));  
        new Thread(new Receptor(socketRebre)).start();
        new Thread(new Emissor(socketEnviar)).start();        
    }
}









