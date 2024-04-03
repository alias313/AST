/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CompteEstalvis;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 *
 * PART B: LES EXTRACCIONS S'HAN DE FER EN ORDRE D'ARRIBADA
 */
public class CompteEstalvis {
    protected int balanc; // Balanç en centims
    protected Lock mon;
    protected Condition potExtreure;
    protected ArrayList<Condition> treure;
    protected ArrayList<Integer> quantitatATreure;
    
    // que incrementa el balanc del compte en quantitat
    public void depositar(int quantitat) {
        mon.lock();
        balanc = balanc + quantitat;
        if (quantitatATreure.isEmpty() && quantitatATreure.get(0) < balanc) {
            treure.get(0).signal();
        }
        mon.unlock();
    }
    
    // que extreu quantitat del balanc del compte.
    // Si el balanc quedes negatiu llavors s'ha d'esperar a poder extreure
    public void extreure(int quantitat) throws InterruptedException {
        mon.lock();
        if (!treure.isEmpty() || balanc < quantitat) {
            Condition cond = mon.newCondition();
            treure.add(cond);
            quantitatATreure.add(quantitat);
            cond.await();
            treure.remove(0);
            quantitatATreure.remove(0);
        }
        balanc = balanc - quantitat;
        if (!treure.isEmpty() && quantitatATreure.get(0) < balanc) {
            treure.get(0).signal();
        }
        mon.unlock();
    }
}
