package produktiivsustracker;

import produktiivsustracker.klient.Klient;
import produktiivsustracker.server.Server;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        Thread serverThread = new Thread(() -> {
            try (Server server = new Server()) {
                server.start(42069);
            }
            catch (IOException viga) {
                System.out.println(viga.getMessage());
            }
        });
        serverThread.start();

        try (Klient klient = new Klient();) {
            klient.uhendaServeriga("127.0.0.1", 42069);
            String vastus = klient.saadaSonum("Tere server!");
            System.out.println(vastus);
        }
    }
}
