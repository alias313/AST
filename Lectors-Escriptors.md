Tenim uns threads lectors, escriptors i una base de dades.
- Escriptors: modifiquen la base de dades, **cada escriptor té accés exclusiu**
- Lectors: Consulten la base de dades, **hi pot haver varis lectors alhora**
Si cada lector tingués accés exclusiu, llavors la solució seria un buffer, però no és el cas.

La capsa amb una porta es posa fora de la base de dades, no al voltant!
El lector:
```
While(true) {
	adquirir_lectura();
	// Accés
	allibera_lectura();
}
```
S'han de programar quatre funcions: adqLes, adqEsc, alliLec, alliEsc
Aquesta solució té preferencia als lectors (els escriptors es moren de gana, com en la vida real)
```java
class ControlLecEsc {
	protected Lock mon;
	protected Condition pucLec, pucEsc;
	protected int numLec, lectorsEsperant, escritorsEsperant;
	protected boolean escrivint;

	public void adqLec() {
		mon.lock();
		while (escrivint) {
			lectorsEsperant++; // Depen de l'aplicació es pot fer fora
			pucLec.await();
			lectorsEsperant--;
		}
		// Asserció: escrivint == false
		numLec++;
		mon.unlock();
	}

	public void adqEsc() {
		mon.lock();
		while(numLec > 0 || escrivint) {
			escritorsEsperant++;
			pucEsc().await();
			escritorsEsperant--;
		}
		// Asserció: numLec == 0 && !escrivint
		escrivint = true;
		mon.unlock();
	}

	public void alliEsc() {
		mon.lock();
		escrivint = false;
		if (lectorsEsperant > 0) {
			pucLec.signalAll();
		} else if (escritorsEsperant > 0) { // no és necessaria la condició 
										    // perque el signal no té memoria
			pucEsc.signal();
		}
		mon.unlock();
	}

	public void alliLec() {
		mon.lock();
		numLec--;
		if (numLec == 0) {
			pucEsc.signal(); // Si no hi ha ningú esperant no fa res
		}
		mon.unlock();
	}
}
```

Ara no deixem pasar un Lector si n'hi ha un escriptor esperant (preferencia als escriptors)
```java
public void adqLec() {
	mon.lock();
	while (escrivint || escriptorsEsperant) {
		lectorsEsperant++; // Depen de l'aplicació es pot fer fora
		pucLec.await();
		lectorsEsperant--;
	}
	// Asserció: escrivint == false
	numLec++;
	mon.unlock();
}
```

Per arreglar aquest desequilibri, s'afegeix una variable (encara es poden colar lectors fins que es desperti l'últim lector)
```java
protected boolean despertantLectors;

public void alliEsc() {
	mon.lock();
	escrivint = false;
	if (lectorsEsperant > 0) {
		pucLec.signalAll();
		despertantLectors = true;
	} else if (escritorsEsperant > 0) { // no és necessaria la condició 
										// perque el signal no té memoria
		pucEsc.signal();
	}
	mon.unlock();
}

public void adqLec() {
	mon.lock();
	while ((escrivint || escriptorsEsperant) && !despertantLectors) {
		lectorsEsperant++; // Depen de l'aplicació es pot fer fora
		pucLec.await();
		lectorsEsperant--;
	}

	if (lectorsEsperant == 0) {
		despertantLectors = false;
	}
	// Asserció: escrivint == false
	numLec++;
	mon.unlock();
}

public void adqEsc() {
	mon.lock();
	while(numLec > 0 || escrivint || despertantLectors) {
		escritorsEsperant++;
		pucEsc().await();
		escritorsEsperant--;
	}
	// Asserció: numLec == 0 && !escrivint
	escrivint = true;
	mon.unlock();
}

```

Exercici:
- One lane bridge
- Lavabo Unisex
