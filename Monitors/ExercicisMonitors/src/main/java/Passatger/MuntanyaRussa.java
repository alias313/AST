/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Passatger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author ele
 */
public class MuntanyaRussa {
    protected Lock mon;
    protected int capacitat, passatgers;
    protected final int PUJANT = 10, CIRCULANT = 20, BAIXANT = 30;
    protected int estat = PUJANT;
    protected Condition poderPujar, poderBaixar, poderCircular;

    public MuntanyaRussa(int capacitat) {
        this.capacitat = capacitat;
        mon = new ReentrantLock();
        poderPujar = mon.newCondition();
        poderBaixar = mon.newCondition();
        poderCircular = mon.newCondition();
    }
    
    public void pujar() {
        mon.lock();

        while (estat != PUJANT) {
            poderPujar.awaitUninterruptibly();
        }
        passatgers = passatgers+1;
        System.out.println("PUJA: " + passatgers);
        if (passatgers == capacitat) {
            estat = CIRCULANT;
            System.out.println("VAGO PLE");
            poderCircular.signal();
        }
        mon.unlock();
    }
    
    public void baixar() {
        mon.lock();
        while (estat != BAIXANT) {
            poderBaixar.awaitUninterruptibly();
        }
        System.out.println("BAIXA: " + passatgers);
        passatgers = passatgers-1;
        if (passatgers == 0) {
            estat = PUJANT;
            poderPujar.signalAll();
        }
        mon.unlock();
    }
    
    public void arrencar() {
        mon.lock();
        if (estat != CIRCULANT) {
            poderCircular.awaitUninterruptibly();
        }
        System.out.println("VAGO ESTA CIRCULANT");
        mon.unlock();
    }
    
    public void arribada() {
        mon.lock();
        estat = BAIXANT;
        poderBaixar.signalAll();
        System.out.println("VAGO HA ARRIBAT");
        mon.unlock();
    }
}
