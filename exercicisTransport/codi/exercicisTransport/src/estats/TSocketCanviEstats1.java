/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package estats;

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

    public TSocketCanviEstats1(Xarxa x)  {
        super(x);
        estat = ESTAT_TANC;
    }

   
    public void inicia(){
        mon.lock();
        try{
            throw new RuntimeException("Part a completar");


            
            
            
            
        } catch (Exception ex) { System.out.println(ex); }
        finally{
            mon.unlock();
        }
    }
    

    public void espera(){
        mon.lock();
        try{
            throw new RuntimeException("Part a completar");
            
 
            
            
        
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
