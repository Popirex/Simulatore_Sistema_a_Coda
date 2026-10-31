package sim;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

        stampaRisultati(configurazione, scenari);

        
    }

    public static void stampaRisultati(Config configurazione, List<RisultatoScenario> scenari){
        RisultatiFinali risultati = new RisultatiFinali(configurazione, scenari);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Path pathOutput = Paths.get(configurazione.outputPath);

        try {
            
            if(pathOutput.getParent() != null){
            Files.createDirectories(pathOutput.getParent());


            try(Writer w = Files.newBufferedWriter(pathOutput);) {
                gson.toJson(risultati, w);
            } catch (Exception e) {
                System.err.println("Errore con la creazione del writer per il file di output" + e.getMessage());
                e.printStackTrace();
                System.exit(4);
            }

            

            

        }

        } catch (Exception e) {
            System.err.println("Errore: " + e.getMessage());
            e.printStackTrace();
            System.exit(3);
        }
        
    }

    
}