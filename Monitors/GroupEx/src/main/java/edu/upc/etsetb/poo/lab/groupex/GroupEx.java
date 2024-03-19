/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package edu.upc.etsetb.poo.lab.groupex;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author ele
 */
public class GroupEx {
    protected Lock mon;
    protected Condition potEntrar, potEsperar;
    protected int numRequests, numEntrant, numSortint, numThreads;
    protected boolean usantRecurs;
    
    // n es el nombre de threads que accedeixen al recurs
    public GroupEx(int n) {
        numThreads = n;
        mon = new ReentrantLock();
        potEsperar = mon.newCondition();
        potEntrar = mon.newCondition();
    }
    
    // si el recurs no esta sent usat i es l'ultim thread dels n que demanen accedir
    // el grup d'n threads passara a usar el recurs, si no s'espera
    public void enter() {
        mon.lock();
        numRequests++;
        if (numRequests == numThreads) {
            for(int i = 0; i < numThreads - 1; i++) {
                potEsperar.signal();
            }
            numRequests = 0;
        } else {
            potEsperar.awaitUninterruptibly();
        }

        while (usantRecurs) {
            potEntrar.awaitUninterruptibly();
        }
        numEntrant++;
        if (numEntrant == numThreads) {
            usantRecurs = true;
            numEntrant = 0;
        }
        System.out.println("He entrado!");
        mon.unlock();
    }
    
    // deixa d'usar el recurs, si es l'ultim permet que altres n threads
    // puguin accedir al recurs
    public void exit() {
        mon.lock();
        numSortint++;
        if (numSortint == numThreads) {
            usantRecurs = false;
            for(int i = 0; i < numThreads; i++) {
                potEntrar.signal();
            }
            numSortint = 0;
        }
        System.out.println("He salido!");        
        mon.unlock();
    }
}
