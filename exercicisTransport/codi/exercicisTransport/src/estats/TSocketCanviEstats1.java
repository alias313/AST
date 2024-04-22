/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package estats;

import java.util.concurrent.locks.Condition;

import xarxa.Xarxa;


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
    protected Condition esperaRebInici;
    protected Condition esperaRebIniciRecon;
    protected Condition esperaRebRecon;

    public TSocketCanviEstats1(Xarxa x)  {
        super(x);
        estat = ESTAT_TANC;
        esperaRebInici = mon.newCondition();
        esperaRebIniciRecon = mon.newCondition();
        esperaRebRecon = mon.newCondition();
    }

   
    public void inicia(){
        mon.lock();
        try{
            this.enviar(TSocketCanviEstats.INICI);
            estat = TSocketCanviEstats.ESTAT_ENV_INICI;
            while ( != INICI_RECON) {
                esperaRebIniciRecon.awaitUninterruptibly();
            }
            this.enviar(TSocketCanviEstats.RECON);
            estat = TSocketCanviEstats.;
        } catch (Exception ex) { System.out.println(ex); }
        finally{
            mon.unlock();
        }
    }
    

    public void espera(){
        mon.lock();
        try{
            estat = TSocketCanviEstats.ESTAT_ESP;
            int estatRebut = (int) rebre();
            while (estatRebut != INICI) {
                esperaRebInici.await();
            }
            this.enviar(TSocketCanviEstats1.INICI_RECON);
            estat = TSocketCanviEstats.ESTAT_REB_INICI;
            estatRebut = (int) rebre();
            while (estatRebut != RECON) {
                esperaRebRecon.awaitUninterruptibly();
            }
            estat = ESTAT_FINAL;
        } catch (Exception ex) { System.out.println(ex); }
        finally{
            mon.unlock();
        }
    }   

    public void processarMissatge(Object miss){
        mon.lock();
        try {
            throw new RuntimeException("Part a completar");
            
            
            
            
            
            
            
            
        }  finally {
            mon.unlock();
        }
    }
}
