package practica1.CircularQ;

import java.util.Iterator;

public class TestCQ_ite {

    public static void main(String[] args) {

        CircularQueue<Integer> q = new CircularQueue<>(20);
        for (int i = 0; i < 0; i++) {
            q.put(i);
        }
        q.get();
        System.out.println("Queue content: " + q.toString());

        Iterator<Integer> ite = q.iterator();

        System.out.println("we iterate over the queue elements to take 0,4,5 and 9, if present:");
        while (ite.hasNext()) {
            int valor = ite.next();
            System.out.println("Valor: "+ valor);
            if (valor == 0 || valor == 4 || valor == 5 || valor == 9) {
                ite.remove();
                System.out.println("taken: " + valor);
                System.out.println(q.toString());
            }
        }
        System.out.println("present content of the queue: " + q);
        for (int i = 10; i < 15; i++) {
            q.put(i);
        }
        System.out.println("Queue content after put: " + q);
        
        q.get();
        Iterator<Integer> ite1 = q.iterator();
        System.out.println("Queue content after get: " + q);
        while (ite1.hasNext()) {
            int valor = ite1.next();
            System.out.println("Valor: "+ valor);
            if (valor == 13) {
                ite1.remove();
                System.out.println("taken: " + valor);
                System.out.println(q.toString());
            }
        }
    }
}
