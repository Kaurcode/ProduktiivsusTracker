package produktiivsustracker.server.db;

import java.sql.*;

public class Andmebaas implements AutoCloseable {
    private Connection andmebaas;
    private String andmebaasiNimi;

    public Andmebaas(String URL, int port, String kasutajaNimi, String parool, String andmebaasiNimi,
                     boolean teeKontroll) throws SQLException {

        this.andmebaasiNimi = andmebaasiNimi;

        if (teeKontroll) {
            andmebaas = looUhendus(URL, port, kasutajaNimi, parool, "postgres");
            looAndmebaas();
            katkestaUhendus();
        }

        andmebaas = looUhendus(URL, port, kasutajaNimi, parool, andmebaasiNimi);
        looKasutajadOlem();
        looEesmargidOlem();
        looUlesandedOlem();
        looProduktiivsusAegOlem();
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

    public Connection looUhendus(String URL, int port, String kasutajaNimi, String parool, String andmebaasiNimi) throws SQLException {
        String url = String.format("jdbc:postgresql://%s:%d/%s", URL, port, andmebaasiNimi);
        Connection andmebaas = DriverManager.getConnection(url, kasutajaNimi, parool);
        System.out.println("Ühendus loodud");
        return andmebaas;
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

    public void looKasutajadOlem() {
        final String tabeliNimi = "kasutajad";

        if (kasOlemOlemas(tabeliNimi)) {
            System.out.printf("%s olem juba olemas\n", tabeliNimi);
            return;
        }
        final String looKasutajadOlem =
                "CREATE TABLE " + tabeliNimi + " (" +
                        "kasutaja_id SERIAL PRIMARY KEY NOT NULL UNIQUE," +
                        "nimi VARCHAR(100) NOT NULL UNIQUE," +
                        "parooli_sool VARCHAR(24) NOT NULL," +
                        "parooli_rasi VARCHAR(44) NOT NULL" +
                        ");";

        try (PreparedStatement looKasutajadOlemLause = andmebaas.prepareStatement(looKasutajadOlem)) {
            looKasutajadOlemLause.executeUpdate();
            System.out.printf("%s olem loodud\n", tabeliNimi);
        } catch (SQLException viga) {
            System.out.printf("%s olemi loomisel tekkis viga: %s\n", tabeliNimi, viga.getMessage());
        }
    }

    public void looEesmargidOlem() {
        final String tabeliNimi = "eesmargid";

        if (kasOlemOlemas(tabeliNimi)) {
            System.out.printf("%s olem juba olemas\n", tabeliNimi);
            return;
        }

        final String looEesmargidOlem =
                "CREATE TABLE " + tabeliNimi + " (" +
                        "eesmark_id SERIAL PRIMARY KEY NOT NULL UNIQUE, " +
                        "eesmark_nimi VARCHAR(100) NOT NULL, " +
                        "kasutaja_id INT NOT NULL," +
                        "kas_tehtud BOOLEAN DEFAULT FALSE NOT NULL," +
                        "tahtaeg TIMESTAMP," +
                        "FOREIGH KEY (kasutaja_id) REFERENCES kasutajad(kasutaja_id)," +
                        "CONSTRAINT kasutajal_ainulaadsed_eesmargid UNIQUE (eesmark_nimi, kasutaja_id)" +
                        ");";
        try (PreparedStatement looEesmargidOlemLause = andmebaas.prepareStatement(looEesmargidOlem)) {
            looEesmargidOlemLause.executeUpdate();
            System.out.printf("%s olem loodud\n", tabeliNimi);
        } catch (SQLException viga) {
            System.out.printf("%s olemi loomisel tekkis viga: %s\n", tabeliNimi, viga.getMessage());
        }
    }

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
                        "eesmark_id INT NOT NULL," +
                        "kas_tehtud BOOLEAN DEFAULT FALSE NOT NULL," +
                        "tahtaeg TIMESTAMP, " +
                        "FOREIGN KEY (eesmark_id) REFERENCES eesmargid(eesmark_id)," +
                        "CONSTRAINT kasutajal_ainulaadsed_ulesanded UNIQUE (ulesanne_nimi, eesmark_id)" +
                        ");";

        try (PreparedStatement looUlesandedOlemLause = andmebaas.prepareStatement(looUlesandedOlem)) {
            looUlesandedOlemLause.executeUpdate();
            System.out.printf("%s olem loodud\n", tabeliNimi);
        } catch (SQLException viga) {
            System.out.printf("%s olemi loomisel tekkis viga: %s\n", tabeliNimi, viga.getMessage());
        }
    }

    public void looProduktiivsusAegOlem() {
        final String tabeliNimi = "produktiivne_aeg";

        if (kasOlemOlemas(tabeliNimi)) {
            System.out.printf("%s olem juba olemas\n", tabeliNimi);
            return;
        }

        final String looProduktiivsusAegOlem =
                "CREATE TABLE " + tabeliNimi + " (" +
                        "produktiivne_aeg_id SERIAL PRIMARY KEY NOT NULL UNIQUE," +
                        "kuupaev TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                        "produktiivne_aeg INTERVAL," +
                        "ulesanne_id INT NOT NULL," +
                        "FOREIGN KEY (ulesanne_id) REFERENCES ulesanded(ulesanne_id)" +
                        ");";

        try (PreparedStatement looProduktiivsusAegOlemLause = andmebaas.prepareStatement(looProduktiivsusAegOlem)) {
            looProduktiivsusAegOlemLause.executeUpdate();
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
