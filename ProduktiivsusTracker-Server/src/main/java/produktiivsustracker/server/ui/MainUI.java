package produktiivsustracker.server.ui;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import produktiivsustracker.server.db.Andmebaas;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class MainUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Andmebaas andmebaas;
    private Stage peaLava;

    @Override
    public void start(Stage peaLava) {
        this.peaLava = peaLava;
        peaLava.setScene(dbUhendamineUI());
        peaLava.setTitle("Andmebaasi sisselogimine");
        peaLava.show();
    }

    private Scene dbUhendamineUI() {
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

        Label URLiSilt = new Label("Andmebaasi URL: ");
        TextField URLiVali = new TextField();

        UnaryOperator<TextFormatter.Change> numbriFilter = muutus -> {
            String uusTekst = muutus.getControlNewText();
            if (uusTekst.matches("\\d*")) {
                return muutus;
            }
            return null;
        };

        Label pordiSilt = new Label("Andmebaasi port:");
        TextField pordiVali = new TextField();
        pordiVali.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), null, numbriFilter));  // https://stackoverflow.com/questions/40472668/numeric-textfield-for-integers-in-javafx-8-with-textformatter-and-or-unaryoperat

        Label kasutajaNimeSilt = new Label("Andmebaasi kasutajanimi:");
        TextField kasutajaNimeVali = new TextField();

        Label parooliSilt = new Label("Andmebaasi parool:");
        PasswordField parooliVali = new PasswordField();

        Label andmebaasiNimeSilt = new Label("Andmebaasi nimi:");
        TextField andmebaasiNimeVali = new TextField();

        Label kasKontrolliSilt = new Label("Tee andmebaasi olemasolule kontroll (mittesoovitatav):");
        CheckBox kasKontrolliValik = new CheckBox();
        CheckBox[] valikuKastid = new CheckBox[] {kasKontrolliValik};

        Label[] sildid = new Label[] {URLiSilt, pordiSilt, kasutajaNimeSilt, parooliSilt, andmebaasiNimeSilt, kasKontrolliSilt};
        TextField[] tekstiValjad = new TextField[] {URLiVali, pordiVali, kasutajaNimeVali, parooliVali, andmebaasiNimeVali};
        Control[] valikud = Stream.concat(Arrays.stream(tekstiValjad), Arrays.stream(valikuKastid)).toArray(Control[]::new);

        final int[] fokuseeritudVali = {0};

        EventHandler<KeyEvent> nupuVajutus = keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                if (fokuseeritudVali[0] == tekstiValjad.length - 1) looAndmebaas(tekstiValjad, kasKontrolliValik);
                fokuseeritudVali[0]++;
                fokuseeritudVali[0] = Math.min(fokuseeritudVali[0], tekstiValjad.length - 1);
                keyEvent.consume();
            } else if (keyEvent.getCode() == KeyCode.DOWN) {
                fokuseeritudVali[0]++;
                fokuseeritudVali[0] = Math.min(fokuseeritudVali[0], tekstiValjad.length - 1);
                keyEvent.consume();
            } else if (keyEvent.getCode() == KeyCode.UP) {
                fokuseeritudVali[0]--;
                fokuseeritudVali[0] = Math.max(fokuseeritudVali[0], 0);
                keyEvent.consume();
            }
            tekstiValjad[fokuseeritudVali[0]].requestFocus();
        };

        for (int elemendiNr = 0; elemendiNr < valikud.length; elemendiNr++) {
            ankeet.addRow(elemendiNr, sildid[elemendiNr], valikud[elemendiNr]);
            GridPane.setHgrow(valikud[elemendiNr], Priority.ALWAYS);

            valikud[elemendiNr].setOnKeyPressed(nupuVajutus);

            final int finalElemendiNr = elemendiNr;
            valikud[elemendiNr].focusedProperty().addListener((observableValue, vanaVaartus, uusVaartus) -> {
                if (uusVaartus) {
                    fokuseeritudVali[0] = finalElemendiNr;
                }
            });
        }

        // Nupp
        Button edasiNupp = new Button("Edasi");
        edasiNupp.setOnMouseClicked(mouseEvent -> looAndmebaas(tekstiValjad, kasKontrolliValik));

        juur.getChildren().addAll(ankeet, edasiNupp);

        Scene stseen = new Scene(juur);
        stseen.getStylesheets().add("produktiivsustracker/server/Teema.css");

        return stseen;
    }

    private void looAndmebaas(TextField[] tekstiValjad, CheckBox kasKontrolli) {
        String dbURL = tekstiValjad[0].getText();

        int dbPort;
        try {
            dbPort = Integer.parseInt(tekstiValjad[1].getText());
        } catch (NumberFormatException viga) {
            Stage veateade = new Stage();
            veateade.setScene(vigaUI("Andmebaasi pordi väärtus on ebasobiv. Proovi uuesti!", viga.getMessage(),
                    false));
            veateade.show();
            return;
        }

        String dbKasutajanimi = tekstiValjad[2].getText();
        String dbParool = tekstiValjad[3].getText();
        String dbNimi = tekstiValjad[4].getText();
        boolean kasKontrolliDb = kasKontrolli.isSelected();

        try {
            andmebaas = new Andmebaas(dbURL, dbPort, dbKasutajanimi, dbParool, dbNimi, kasKontrolliDb);
            peaLava.close();
        } catch (SQLException viga) {
            Stage veateade = new Stage();
            veateade.setScene(vigaUI("Andmebaasi loomisel tekkis viga. Proovi uuesti!",
                    viga.getMessage(), false));
            veateade.show();
        }

    }

    public static Scene vigaUI(String paiseTekst, String veateateTekst, boolean fataalne) {
        VBox juur = new VBox();

        juur.setPadding(new Insets(15));
        juur.setSpacing(20);
        juur.setAlignment(Pos.CENTER);

        Label pais = new Label(paiseTekst);
        juur.getChildren().add(pais);

        Label veateade = new Label(veateateTekst);
        juur.getChildren().add(veateade);

        Button nupp = new Button(fataalne ? "Sulge" : "Ok");
        nupp.setOnMouseClicked(mouseEvent -> {
            Stage veaAken = (Stage) nupp.getScene().getWindow();
            veaAken.close();
        });

        EventHandler<KeyEvent> enter = keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                Stage veaAken = (Stage) nupp.getScene().getWindow();
                veaAken.close();
                keyEvent.consume();
            }
        };

        nupp.setOnKeyPressed(enter);

        juur.getChildren().add(nupp);

        Scene stseen = new Scene(juur);
        stseen.getStylesheets().add("produktiivsustracker/server/Teema.css");
        return stseen;
    }
}
