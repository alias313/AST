/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flux.finestra0;

import xarxa.Emissor;
import xarxa.EstablirXarxa;
import xarxa.EstablirXarxaDatagrama;
import xarxa.Receptor;



public class ControlFluxFinestra0 {

    public static void main(String[] args) {
        EstablirXarxa xarxa = new EstablirXarxaDatagrama();
        TSocketEnviarControlFluxFinestra0 socketEnviar = 
                             new TSocketEnviarControlFluxFinestra0(xarxa.getExtrem(0));
        TSocketRebreControlFluxFinestra0 socketRebre = 
                             new TSocketRebreControlFluxFinestra0(xarxa.getExtrem(1));    
        new Thread(new Receptor(socketRebre)).start();
        new Thread(new Emissor(socketEnviar)).start();
    }
}









