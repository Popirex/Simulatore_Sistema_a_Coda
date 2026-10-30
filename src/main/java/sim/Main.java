package sim;

import java.util.ArrayList;
import java.util.List;

public class Main {



    public static void main(String[] args) throws Exception {

    Config configurazione;
    String pathConfigurazione = "config.json";

    configurazione = new Config().load(pathConfigurazione);
    configurazione.validate();
    List<RisultatoScenario> scenari = new ArrayList<>();

    // uso contatore run per incrementare ogni volta il valore del seme di partenza, per avere risultati casuali
    int contatoreRun = 0;

    for (int c : configurazione.valoriC) {
        for (double lambda : configurazione.valoriLambda) {
            
            double rho = lambda / (c * configurazione.mu);

            if(rho >= 1){
                System.out.println("Salto configurazione con c= " + c + " , lambda= " + lambda + " e rho: " + rho + " >=1");
                continue;
            }

            List<RisultatiRun> ripetizioni = new ArrayList<>();

            for(int i = 0; i < configurazione.ripetizioni; i++){
                // a ogni iterazione modifico prima il contatore, in modo da avere ogni volta un seme nuovo per la simulazione;
                contatoreRun++;
                Simulatore sim = new Simulatore(configurazione, lambda, c, configurazione.seme + contatoreRun);
                ripetizioni.add(sim.run());
            }
            scenari.add(new RisultatoScenario(lambda, configurazione.mu, c, ripetizioni));
            

        }
    }
        
    }

    
}