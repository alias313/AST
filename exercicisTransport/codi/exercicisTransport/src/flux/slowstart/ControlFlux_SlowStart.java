/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flux.slowstart;

import xarxa.Emissor;
import xarxa.EstablirXarxa;
import xarxa.EstablirXarxaDatagrama;
import xarxa.Receptor;


public class ControlFlux_SlowStart {

    public static void main(String[] args) {
        EstablirXarxa xarxa = new EstablirXarxaDatagrama();
        TSocketEnviarControlFluxSlowStart socketEnviar = 
                             new TSocketEnviarControlFluxSlowStart(xarxa.getExtrem(0));
        TSocketRebreControlFluxSlowStart socketRebre = 
                             new TSocketRebreControlFluxSlowStart(xarxa.getExtrem(1));    
        new Thread(new Receptor(socketRebre)).start();
        new Thread(new Emissor(socketEnviar)).start();
    }
}









