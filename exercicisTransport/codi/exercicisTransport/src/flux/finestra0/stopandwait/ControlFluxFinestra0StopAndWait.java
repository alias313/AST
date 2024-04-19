/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flux.finestra0.stopandwait;


import xarxa.Emissor;
import xarxa.EstablirXarxa;
import xarxa.EstablirXarxaDatagrama;
import xarxa.Receptor;



public class ControlFluxFinestra0StopAndWait {

    public static void main(String[] args) {
        EstablirXarxa xarxa = new EstablirXarxaDatagrama();
        TSocketEnviarFluxFinestra0StopandWait socketEnviar = 
                             new TSocketEnviarFluxFinestra0StopandWait(xarxa.getExtrem(0));
        TSocketRebreFluxFinestra0StopandWait socketRebre = 
                             new TSocketRebreFluxFinestra0StopandWait(xarxa.getExtrem(1));    
        new Thread(new Receptor(socketRebre)).start();
        new Thread(new Emissor(socketEnviar)).start();
    }
}









