package Pont;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class Pont {
    protected Lock mon;
    protected Condition cotxePassa, vaixellPassa, deixaSortir;
    protected char estatActual; // 'v' es obert/aixecat (passen vaixells), 'c' es tancat/abaixat (passen cotxes)
    protected int vehiclesEnTransit;
    protected boolean canviant;

    public Pont(char estatInicial) {
        this.mon = new ReentrantLock();
        this.deixaSortir = mon.newCondition();
        this.cotxePassa = mon.newCondition();
        this.vaixellPassa = mon.newCondition();
        this.estatActual = estatInicial;
    }
    public void entrar(char tipus) {
        try {
            mon.lock();
            while (Character.compare(tipus, estatActual) != 0) {
                if (tipus == 'c') {
                    cotxePassa.await();
                }
                else if (tipus == 'v') {
                    vaixellPassa.await();
                }
                else {
                    System.out.println("NO HAS POSSAT EL TIPUS CORRECTE");
                    // throw some exception
                }
            }
            vehiclesEnTransit++;
            if (tipus == 'c') {
                System.out.println("COTXE ENTRA");
            }
            else if (tipus == 'v') {
                System.out.println("VAIXELL ENTRA");
            }
            else {
                System.out.println("NO HAS POSSAT EL TIPUS CORRECTE");
                // throw some exception
            }
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            mon.unlock();
        }
    }

    public void sortir(char tipus) {
        try {
            mon.lock();
            vehiclesEnTransit--;

            if (canviant && vehiclesEnTransit == 0) deixaSortir.signal();
            
            if (tipus == 'c') {
                System.out.println("COTXE SURT");
            }
            else if (tipus == 'v') {
                System.out.println("VAIXELL SURT");
            }
            else {
                System.out.println("NO HAS POSSAT EL TIPUS CORRECTE");
                // throw some exception
            }
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            mon.unlock();
        }
    }

    public void canviar() {
        try {
            mon.lock();
            if (estatActual == 'c') {
                if (vehiclesEnTransit != 0) {
                    System.out.println("COTXES per sortir: " + vehiclesEnTransit);
                    canviant = true;
                    deixaSortir.await();
                }
                canviant = false;
                estatActual = 'v';
                vaixellPassa.signalAll();
                System.out.println("PONT AIXECAT");
            }
            else if (estatActual == 'v') {
                if (vehiclesEnTransit != 0) {
                    System.out.println("VAIXELLS per sortir: " + vehiclesEnTransit);
                    canviant = true;
                    deixaSortir.await();
                }
                canviant = false;
                estatActual = 'c';
                cotxePassa.signalAll();
                System.out.println("PONT ABAIXAT");
            }
            else {
                System.out.println("NO HAS POSSAT EL TIPUS CORRECTE");
                // throw some exception
            }
        } catch (Exception ex) {
            System.out.println(ex);
        } finally {
            mon.unlock();
        }
    }
}
