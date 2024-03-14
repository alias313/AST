Es demana programar el monitor Intercanviador amb un únic mètode
a) Hi ha 2 thread
```java
public class Intercanviador {
    protected Lock mon;
    protected int arribats;
    protected Object espai;
    protected Condition segon;
    
    Object intercanvia (Object elem) throws InterruptedException {
        mon.lock();
        Object tmp = null;
        arribats++;
        if (arribats == 1) {
            espai = elem;
            segon.await();
            tmp = espai;
            arribats = 0; // L'ultim que marxa es el primer thread
        } else {
            tmp = espai;
            espai = elem;
            segon.signal();
        }
        mon.unlock();
        return espai;
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
    protected Condition ego;
    
    Object intercanvia (Object elem) throws InterruptedException {
        mon.lock();
        Object tmp = null;
        arribats++;
        if (arribats > 2) {
            ego.await();
        }
        else if (arribats == 1) {
            espai = elem;
            ego.await();
            tmp = espai;
            arribats = 0; // L'ultim que marxa es el primer
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