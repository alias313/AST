/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flux.finestra0.stopandwait.arq;

import xarxa.Emissor;
import xarxa.EstablirXarxa;
import xarxa.EstablirXarxaDatagrama;
import xarxa.Receptor;


public class ControlFlux_stopandwait_arq {

    public static void main(String[] args) {
        EstablirXarxa xarxa = new EstablirXarxaDatagrama();
        TSocketEnviarControlFluxStopAndWaitARQ socketEnviar = 
                             new TSocketEnviarControlFluxStopAndWaitARQ(xarxa.getExtrem(0));
        TSocketRebreControlFluxStopAndWaitARQ socketRebre = 
                             new TSocketRebreControlFluxStopAndWaitARQ(xarxa.getExtrem(1));    
        new Thread(new Receptor(socketRebre)).start();
        new Thread(new Emissor(socketEnviar)).start();
    }
}









