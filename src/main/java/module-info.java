module com.example.produktiivsustracker {
    requires javafx.controls;
    requires javafx.fxml;


    opens produktiivsustracker.ui to javafx.fxml;
    exports produktiivsustracker.ui;
}