/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Passatger;

/**
 *
 * @author ele
 */
public class Vago implements Runnable {
    protected MuntanyaRussa atraccio;
    
    public void run() {
        while(true) {
            atraccio.arrencar();
            // Circular
            atraccio.arribada();
        }
    }
}
