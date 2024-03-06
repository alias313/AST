Un Thread Observador que observa i compta esdeveniments i un Thread informador que informa dels events ocorreguts.

```java
int comptador = 0; // recurs compartit

Thread Observador {
	while (true) {
		// Esdeveniment ocurregut (o1)
		comptador = comptador + 1; // (o2)
	}
}

Thread Informador {
	while (true) {
		System.out.println("Numero esdeveniments: " + comptador); // (i1)
		comptador = 0; // (i2)
	}
}
```
This code introduces a race condition. També n'hi ha un altra problema:
Traça de execució:
	o1=>o2=>o1=>o2=>i1=>(o1=>o2=>o1=>o2)
	comptador = 2
	num esdeveniments = 5
	El que hi ha entre paréntesis no es compta
Traça de execució deseada:
	i1=>i2=>o1=>o2=>i1=>i2=>o1=>o2=>...

## Estructura general d'un Thread acudit a Zona Crítica
```pseudocode
Thread[i] { // 1<=i<=n
	while(true) {
		// ZONA NO CRÍTICA
		
		
		// PROTOCOL D'ENTRADA A ZONA CRÍTICA
		
		// ZONA CRITICA
		// Accés al recurs compartit
		
		// PROTOCOL DE SORTIDA A ZONA CRÍTICA
		
		// ZONA NO CRÍTICA
		
	}
}
```
Els punts d'entrada i sortida s'anomenen primitives d'exclusió mutua

a) Primera "solució" al problema d'exclusió mútua

```java
boolean ocupat = false; // this is a flag

Thread 1 {
	while(true) {
		while (ocupat) { } // bucle 
		ocupat = true;
		// ZONA CRITICA
		ocupat = false;
	}
}

Thread 2 {
	while(ocupat) { } // bucle
	ocupat = true;
	// ZONA CRITICA
	ocupat = false;
}
```

1) Suposarem que l'accés al recurs compartit es finit (vol dir que surt)
2) El problema del recurs compartit l'hem treslladat a la variable ocupat
b) segona "solució" problema d'exclusió mútua
```java
boolean dinsT1 = false;
boolean dinsT2 = false;

Thread 1 {
	while (true) {
		dinsT1 = true;
		while(dinsT2) {}
		// ZONA CRITICA
		dinsT1 = false;
	}
}

Thread 2 {
	while (true) {
		dinsT2 = true;
		while (dinsT1) {}
		// ZONA CRITICA
		dinsT2 = false;
	}
}
```
This is a deadlock (or livelock, because it's an active wait, it's consuming cpu resources)
En catalá 'enclavament'.

c) Tercera solució al problema d'exclusió mútua
L'últim que intenta entrar s'hauría d'esperar
```java
boolean dinsT1 = false;
boolean dinsT2 = false;
int ultim = 1;

Thread 1 {
	while (true) {
		dinsT1 = true;
		ultim = 1;
		while (dinsT2 && ultim == 1) {}
		// ZONA CRITICA
		dinsT1 = false;
	}
}

Thread 2 {
	while (true) {
		dinsT2 = true;
		ultim = 2;
		while (dinsT1 && ultim == 2) {}
		// ZONA CRITICA
		dinsT2 = false;
	}
}
```
Algorisme de peterson o Algorisme del tie-break
L'orde de les assignacions de les variables d'inici no importa

```java

interface Mutex {
	public void entrarZonaCritica();
	public void sortirZonaCritica();
}

class MutexPeterson implements Mutex {
	// atributs

	public void  entrarZonaCritica() {
		// implementacio
	}

	public void sortirZonaCritica() {
		// implementacio
	}
}

Thread 1 {
	entrarZonaCritica();
	// ZONA CRITICA
	sortirZonaCritica();
}

Thread 2 {
	entrarZonaCritica();
	// ZONA CRITICA
	sortirZonaCritica();
}
```