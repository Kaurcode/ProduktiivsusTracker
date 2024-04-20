package produktiivsustracker.Main;
import javafx.application.Application;
import produktiivsustracker.db.*;
import produktiivsustracker.ui.*;

public class Main {
    public static void main(String[] args) {
        Andmebaas.kontrolliDraiver();
        Application.launch(dbUhendamineUI.class);
        try (Andmebaas andmebaas = new Andmebaas(dbUhendamineUI.dbKasutajanimi,
                dbUhendamineUI.dbParool,
                dbUhendamineUI.dbPort,
                dbUhendamineUI.dbNimi)) {

        }
    }
}