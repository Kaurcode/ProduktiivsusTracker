package produktiivsustracker.server.ui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.util.function.UnaryOperator;

public class dbUhendamineUI extends Application {
    public static String dbKasutajanimi;
    public static String dbParool;
    public static int dbPort;
    public static String dbNimi;

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


        UnaryOperator<TextFormatter.Change> numbriFilter = muutus -> {
            String uusTekst = muutus.getControlNewText();
            if (uusTekst.matches("\\d*")) {
                return muutus;
            }
            return null;
        };

        Label pordiSilt = new Label("Andmebaasi LocalHost port:");
        TextField pordiVali = new TextField();
        pordiVali.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), null, numbriFilter));  // https://stackoverflow.com/questions/40472668/numeric-textfield-for-integers-in-javafx-8-with-textformatter-and-or-unaryoperat

        Label andmebaasiNimeSilt = new Label("Andmebaasi nimi:");
        TextField andmebaasiNimeVali = new TextField();

        Label[] sildid = new Label[] {kasutajaNimeSilt, parooliSilt, pordiSilt, andmebaasiNimeSilt};
        TextField[] tekstiValjad = new TextField[] {kasutajaNimeVali, parooliVali, pordiVali, andmebaasiNimeVali};

        final int[] fokuseeritudVali = {0};

        EventHandler<KeyEvent> nupuVajutus = keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                if (fokuseeritudVali[0] == 3) liiguEdasi(peaLava, tekstiValjad);
                fokuseeritudVali[0]++;
                fokuseeritudVali[0] = Math.min(fokuseeritudVali[0], 3);
                keyEvent.consume();
            } else if (keyEvent.getCode() == KeyCode.DOWN) {
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

            final int finalElemendiNr = elemendiNr;
            tekstiValjad[elemendiNr].focusedProperty().addListener((observableValue, vanaVaartus, uusVaartus) -> {
                if (uusVaartus) {
                    fokuseeritudVali[0] = finalElemendiNr;
                }
            });
        }

        // Nupp
        Button edasiNupp = new Button("Edasi");
        edasiNupp.setOnMouseClicked(mouseEvent -> liiguEdasi(peaLava, tekstiValjad));

        juur.getChildren().addAll(ankeet, edasiNupp);

        Scene stseen = new Scene(juur);
        stseen.getStylesheets().add("produktiivsustracker/server/ui/Teema.css");

        peaLava.setScene(stseen);
        peaLava.setTitle("Andmebaasi sisselogimine");
        peaLava.show();
    }

    private void liiguEdasi(Stage peaLava, TextField[] tekstiValjad) {
        dbKasutajanimi = tekstiValjad[0].getText();
        dbParool = tekstiValjad[1].getText();
        dbPort = Integer.parseInt(tekstiValjad[2].getText());
        dbNimi = tekstiValjad[3].getText();
        peaLava.close();
    }
}
