/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package estats;


import xarxa.Xarxa;

public abstract class TSocketCanviEstats extends TSocket{
    
    public TSocketCanviEstats(Xarxa x){
        super(x);
    }
    
    protected final static int ESTAT_TANC = 0,
                               ESTAT_ESP = 10,
                               ESTAT_ENV_INICI = 20,
                               ESTAT_REB_INICI = 30,
                               ESTAT_FINAL = 40,
                               ESTAT_ESP_RECON = 50,
                               ESTAT_REBUT_RECON = 60;   

    public final static int INICI = 100,
                            INICI_RECON = 200,
                            RECON = 300;
                            
    protected int estat;
    
    public abstract void inicia();
    public abstract void espera();
}
