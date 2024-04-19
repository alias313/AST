/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flux;

import xarxa.Emissor;
import xarxa.EstablirXarxa;
import xarxa.EstablirXarxaDatagrama;
import xarxa.Receptor;


public class ControlFlux {

    public static void main(String[] args) {
        EstablirXarxa xarxa = new EstablirXarxaDatagrama();
        TSocketEnviarControlFlux socketEnviar = 
                             new TSocketEnviarControlFlux(xarxa.getExtrem(0));
        TSocketRebreControlFlux socketRebre = 
                             new TSocketRebreControlFlux(xarxa.getExtrem(1));    
        new Thread(new Receptor(socketRebre)).start();
        new Thread(new Emissor(socketEnviar)).start();
    }
}









