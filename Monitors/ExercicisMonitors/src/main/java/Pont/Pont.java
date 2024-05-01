package Pont;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class Pont {
    protected Lock mon;
    protected Condition cotxePassa, vaixellPassa, deixaSortir;
    protected char estatActual; // 'v' es obert/aixecat (passen vaixells), 'c' es tancat/abaixat (passen cotxes)
    protected int cotxesEntrat, cotxesSortit, vaixellsEntrat, vaixellsSortit;
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
            if (tipus == 'c') {
                cotxesEntrat++;
                System.out.println("COTXE ENTRA");
            }
            else if (tipus == 'v') {
                vaixellsEntrat++;
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
            if (tipus == 'c') {
                cotxesSortit++;
                if (canviant && cotxesEntrat == cotxesSortit) {
                    deixaSortir.signal();
                }
                System.out.println("COTXE SURT");
            }
            else if (tipus == 'v') {
                vaixellsSortit++;
                if (canviant && vaixellsEntrat == vaixellsSortit) {
                    deixaSortir.signal();
                }
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
                if (cotxesEntrat != cotxesSortit) {
                    System.out.println("COTXES: " + cotxesEntrat + " " + cotxesSortit);
                    canviant = true;
                    deixaSortir.await();
                }
                canviant = false;
                estatActual = 'v';
                vaixellPassa.signalAll();
                System.out.println("PONT AIXECAT");
            }
            else if (estatActual == 'v') {
                if (vaixellsEntrat != vaixellsSortit) {
                    System.out.println("VAIXELLS: " + vaixellsEntrat + " " + vaixellsSortit);
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
