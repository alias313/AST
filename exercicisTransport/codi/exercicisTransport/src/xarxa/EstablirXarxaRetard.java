/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xarxa;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marcel Fernandez
 */
public class EstablirXarxaRetard {
    protected Xarxa[] extrems;
    
    public EstablirXarxaRetard(){
        
        try {
            extrems = new Xarxa[2];
            extrems[0]=new XarxaDatagrama(Comms.PORT_1, Comms.PORT_A, InetAddress.getByName(Comms.HOST_A));
            extrems[1]=new XarxaDatagrama(Comms.PORT_2, Comms.PORT_B, InetAddress.getByName(Comms.HOST_B));
            new Thread(new Retard()).start();
        } catch (UnknownHostException ex) {
            Logger.getLogger(EstablirXarxa.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Xarxa getExtrem(int extrem){
        return extrems[extrem];
    }
    
    
}