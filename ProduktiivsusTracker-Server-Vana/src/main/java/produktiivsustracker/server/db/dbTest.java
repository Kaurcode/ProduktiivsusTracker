package produktiivsustracker.server.db;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class dbTest {
    public static void main(String[] args) {
        byte[] sool = looSool(16);
        String parool1 = Base64.getEncoder().encodeToString(looParooliRasi("TereMinuNimiOnKaur", sool));
        System.out.println(parool1);
        System.out.println(parool1.length());

        for (int i = 0; i < 10; i++) {
            sool = looSool(16);
            System.out.println(Base64.getEncoder().encodeToString(sool).length());
        }

    }

    private static byte[] looParooliRasi(String parool, byte[] sool) {
        int iteratsioone = 10;
        int maluLimiitKB = 66536;
        int rasiPikkusBaitides = 32;
        int paralleelsus = 1;

        Argon2Parameters.Builder rasiEhitaja = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withVersion(Argon2Parameters.ARGON2_VERSION_13)
                .withIterations(iteratsioone)
                .withMemoryAsKB(maluLimiitKB)
                .withParallelism(paralleelsus)
                .withSalt(sool);

        Argon2BytesGenerator genereerija = new Argon2BytesGenerator();
        genereerija.init(rasiEhitaja.build());
        byte[] parooliRasi = new byte[rasiPikkusBaitides];
        genereerija.generateBytes(parool.getBytes(StandardCharsets.UTF_8), parooliRasi, 0, parooliRasi.length);

        return parooliRasi;
    }

    private static byte[] looSool(int arvBaite) {
        SecureRandom suvalisuseGeneraator = new SecureRandom();
        byte[] sool = new byte[arvBaite];

        suvalisuseGeneraator.nextBytes(sool);

        return sool;
    }
}
