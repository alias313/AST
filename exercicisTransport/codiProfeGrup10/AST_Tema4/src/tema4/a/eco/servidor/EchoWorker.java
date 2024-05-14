package tema4.a.eco.servidor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author juanluis
 */
public class EchoWorker implements Runnable {

    Socket sc;

    public EchoWorker(Socket sc) {
        this.sc = sc;
    }

    public void run() {

        try {

            BufferedReader sc_br = new BufferedReader(new InputStreamReader(sc.getInputStream()));
            PrintWriter sc_pr    = new PrintWriter(sc.getOutputStream(), true);

            while (true) {

                String line = sc_br.readLine();
                sc_pr.println(line);
                if (line.equals("fi")) {
                    break;
                }

            }

            sc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
