/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Passatger;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 *
 * @author ele
 */
public class MuntanyaRussa {
    protected Lock mon;
    protected int capacitat, passatgers;
    protected boolean sortint;
    protected Condition ple, aturat;

    public MuntanyaRussa(int capacitat) {
        this.capacitat = capacitat;
    }
    
    public void pujar() {
        mon.lock();
        while (passatgers > capacitat || sortint) {
            ple.awaitUninterruptibly();
        }
        passatgers = passatgers + 1;
        if (passatgers == capacitat) {
            aturat.signal();
        }
        mon.unlock();
    }
    
    public void baixar() {
        mon.lock();
        sortint = true;
        passatgers = passatgers - 1;
        if (passatgers == 0) {
            sortint = false;
            ple.signalAll();
        }
        mon.unlock();
    }
    
    public void arrencar() {
        mon.lock();
        if (passatgers < capacitat) {
            aturat.awaitUninterruptibly();
        }
        mon.unlock();
    }
    
    public void arribada() {
        mon.lock();
        if (sortint) {
            aturat.awaitUninterruptibly();
        }
        mon.unlock();
    }
}
