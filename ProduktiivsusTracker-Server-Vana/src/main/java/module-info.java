module produktiivsustracker.server {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.sql;
    requires org.postgresql.jdbc;

    requires org.bouncycastle.provider;

    opens produktiivsustracker.server.ui to javafx.fxml;
    exports produktiivsustracker.server.ui;

    opens produktiivsustracker.server.produktiivsustrackerserver to javafx.fxml;
    exports produktiivsustracker.server.produktiivsustrackerserver;
}