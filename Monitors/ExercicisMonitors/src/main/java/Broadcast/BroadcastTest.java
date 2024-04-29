package Broadcast;

import java.util.ArrayList;

/**
 *
 * @author ele
 */
public class BroadcastTest extends Thread {
    public static void main(String[] args) {
        int nombreConsumidors = 7;
        //System.out.println("numViatges: " + numViatges);
        final BufferBroadcastUn monitor = new BufferBroadcastUn(nombreConsumidors);
        ArrayList<Thread> consumidors = new ArrayList<Thread>();
        Thread productor, tmpConsumidor;
        Integer INT = 12;
        Integer INT2 = 25;
        
        productor = new Thread(() -> {
            monitor.putValue(INT);
            monitor.putValue(INT2);
        });
        productor.start();

        for (int i = 0; i < nombreConsumidors; i++) {
            final int I = i;
            tmpConsumidor = new Thread(() -> {monitor.getValue(I); monitor.getValue(I); });
            consumidors.add(i, tmpConsumidor);
            consumidors.get(i).start();
        }           
    }

}
