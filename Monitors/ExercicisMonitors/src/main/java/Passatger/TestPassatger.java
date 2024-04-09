/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Passatger;

import java.util.ArrayList;

/**
 *
 * @author ele
 */
public class TestPassatger extends Thread {
    public static void main(String[] args) {
        int numPassatgers = 3600, capacitatVago = 6;
        int numViatges = Math.floorDiv(numPassatgers, capacitatVago);
        //System.out.println("numViatges: " + numViatges);
        final MuntanyaRussa monitor = new MuntanyaRussa(capacitatVago);
        ArrayList<Thread> passatgers = new ArrayList<Thread>();
        ArrayList<Thread> vagons = new ArrayList<Thread>();
        Thread tempPassatger, tempVago;
        
        tempVago = new Thread(() -> {
            for (int i = 0; i < numViatges; i++) {
                monitor.arrencar();  monitor.arribada();
            }
        });
        tempVago.start();

        for (int i = 0; i < numPassatgers; i++) {
            tempPassatger = new Thread(() -> {monitor.pujar();  monitor.baixar();});
            passatgers.add(i, tempPassatger);
            passatgers.get(i).start();
        }           
    }
}
