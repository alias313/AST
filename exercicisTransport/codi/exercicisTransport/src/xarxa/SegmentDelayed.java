/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xarxa;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Marcel Fernandez
 */
public class SegmentDelayed extends Segment implements Delayed {
    
    private long startTime;    
    
    public SegmentDelayed(int tipus, int numeroSequencia, Object dades, int delayInMilliseconds){
        super(tipus, numeroSequencia, dades);
        this.startTime = System.currentTimeMillis() + delayInMilliseconds;
    }
   
    
    public SegmentDelayed(int tipus, int numeroSequencia, int finestra, Object dades, int delayInMilliseconds){
        super(tipus, numeroSequencia, finestra, dades);
        this.startTime = System.currentTimeMillis() + delayInMilliseconds;
    }
    
    @Override
    public long getDelay(TimeUnit unit) {
        long diff = startTime - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }
    
    
    @Override
    public int compareTo(Delayed o) {
        if(this.getDelay(TimeUnit.MILLISECONDS) < o.getDelay(TimeUnit.MILLISECONDS)){
            return -1;
        }
        if(this.getDelay(TimeUnit.MILLISECONDS) > o.getDelay(TimeUnit.MILLISECONDS)){
            return 1;
        }
        return 0;
            
    }  
    
    
        public String toString(){
        return new String("tipus: "+ tipus +" numSeq: " + numeroSequencia + 
                          " valor: " + dades + " finestra: " + finestra +
                            " startTime: "+startTime);
    }
}
