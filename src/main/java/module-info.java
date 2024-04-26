module produktiivsustracker.ui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;

    opens produktiivsustracker.server.ui to javafx.fxml;
    exports produktiivsustracker.server.ui;
}