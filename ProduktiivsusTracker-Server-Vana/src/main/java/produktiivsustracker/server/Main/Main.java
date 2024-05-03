package produktiivsustracker.server.Main;
import javafx.application.Application;
import produktiivsustracker.server.db.Andmebaas;
import produktiivsustracker.server.ui.MainUI;

public class Main {
    public static void main(String[] args) {
        Andmebaas.kontrolliDraiver();
        Application.launch(MainUI.class);
    }
}