Es demana programar el monitor Intercanviador amb un únic mètode
a) Hi ha 2 thread
```java
public class Intercanviador {
    protected Lock mon;
    protected int arribats;
    protected Object espai;
    protected Condition segon;
    
    Object intercanviador (Object elem) throws InterruptedException {
        mon.lock();
        Object tmp = null;
        arribats++;
        if (arribats == 1) {
            espai = elem;
            segon.await();
            Object tmp = espai;
            arribats = 0; // L'ultim que marxa es el primer thread
        } else {
            Object tmp = espai;
            espai = elem;
            segon.signal();
        }
        mon.unlock();
        return tmp;
    }
}

```
b) molt més que 2 threads
```java
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.etsetb.poo.lab.intercanviador;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 *
 * @author ele
 */
public class Intercanviador {
    protected Lock mon;
    protected int arribats;
    protected Object espai;
    protected Condition intercanviAcabat;
    
    Object intercanviador (Object elem) throws InterruptedException {
        mon.lock();
        Object tmp = null;

        while (arribats == 2) {
            intercanviAcabat.await();
        }
        if (arribats == 1) {
	        arribats++;
			espai = elem;
			ego.await();
			tmp = espai;
			arribats = 0; // L'ultim que marxa es el primer
			intercanviAcabat.signal();
			intercanviAcabat.signal();
        } else {
			tmp = espai;
			espai = elem;
			ego.signal();
        }
        mon.unlock();
        return espai;
    }
}

```