package produktiivsustracker.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements AutoCloseable {
    private ServerSocket server;
    private Socket klient;
    private BufferedReader sisend;
    private PrintWriter valjund;


    public void start(int port) throws IOException {
        server = new ServerSocket(port);
        klient = server.accept();

        valjund = new PrintWriter(klient.getOutputStream(), true);
        sisend = new BufferedReader(new InputStreamReader(klient.getInputStream()));

        String tervitus = sisend.readLine();

        if (tervitus.contentEquals("Tere server!")) {
            valjund.println("Tere klient!");
            System.out.println(tervitus);
        } else {
            valjund.println("Viga!");
            System.out.println(tervitus);
        }
    }

    @Override
    public void close() throws IOException {
        sisend.close();
        valjund.close();
        klient.close();
        server.close();
    }
}
