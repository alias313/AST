/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package groupex;

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
        //----------SALA D'ESPERA 1----------
        //Fa esperar als threads que no son l'ultim del bloc (l'enessim)
        //L'ultim thread manda una senyal per despertar n-1 threads
        numRequests++;
        while (numRequests % numThreads != 0) {
            potEsperar.awaitUninterruptibly();
        }
        if (numRequests % numThreads == 0) {
            for(int i = 0; i < numThreads - 1; i++) {
                potEsperar.signal();
            }
        }
        
        //----------SALA D'ESPERA 2----------
        //Si s'esta usant el recurs fa esperar tots els threads
        //quan un bloc d'n threads invoqui exit(), n threads surten d'aquest bucle
        while (usantRecurs) {
            potEntrar.awaitUninterruptibly();
        }
        
        //L'ultim thread en entrar al recurs força a esperar el
        //seguent bloc de threads fins que aquest bloc surti
        numEntrant++;
        if (numEntrant % numThreads == 0) {
            usantRecurs = true;
        }
        System.out.println("He entrado!");
        mon.unlock();
    }
    
    // deixa d'usar el recurs, si es l'ultim permet que altres n threads
    // puguin accedir al recurs
    public void exit() {
        mon.lock();
        // l'ultim thread en sortir desperta un bloc de n threads i a mes
        // els indica que el recurs s'ha deixat d'utilitzar per que entrin
        numSortint++;
        if (numSortint % numThreads == 0) {
            usantRecurs = false;
            for(int i = 0; i < numThreads; i++) {
                potEntrar.signal();
            }
        }
        System.out.println("He salido!");        
        mon.unlock();
    }
}
