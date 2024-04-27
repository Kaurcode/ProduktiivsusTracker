package produktiivsustracker.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class UhendatudKlient extends Thread implements AutoCloseable {
    private Socket klient;
    private PrintWriter valjund;
    private BufferedReader sisend;

    public UhendatudKlient(Socket klient) {
        this.klient = klient;
    }

    public void run() {
        try {
            valjund = new PrintWriter(klient.getOutputStream(), true);
            sisend = new BufferedReader(new InputStreamReader(klient.getInputStream()));

            String sonum;
            while ((sonum = sisend.readLine()) != null) {
                if (sonum.contentEquals("EXIT")) {
                    valjund.println("Uhendus katkestatud");
                    break;
                }

                valjund.println(sonum);
            }
        } catch (IOException viga) {
            throw new RuntimeException(viga.getMessage());
        } finally {
            try {
                close();
            } catch (IOException viga) {
                throw new RuntimeException(viga.getMessage());
            }
        }
    }

    @Override
    public void close() throws IOException {
        sisend.close();
        valjund.close();
        klient.close();
    }
}
