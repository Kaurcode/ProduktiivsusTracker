import javafx.application.Application;

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
        Application.launch(produktiivsustracker.server.ui.MainUI.class);

        try (Klient klient1 = new Klient(); Klient klient2 = new Klient()) {

            String vastus;

            klient1.uhendaServeriga("127.0.0.1", 42069);
            vastus = klient1.saadaSonum("Tere server!");
            System.out.println("Klient1: " + vastus);

            klient2.uhendaServeriga("127.0.0.1", 42069);
            vastus = klient2.saadaSonum("Tere server!");
            System.out.println("Klient2: " + vastus);

            vastus = klient1.saadaSonum("Katse1");
            System.out.println("Klient1: " + vastus);

            vastus = klient2.saadaSonum("Katse1");
            System.out.println("Klient2: " + vastus);

            vastus = klient2.saadaSonum("EXIT");
            System.out.println("Klient2: " + vastus);

            vastus = klient1.saadaSonum("Minu nimi on Kaur");
            System.out.println(vastus);
            vastus = klient1.saadaSonum("Head aega!");
            System.out.println(vastus);
            vastus = klient1.saadaSonum("EXIT");
            System.out.println(vastus);
        }
    }
}
