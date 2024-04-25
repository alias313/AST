Programar un monitor ciclic {
amb un únic mètode atura()

- El primer thread que crida atura() es bloqueja
- El segon thread que crida atura() es bloqueja
- El tercer thread desperta als altres dos i continuen tots 3
I així de manera cíclica
}
a) solució (porta al fracàs)
```java
arribats++;
if (arribats < 3) {
	tres.await();
} else {
	tres.signal();
	tres.signal();
}
```

1r exercici (posar arribat = 0)
```java
arribats++;
if (arribats < 3) {
	tres.await();
	arribats = 0; // el posa l'ultima que marxa
} else {
	tres.signalAll();
}
```

2n exercici (programar amb un while)
```java
```