package produktiivsustracker.klient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Klient implements AutoCloseable {
    private Socket klient;
    private PrintWriter valjund;
    private BufferedReader sisend;

    public void uhendaServeriga(String ip, int port) throws IOException {
        klient = new Socket(ip, port);
        valjund = new PrintWriter(klient.getOutputStream(), true);
        sisend = new BufferedReader(new InputStreamReader(klient.getInputStream()));
    }

    public String saadaSonum(String sonum) throws IOException{
        valjund.println(sonum);
        String vastus = sisend.readLine();
        return vastus;
    }

    @Override
    public void close() throws IOException {
        sisend.close();
        valjund.close();
        klient.close();
    }
}
