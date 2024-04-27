module produktiivsustracker.klient {
    requires javafx.controls;
    requires javafx.fxml;

    opens produktiivsustracker.klient.ui to javafx.fxml;
    exports produktiivsustracker.klient.ui;

    opens produktiivsustracker.klient.produktiivsustrackerklient to javafx.fxml;
    exports produktiivsustracker.klient.produktiivsustrackerklient;
}