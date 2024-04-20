module produktiivsustracker.ui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;

    opens produktiivsustracker.ui to javafx.fxml;
    exports produktiivsustracker.ui;
}