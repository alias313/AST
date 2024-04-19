/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xarxa;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marcel Fernandez
 */
public class Receptor implements Runnable {

    protected TSocket socket;

    public Receptor(TSocket s) {
        socket = s;
    }

    public void run() {
        while (true) {
            adormir(1000);
            Object rebut = socket.rebre();
            System.out.println("++++++++++++Receptor: " + rebut);
        }
    }

    private void adormir(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            Logger.getLogger(TSocket.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
    }
}
