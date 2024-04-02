/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package CompteEstalvis;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 *
 * @author ele
 */
public class CompteEstalvis {
    protected int balanç; // Balanç en centims
    protected Lock mon;
    protected Condition potExtreure;
    
    // que incrementa el balanç del compte en quantitat
    public void depositar(int quantitat) {
        mon.lock();
        balanç = balanç + quantitat;
        potExtreure.signalAll();
        mon.unlock();
    }
    
    // que extreu quantitat del balanç del compte.
    // Si el balanç quedes negatiu llavors s'ha d'esperar a poder extreure
    public void extreure(int quantitat) throws InterruptedException {
        mon.lock();
        while (balanç < quantitat) {
            potExtreure.await();
        }
        balanç = balanç - quantitat;
        mon.unlock();
    }
}
