Un canal/buffer de comunicacions es pot modelar com una caixa amb una porta que conté una cua circular.

```java
public class Buffer {
	protected CircularQueue q;
	protected Lock mon;
	protected Condition noBuida, noPlena;

	public Buffer(int capacitat) {
		mon = new ReentrantLock();
		noBuida = mon.newCondition();
		noPlena = mon.newCondition();
		q = new CircularQueue(capacitat);
	}

	public void put(Object e) {
		mon.lock();
		while(q.full()) {
			noPlena.await();
		}
		// Asserció: cua noPlena
		q.put(e);
		// Asserció: cua noBuida
		noBuida.signal();
		mon.unlock();
	}
	
	public Object get() {
		mon.lock();
		while (q.empty()) {
			noBuida.await();
		}
		// Asserció: cua noBuida
		Object tmp = q.set();
		// Asserció: cua noPlena
		noPlena.signal();
		mon.unlock();
		return tmp;
	}
}
```
Recomanat
```java
	try {
		mon.lock();
		// Logica
		return tmp;
	} finally {
		mon.unlock();
	}
```

```java
public class Consumidor implements Runnable {
	protected Buffer buf;

	public Consumidor(Buffer b) {
		buf = b;
	}
	public void run() {
		while (true) {
			Object tmp = buf.get();
			// Processar/consumir tmp
		}
	}
}
```

Programar un monitor ciclic {
amb un únic mètode atura()

- El primer thread que crida atura() es bloqueja
- El segon thread que crida atura() es bloqueja
- El tercer thread desperta als altres dos i continuen tots 3
I així de manera cíclica
}