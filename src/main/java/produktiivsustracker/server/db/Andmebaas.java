package produktiivsustracker.server.db;

import java.sql.*;

public class Andmebaas implements AutoCloseable {
    private Connection andmebaas;
    private String andmebaasiNimi;

    public Andmebaas(String kasutajaNimi, String parool, int port, String andmebaasiNimi) {
        this.andmebaasiNimi = andmebaasiNimi;
        andmebaas = looUhendus(kasutajaNimi, parool, port, "postgres");
        looAndmebaas();
        katkestaUhendus();
        andmebaas = looUhendus(kasutajaNimi, parool, port, andmebaasiNimi);
        looKasutajadOlem();
        looUlesandedOlem();
        looPomodorodOlem();
    }

    public static boolean kontrolliDraiver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException viga) {
            System.out.println("PostgreSQL JDBC draiverit ei leitud: " + viga.getMessage());
            System.exit(1);
        }
        return true;
    }

    /**
     * Meetod andmebaasiga ühendamiseks, et andmebaasist andmeid saada
     * @param kasutajaNimi Parameeter kasutaja andmebaasi kasutajanime jaoks, enamasti "postgres"
     * @param parool Parameeter kasutaja andmebaasi parooli jaoks, enamasti "sql"
     * @param port Parameeter andmebaasi localhost pordi määramiseks
     * @param andmebaasiNimi Parameeter andmebaasi nime jaoks, valisime "pomodoro"
     * @return Tagastab ühenduse andmebaasiga
     */
    public Connection looUhendus(String kasutajaNimi, String parool, int port, String andmebaasiNimi) {
        try {
            String url = String.format("jdbc:postgresql://localhost:%d/%s", port, andmebaasiNimi);
            Connection andmebaas = DriverManager.getConnection(url, kasutajaNimi, parool);
            System.out.println("Ühendus loodud");
            return andmebaas;
        } catch (SQLException viga) {
            System.out.println("Andmebaasiga ühendamisel tekkis viga: " + viga.getMessage());
        }
        return null;
    }

    public void katkestaUhendus() {
        try {
            andmebaas.close();
            System.out.println("Ühendus andmebaasiga suletud");
        } catch (SQLException viga) {
            System.out.println("Andmebaasi sulgemisel tekkis viga: " + viga.getMessage());
        }
    }

    @Override
    public void close() {
        katkestaUhendus();
    }

    /**
     * Meetod andmebaasi olemasolu ning toimimise kontrollimiseks
     * @return Töötava andmebaasi puhul tagastab "true"
     */
    public boolean kontrolliAndmebaas() {
        boolean tagastus = false;

        final String kontrolliDB =
                "SELECT 1 FROM pg_database " +
                        "WHERE datname= ?";
        try (PreparedStatement kontrolliDBLause = andmebaas.prepareStatement(kontrolliDB)) {
            kontrolliDBLause.setString(1, andmebaasiNimi);
            try (ResultSet kasDBOlemas = kontrolliDBLause.executeQuery()) {
                tagastus = kasDBOlemas.next();
            }
        } catch (SQLException viga) {
            System.out.println("Andmebaasi kontrollimisel tekkis viga: " + viga.getMessage());
        }

        return tagastus;
    }

    /**
     * Kui andmebaas puudub, siis loob selle andmebaasi.
     */
    public void looAndmebaas() {
        if (kontrolliAndmebaas()) {
            System.out.println("Andmebaas juba olemas!");
            return;
        }
        try (Statement looDBLause = andmebaas.createStatement()) {
            String looDB = String.format("CREATE DATABASE %s", andmebaasiNimi);
            looDBLause.executeUpdate(looDB);
            System.out.println("DB loodud");
        } catch (SQLException viga) {
            System.out.println("Andmebaasi loomisel tekkis viga: " + viga.getMessage());
        }
    }

    /**
     * Meetod andmebaasis kasutajate tabeli loomiseks, kasutusel kui andmebaas puudub.
     */
    public void looKasutajadOlem() {
        final String tabeliNimi = "kasutajad";

        if (kasOlemOlemas(tabeliNimi)) {
            System.out.printf("%s olem juba olemas\n", tabeliNimi);
            return;
        }
        final String looKasutajadOlem =
                "CREATE TABLE " + tabeliNimi + " (" +
                        "kasutaja_id SERIAL PRIMARY KEY NOT NULL UNIQUE," +
                        "nimi VARCHAR(100) NOT NULL UNIQUE" +
                        ");";

        try (Statement looKasutajadOlemLause = andmebaas.createStatement()){
            looKasutajadOlemLause.executeUpdate(looKasutajadOlem);
            System.out.printf("%s olem loodud\n", tabeliNimi);
        } catch (SQLException viga) {
            System.out.printf("%s olemi loomisel tekkis viga: %s\n", tabeliNimi, viga.getMessage());
        }
    }

    /**
     * Neetid abdnebaasu ülesannete tabeli loomiseks, kui see puudub andmebaasist.
     */
    public void looUlesandedOlem() {
        final String tabeliNimi = "ulesanded";

        if (kasOlemOlemas(tabeliNimi)) {
            System.out.printf("%s olem juba olemas\n", tabeliNimi);
            return;
        }

        final String looUlesandedOlem =
                "CREATE TABLE " + tabeliNimi + " (" +
                        "ulesanne_id SERIAL PRIMARY KEY NOT NULL UNIQUE," +
                        "ulesanne_nimi VARCHAR(100) NOT NULL," +
                        "kasutaja_id INT NOT NULL," +
                        "FOREIGN KEY (kasutaja_id) REFERENCES kasutajad(kasutaja_id)," +
                        "CONSTRAINT kasutajal_ainulaadsed_ulesanded UNIQUE (ulesanne_nimi, kasutaja_id)" +
                        ");";

        try (Statement looUlesandedOlemLause = andmebaas.createStatement()) {
            looUlesandedOlemLause.executeUpdate(looUlesandedOlem);
            System.out.printf("%s olem loodud\n", tabeliNimi);
        } catch (SQLException viga) {
            System.out.printf("%s olemi loomisel tekkis viga: %s\n", tabeliNimi, viga.getMessage());
        }
    }

    /**
     * Meetod pomodorode tabeli loomiseks, kui see andmebaasist puudub
     */
    public void looPomodorodOlem() {
        final String tabeliNimi = "pomodorod";

        if (kasOlemOlemas(tabeliNimi)) {
            System.out.printf("%s olem juba olemas\n", tabeliNimi);
            return;
        }

        final String looPomodorodOlem =
                "CREATE TABLE " + tabeliNimi + " (" +
                        "pomodoro_id SERIAL PRIMARY KEY NOT NULL UNIQUE," +
                        "produktiivne_aeg INTERVAL NOT NULL," +
                        "puhke_aeg INTERVAL NOT NULL," +
                        "kordused INT," +
                        "produktiivne_aeg_kokku INTERVAL," +
                        "ulesanne_id INT NOT NULL," +
                        "sisestuse_aeg TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                        "FOREIGN KEY (ulesanne_id) REFERENCES ulesanded(ulesanne_id)" +
                        ");";

        try (Statement looUlesandedOlemLause = andmebaas.createStatement()) {
            looUlesandedOlemLause.executeUpdate(looPomodorodOlem);
            System.out.printf("%s olem loodud\n", tabeliNimi);
        } catch (SQLException viga) {
            System.out.printf("%s olemi loomisel tekkis viga: %s\n", tabeliNimi, viga.getMessage());
        }
    }

    /**
     * Meetod andmebaasi tabeli kontrollimiseks
     * @param olemiNimi Parameetriks soovitud tabeli nimi
     * @return Tagastab "true", kui tabel on andmebaasis olemas
     */
    public boolean kasOlemOlemas(String olemiNimi) {
        try {
            DatabaseMetaData metaAndmed = andmebaas.getMetaData();
            try (ResultSet kasOlemOlemas = metaAndmed.getTables(andmebaasiNimi, "public", olemiNimi, new String[]{"TABLE"})) {
                return kasOlemOlemas.next();
            }
        } catch (SQLException viga) {
            System.out.printf("Viga %s olemi olemasolu kontrollimisel: %s\n", olemiNimi, viga.getMessage());
            return false;
        }
    }
}
