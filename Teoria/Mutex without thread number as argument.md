Assumming you already have ComptadorPositiu
```java
public class MutexComptadorPositiu implements Mutex {
    protected ComptadorPositiu cp;
    
    public MutexComptadorPositiu() {
        cp = new ComptadorPositiu(1);
    }
    
    // threadNumber is either 1 or 2
    public void entrarZC() {
        cp.dec();
    }
    
    public void sortirZC() {
        cp.inc();
    }
}

```