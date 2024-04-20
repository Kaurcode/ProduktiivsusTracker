package produktiivsustracker.ui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage peaLava) {
        VBox juur = new VBox();

        juur.setPadding(new Insets(15));
        juur.setSpacing(20);
        juur.setAlignment(Pos.CENTER);

        juur.setPrefHeight(200);
        juur.setPrefWidth(500);

        // Ankeet

        GridPane ankeet = new GridPane();
        ankeet.setAlignment(Pos.BASELINE_LEFT);
        ankeet.setHgap(20);
        ankeet.setVgap(10);

        Label kasutajaNimeSilt = new Label("Andmebaasi kasutajanimi:");
        TextField kasutajaNimeVali = new TextField();

        Label parooliSilt = new Label("Andmebaasi parool:");
        PasswordField parooliVali = new PasswordField();

        Label pordiSilt = new Label("Andmebaasi LocalHost port:");
        TextField pordiVali = new TextField();

        Label andmebaasiNimeSilt = new Label("Andmebaasi nimi:");
        TextField andmebaasiNimeVali = new TextField();

        Label[] sildid = new Label[] {kasutajaNimeSilt, parooliSilt, pordiSilt, andmebaasiNimeSilt};
        TextField[] tekstiValjad = new TextField[] {kasutajaNimeVali, parooliVali, pordiVali, andmebaasiNimeVali};

        final int[] fokuseeritudVali = {0};

        EventHandler<KeyEvent> nupuVajutus = keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER || keyEvent.getCode() == KeyCode.DOWN) {
                fokuseeritudVali[0]++;
                fokuseeritudVali[0] = Math.min(fokuseeritudVali[0], 3);
                keyEvent.consume();
            } else if (keyEvent.getCode() == KeyCode.UP) {
                fokuseeritudVali[0]--;
                fokuseeritudVali[0] = Math.max(fokuseeritudVali[0], 0);
                keyEvent.consume();
            }
            tekstiValjad[fokuseeritudVali[0]].requestFocus();
        };

        for (int elemendiNr = 0; elemendiNr < tekstiValjad.length; elemendiNr++) {
            ankeet.addRow(elemendiNr, sildid[elemendiNr], tekstiValjad[elemendiNr]);
            GridPane.setHgrow(tekstiValjad[elemendiNr], Priority.ALWAYS);

            tekstiValjad[elemendiNr].setOnKeyPressed(nupuVajutus);
        }

        // Nupp
        Button edasiNupp = new Button("Edasi");

        juur.getChildren().addAll(ankeet, edasiNupp);

        Scene stseen = new Scene(juur);
        stseen.getStylesheets().add("produktiivsustracker/ui/Teema.css");

        peaLava.setScene(stseen);
        peaLava.setTitle("Andmebaasi sisselogimine");
        peaLava.show();
    }
}
