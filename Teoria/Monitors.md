Construcció que encapsula una estructura de dades on els mètodes s'executen en exclusió mútua

Per l'espera per condicions es defineixen les "variables de condició" (o salas d'espera) que disposen de 2 mètodes

await(); Atura de manera incondicional el thread que l'invoca i deixa el monitor lliure.
signal(); si hi ha threads aturats a la variable en desperta un si no, no fa res

```java
public class ComptaadorPositiu {
	protected int valor;
	protected Lock mon;
	protected Condition positiu;

	public void inc() {
		mon.lock();
		valor = valor + 1;
		positiu.signal();
		mon.unlock();
	}

	public void dec() {
		mon.lock()
		if (valor == 0) {
			positiu.await();
		}
		valor = valor - 1;
		mon.unlock();
	}
}
```