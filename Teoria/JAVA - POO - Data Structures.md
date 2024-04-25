Identifiquem objectes sobre els quals podem aplicar operacions.

## Operacions sobre el objecte
- put (afegir)
- get (treure)
- full (està ple)
- empty (està buit)
- numElems

```java
public interface Capsa {
	public void put (Object e);
	public Object get();
	public boolean full();
	public boolean empty();
	public int numElems();
}
```

Les operacions les podem representar de dues maneres:
- Per espai: camp que guarda el valor de l'operació
	- Atributs
- Per temps: rutina que calcula el valor de l'operació
	- Mètodes

- `int[] v;` fa referencia que pot apuntar a un array de enters (ara apunta a null)
- `v = new int[10];` ara apunta a un contenidor tipus array de nombres enters mida 10
Estructures de dades:
- FIFO (First In First Out): queue (cua) - molt utilitzat en comunicacions
- LIFO (Last In First Out): stack (pila) - no és tan d'interés
En FIFO la posició última fa referencia a on va el primer que posi
```java
public class CircularQueue extends Queue {
	protected Object[] espai;
	protected int primer, ultim, _numElems, capacitat; 
	// ara numElems es representa per espai
	// per distingir entre el cas full y emtpy

	public CircularQueue(int N) {
		capacitat = N;
	}

	public int numElems() {
		return _numElems;
	}
	public boolean Full() {
		return numElems == capacitat;
	}
	public void put(Object e) {
		espai[ultim] = e;
		ultim = (ultim+1) % capacitat;
		numElems++;
	}

	public Object get() {
		Object resultat = espai[primer];
		espai[primer] = null; // Optimització si n'hi ha molt's elements
		primer = (primer + 1) % capacitat;
		_numElems--;
		return resultat
	}
	
}
```

```java
class Link { // no es posa publica per posarl-a en el mateix fitxer
	protected Object item;

	protected Link seguent;

	public Link(Object item, Link seguent) {
		this.item = item;
		this.seguent = seguent;
	}

	public void enllaçat(Link l) {
		seguent = l;
	}
}

public class LinkedQueue extends Queue {
	protected Link primer, últim;
	protected int _numElems, capacitat;

	public int numElems();
		return _numElems;
	}

	public boolean full() {
		return false;
	}

	public void put(Object m) {
		Linkt temp = new Link(m, null);

		if (primer == null) {
			primer = temp;
		} else {
			ultim.enllaçar(temp);
		}
		ultim = temp;
		_numElems++;
	}

	public Object get() {
		Object temp = primer.item;
		primer = primer.seguent;
		if (primer == null) { // o _numElems == 1
			ultim = null;
		}
		_numElems--;
		return temp;
	}
}
```

- El cicle de vida d'una variable comença i acaba entre les claus {}
- El cicle de vida d'un objecte comença quan és creat pel constructor i referenciat per una variable i acaba quan es totes les variables que el tenen com a referència deixen de referenciar-lo.