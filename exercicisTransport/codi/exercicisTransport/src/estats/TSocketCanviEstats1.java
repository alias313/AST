/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package estats;

import java.util.concurrent.locks.Condition;

import xarxa.Xarxa;
import xarxa.Comms;
import xarxa.Emissor;
import xarxa.Segment;


/**

                              +-------------+
                              |  ESTAT_TANC |------------
                              +-------------+             \
                         espera()    |                     \
                        ---------    |                      |   inicia()
                                     V                      | ------------
                               +------------+               | envia INICI
                               |  ESTAT_ESP |               |
                               +------------+         +-----------------+
                                     |                | ESTAT_ENV_INICI |
                                     |                +-----------------+
                                     |                      |           
                 reb INICI           |                      |reb INICI_RECON                                   
              -------------------    |                      |----------------       
               envia INICI_RECON     |                      |   envia RECON
                                     |                      |           
                           +-----------------+              |
                           | ESTAT_REB_INICI |              |  
                           +-----------------+              |
                                     |                      |
                  reb RECON          |                      |           
                 -----------         |                      |
                                     V                      /
                              +--------------+             /
                              |  ESTAT_FINAL |<------------
                              +--------------+

 */
public class TSocketCanviEstats1 extends TSocketCanviEstats{

    public TSocketCanviEstats1(Xarxa x)  {
        super(x);
        estat = ESTAT_TANC;
    }

   
    public void inicia(){
        mon.lock();
        try{
            // EVENT inicia, accio envia INICI, canvia a estat ESTAT_ENV_INICI
            Segment segInici = new Segment(INICI, 1, null);
            xarxa.enviar(segInici);
            estat = ESTAT_ENV_INICI;
            System.out.println("S'ha enviat el SYN");

            // EVENT reb INICI_RECOM, accio envia RECON, canvia a estat ESTAT_FINAL
            appCV.awaitUninterruptibly();
            Segment segRecon = new Segment(RECON, 2, null);
            xarxa.enviar(segRecon);
            System.out.println("S'ha rebut l'ACK");

            estat = TSocketCanviEstats.ESTAT_FINAL;

        } catch (Exception ex) { System.out.println(ex); }
        finally{
            mon.unlock();
        }
    }
    

    public void espera(){
        mon.lock();
        try{
            // EVENT espera canvia a estat ESTAT_ESP
            estat = TSocketCanviEstats.ESTAT_ESP;
            appCV.awaitUninterruptibly();
            System.out.println("S'ha rebut el SYN");
            // EVENT reb INICI, accio envia INICI_RECOM, canvia a estat ESTAT_REB_INICI
            Segment segREBInici = new Segment(INICI_RECON, 1, null);
            xarxa.enviar(segREBInici);
            estat = TSocketCanviEstats.ESTAT_REB_INICI;
            System.out.println("S'ha enviat l'ACK");

            // EVENT reb RECON, canvia a estat ESTAT_FINAL
            appCV.awaitUninterruptibly();
            estat = ESTAT_FINAL;

        } catch (Exception ex) { System.out.println(ex); }
        finally{
            mon.unlock();
        }
    }   

    public void processarMissatge(Object miss){
        mon.lock();
        try {
            switch (estat) {
                case ESTAT_ESP: {
                    Segment missAssumedSegment = (Segment) miss;
                    int tipusMiss = missAssumedSegment.getTipus();
                    if (tipusMiss == INICI) {
                        appCV.signal();
                    } else {
                        System.out.println("FAILED AT ESTABLISHING CONNECTION: expected SYN (100) type header but got " + tipusMiss);
                    }
                    break;
                }
                case ESTAT_ENV_INICI: {
                    Segment missAssumedSegment = (Segment) miss;
                    int tipusMiss = missAssumedSegment.getTipus();
                    if (tipusMiss == INICI_RECON) {
                        appCV.signal();
                    } else {
                        System.out.println("FAILED AT ESTABLISHING CONNECTION: expected SYN (100) type header but got " + tipusMiss);
                    }                    break;
                }
                case ESTAT_REB_INICI: {
                    Segment missAssumedSegment = (Segment) miss;
                    int tipusMiss = missAssumedSegment.getTipus();
                    if (tipusMiss == RECON) {
                        appCV.signal();
                    } else {
                        System.out.println("FAILED AT ESTABLISHING CONNECTION: expected SYN (100) type header but got " + tipusMiss);
                    }                    break;
                }
            }
        }  finally {
            mon.unlock();
        }
    }
}
