/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package xarxa;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public abstract class TSocket{
    protected Xarxa xarxa;

    protected ReentrantLock mon;
    protected Condition appCV;

    public TSocket(Xarxa x) {
        xarxa = x;
        mon = new ReentrantLock();
        appCV = mon.newCondition();
        new Thread(new PartReceptora()).start();
    }
    
    public abstract void processarMissatge(Segment miss);

    class PartReceptora implements Runnable {
        public void run() {
            while (true) {
                Segment rebut = null;
                rebut = (Segment)xarxa.rebre();
                processarMissatge(rebut);
            }
        }
    }
    
    public Object rebre(){
        throw new RuntimeException("no implementat");
    }
    
    public void enviar(Object miss){
        throw new RuntimeException("no implementat");
    }
}
