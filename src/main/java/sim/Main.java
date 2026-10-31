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


            //lancio n (ripetizioni) simulazioni con gli stessi parametri per ottenere una quantità sufficiente di dati con ogni preset

            for(int i = 0; i < configurazione.ripetizioni; i++){
                // a ogni iterazione modifico prima il contatore, in modo da avere ogni volta un seme nuovo per la simulazione;
                contatoreRun++;

                
                // faccio andare la simulazione e quando ha finito la salvo dentro Scenari ( stessi parametri, piu ripetizioni)
                Simulatore sim = new Simulatore(configurazione, lambda, c, configurazione.seme + contatoreRun);
                ripetizioni.add(sim.run());
            }

            // salvo ogni scenario quando ha terminato ( scenario = insieme di n (ripetizioni) con stessi parametri)
            scenari.add(new RisultatoScenario(lambda, configurazione.mu, c, ripetizioni));
            

        }
    }

        //salvo i risultati per utilizzarli nella creazione dei grafici
        stampaRisultati(configurazione, scenari);

        
    }


    // funzione per esportare i dati in un json
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