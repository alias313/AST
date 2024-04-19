/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xarxa;


import java.util.logging.Level;
import java.util.logging.Logger;
import util.LecturaFitxerText;

/**
 *
 * @author Marcel Fernandez
 */
public class Emissor implements Runnable {

    protected TSocket socket;

    public Emissor(TSocket s) {
        socket = s;
    }
    
    public void run() {
        generaEnters();
    }    
    
    private void generaEnters(){
        int num = 0;
        while (num < Comms.MAX_INT_EMISSOR) {
            adormir(500);
            socket.enviar(num);
            System.out.println("-----------------------------num enviat "+num);            
            num = num + 1;
        }        
    }
    
    private void generaCaracters(){
        char lletra = 'A';
        while (lletra < Comms.MAX_INT_EMISSOR) {
            socket.enviar(lletra);
            System.out.println("lletra enviada "+lletra);
            lletra = (char) (lletra + 1);
        }        
    }
    
    private void llegeixFitxer(){
        LecturaFitxerText lectorText = new LecturaFitxerText(Comms.FITXER);
        char llegit = lectorText.llegirCaracter();
        while (llegit != '\0') {
            socket.enviar(llegit);
            llegit = lectorText.llegirCaracter(); 
//        System.out.println("llegit: ");    
        }
        lectorText.tancar();
        System.out.println("Emisor Tancat");        
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