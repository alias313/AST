/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.etsetb.poo.lab.groupex;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 *
 * @author ele
 */
public class Tester extends Thread {
    protected GroupEx monitor;
    
    public Tester (GroupEx mon){
        monitor = mon;
    }
    
    public void run(){
        Random random = new Random();
        int b = random.nextInt(2900) + 100;
        try {
            Thread.sleep(b);
        } catch (InterruptedException ex) {
            Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
        }
        monitor.enter();
        
        int a = random.nextInt(900) + 100;
        try {
            Thread.sleep(a);
        } catch (InterruptedException ex) {
            Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
        }
        monitor.exit();
    }
}
