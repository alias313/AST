package practica2.P0CZ;

public class TestSum {

    public static void main(String[] args) throws InterruptedException {
        CounterThread thread1 = new CounterThread();
        CounterThread thread2 = new CounterThread();
        
        thread1.start();
        thread2.start();
    }
}
