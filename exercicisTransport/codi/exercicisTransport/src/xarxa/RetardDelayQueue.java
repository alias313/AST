/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xarxa;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Marcel Fernandez
 */
public class RetardDelayQueue implements Runnable{
    protected DatagramSocket extremA; 
    protected DatagramSocket extremB; 
    protected int portA;
    protected int portB;
    protected InetAddress dir1;
    protected InetAddress dir2;
    protected int port1, port2;
    

    protected BlockingQueue<Delayed> bufferSegments;
    
   
    public RetardDelayQueue(){
        try {
            portA = Comms.PORT_A;
            portB = Comms.PORT_B;
            extremA = new DatagramSocket(portA);
            extremB = new DatagramSocket(portB);
            dir1 = InetAddress.getByName(Comms.HOST_1);
            dir2 = InetAddress.getByName(Comms.HOST_2);
            port1 = Comms.PORT_1;
            port2 = Comms.PORT_2;
            
            bufferSegments = new DelayQueue<>();
        } catch (IOException ex) {
            Logger.getLogger(Retard.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public void run() {
        new Thread(new Reverse(extremB, extremA)).start();
        new Thread(new Delay()).start();
        while (true) {
            Delayed i = (Delayed)CommsDatagrama.rebre(extremA);
//            System.out.println("afegir buffer");            
            bufferSegments.add(i);
        }
    }
    
    class Delay implements Runnable {

        public void run() {
            while (true) {
                try {
                    Object i = (Delayed) bufferSegments.take();

                    CommsDatagrama.enviar(extremB, dir2, Comms.PORT_2, i);
//                                System.out.println("enviat "+i);  
                } catch (InterruptedException ex) {
                    Logger.getLogger(RetardDelayQueue.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    
    class Reverse implements Runnable {

        protected DatagramSocket extremA;
        protected DatagramSocket extremB;

        public Reverse(DatagramSocket disB, DatagramSocket dosA) {
            extremB = disB;
            extremA = dosA;
        }

        public void run() {
            while (true) {
                Object i = CommsDatagrama.rebre(extremB);
                //adormir(500);
                CommsDatagrama.enviar(extremA, dir1, Comms.PORT_1, i);
            }
        }
    }
    
    private void adormir(int t) {

        try {
            Thread.sleep( t);
        } catch (InterruptedException ex) {
            Logger.getLogger(Retard.class.getName()).log(Level.SEVERE, null, ex);
        }

    } 
    

    
}
