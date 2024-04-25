package util;

/**
 *
 * @author juanluis
 */
public interface Cons {
  int SEED              = 3;
  int SIMNET_QUEUE_SIZE = 100;
  int MIDA_CUA_RECEPCIO = 4;
  int TEMPS_ENTRE_ESCR  = 50;
  int TEMPS_ENTRE_LECT  = 2000;
  int RTT               = 2000;
  int RTO               = 2*RTT;
  double LOSS_RATE      = 0.2;
}
