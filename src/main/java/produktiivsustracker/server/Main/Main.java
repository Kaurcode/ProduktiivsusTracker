package produktiivsustracker.server.Main;
import javafx.application.Application;
import produktiivsustracker.server.db.*;
import produktiivsustracker.server.db.Andmebaas;
import produktiivsustracker.server.ui.dbUhendamineUI;
import produktiivsustracker.server.ui.*;

public class Main {
    public static void main(String[] args) {
        Andmebaas.kontrolliDraiver();
        Application.launch(dbUhendamineUI.class);
        try (Andmebaas andmebaas = new Andmebaas(dbUhendamineUI.dbKasutajanimi,
                dbUhendamineUI.dbParool,
                dbUhendamineUI.dbPort,
                dbUhendamineUI.dbNimi)) {
            //TODO
        }
    }
}