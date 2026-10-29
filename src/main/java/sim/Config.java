package sim;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import com.google.gson.Gson;

public class Config {
    public double mu;
    public int[] valoriC;
    public double[] valoriLambda;
    public double tempoSimulazione;
    public double tempoWarmup;
    public int ripetizioni;
    public long seme;
    public int maxState;
    public String outputPath;

    // Legge la configurazione dal Json
    public Config load(String path) throws IOException{
        try(Reader reader = new FileReader(path)){
            return new Gson().fromJson(reader, Config.class);
        }
    }

    // Controlla la validità dei parametri
    public void validate(){
        if (mu<=0) {
            throw new IllegalArgumentException("Mu deve essere > 0");
        }
        if (tempoWarmup>tempoSimulazione) {
            throw new IllegalArgumentException("Warmup Time deve essere > di Simulation Time");
        }
        if (ripetizioni<1) {
            throw new IllegalArgumentException("Replications deve essere almeno 1");
        }
        for (double lambda : valoriLambda) {
            if (lambda<0) {
                throw new IllegalArgumentException("Ogni Lambda deve essere > 0");
            }
        }

        int maxC=0;

        for (int c : valoriC) {
           maxC=Math.max(maxC, c);
            if (c<1) {
                throw new IllegalArgumentException("Ogni C deve essere >= 1");
            }
        }
        if (maxState<maxC) {
            throw new IllegalArgumentException("Max State deve essere >= del massimo dei CValues");
        }
    }
}
