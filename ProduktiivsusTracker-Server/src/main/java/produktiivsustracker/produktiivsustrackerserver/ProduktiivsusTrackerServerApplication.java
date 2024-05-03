package produktiivsustracker.produktiivsustrackerserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import produktiivsustracker.produktiivsustrackerserver.andmebaas.Andmebaas;

@SpringBootApplication
public class ProduktiivsusTrackerServerApplication {

    public static void main(String[] args) {
        Andmebaas.kontrolliDraiver();
        SpringApplication.run(ProduktiivsusTrackerServerApplication.class, args);
    }
}
