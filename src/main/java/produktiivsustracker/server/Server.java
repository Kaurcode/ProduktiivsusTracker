package produktiivsustracker.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements AutoCloseable {
    private ServerSocket server;
    private ArrayList<UhendatudKlient> kliendid;

    public void start(int port) throws IOException {
        kliendid = new ArrayList<UhendatudKlient>();

        server = new ServerSocket(port);
        UhendatudKlient klient;

        while (true) {
            klient = new UhendatudKlient(server.accept());
            kliendid.add(klient);
            klient.start();
        }
    }

    @Override
    public void close() throws IOException {
        for (UhendatudKlient klient : kliendid) {
            try {
                klient.close();
            } catch (IOException viga) {
                System.out.println("Kliendi sulgemisel tekkis viga: " + viga.getMessage());
            }
        }
        server.close();
    }
}
