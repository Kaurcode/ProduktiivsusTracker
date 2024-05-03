package produktiivsustracker.produktiivsustrackerserver.kontrollerid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import produktiivsustracker.produktiivsustrackerserver.AndmebaasiVorm;
import produktiivsustracker.produktiivsustrackerserver.andmebaas.Andmebaas;

import java.sql.SQLException;

@Controller
public class UhendaAndmebaasKontroller {
    @GetMapping("/andmebaas")
    public String uhendaAndmebaas(Model model) {
        model.addAttribute("andmebaasiAndmed", new AndmebaasiVorm());
        return "andmebaas-uhenda";
    }

    @PostMapping("/andmebaas")
    public String andmebaasiVaartused(@ModelAttribute AndmebaasiVorm andmebaasiVorm, Model model) {
        Andmebaas andmebaas = null;
        try {
            andmebaas = new Andmebaas(andmebaasiVorm.getURL(),
                    andmebaasiVorm.getPort(),
                    andmebaasiVorm.getKasutajaNimi(),
                    andmebaasiVorm.getParool(),
                    andmebaasiVorm.getAndmebaasiNimi(),
                    false);
        } catch (SQLException viga) {
            //TODO
        } finally {
            if (andmebaas != null)
                andmebaas.close();
        }
        return "index";
    }
}
