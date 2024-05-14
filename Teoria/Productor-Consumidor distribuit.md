Cada vegada que envías una petició creas una nova connexió
Client:
```java
interface Comms {
	public final static int port = 2222;
	public final static String host = "localhost";
	public final static int PUT = 100;
	public final static int GET = 200;
}

interface Buffer {
	public void put(Object e);
	public Object get();
}

class BufferRepresentant implements Buffer {
	protected Socket s;
	
	public BufferRepresentant() {
	}

	public void put(Object e) {
		enviarPeticio(Comms.PUT, e);
	}

	public Object get() {
		return enviarPeticio(Comms.GET, null);
	}

	protected Object enviarPeticio(int accio, Object e) {
		s = new Socket(Comms.host, Comms.port);
		
		// L'ordre importa
		ObjectOutputStream sortida = new ObjectOutputStrem(s.getOutputStream);
		ObjectInputStream entrada = new ObjectInputStream(s.getInputStream);
		
		sortida.writeInt(accio);
		sortida.writeObject(e);
		// Handle closed connection because of timeout
		Object resposta = entrada.readObject();
		sortida.close();
		entrada.close();
		s.close();
		return resposta;
	}
 }
 
 class Productor implements Runnable {
	protected Buffer buf;
	public Productor(Buffer b) {
		buf = b;
	}

	public void run() {
		for (int i = 0; i < 10; i++) {
			buf.put(i);
		}
	}
	
	public static void main() {
		Buffer b = new BufferRepresentant();
		new Thread(new Productor(b)).start();
	}
}

```
Servidor
```java
class Treballador implements Runnable {
	protected Socket sc;
	protected Buffer b;

	public Treballador(Socket s, Buffer buf) {
		sc = s;
		b = buf;
	}

	public void run() {
		// Al revés de l'ordre a enviarPeticio
		ObjectInputStream entrada = new ObjectInputStream(sc.getInputStream);
		ObjectOutputStream sortida = new ObjectOutputStrem(sc.getOutputStream);

		int accio = entrada.readInt(); // bloquejant, punt de sincronia
		Object param = entrada.readObject();
		Object resultat = null;
		
		switch (accio) {
			case Comms.PUT:
				b.put(param);
				break;
			case Comms.GET:
				resultat = b.get();
				break;
		}
		
		sortida.writeObject(resultat);
		entrada.close()
		sortida.close()
		sc.close()
	}
}

class Servidor {
	protected ServerSocket ss;
	protected buffer b;

	public Servidor(Buffer buf) {
		ss = new ServerSocket(Comms.port);
		b = buf;
	}

	public void dispatch() {
		while(true) {
			Socket sc = ss.accept();
			new Thread(new Treballador(sc, b)).start();
		}
	}

	public static void main() {
		Buffer b = new BufferReal(5);
		new Thread(new Productor(b)).start();
		Servidor S = new Servidor(b);
		s.dispatch();
	}
}
 ```
 Exercici: Modificar aquest codi, mantenint la connexió oberta per cada put
 Exercici: Fer altres patrons i distribuir-los, lectors escriptors distribuit... => lector escriptor representant, groupex representant (proxy
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 )
































































































































































































