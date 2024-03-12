package practica2.P1Sync.Monitor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MonitorSync {

    private final int N;
    
    //Completar...

    public MonitorSync(int N) {
        this.N = N;
    }

    public void waitForTurn(int id) {
        throw new RuntimeException("//Completar...");
    }

    public void transferTurn() {
        throw new RuntimeException("//Completar...");
    }
}
