package sim;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class Config {
    public double mu;
    public int[] cValues;
    public double[] lambdaValues;
    public double simulationTime;
    public double warmupTime;
    public int replications;
    public long seed;
    public int maxState;
    public String outputPath;

    public static Config load(String path) throws IOException{
        try(Reader reader = new FileReader(path)){
            return new Gson().fromJson(reader, Config.class);
        }
    }

    public void validate(){
        if (mu<=0) {
            throw new IllegalArgumentException("Mu deve essere > 0");
        }
        if (warmupTime>simulationTime) {
            throw new IllegalArgumentException("Warmup Time deve essere < di Simulation Time");
        }
        if (replications<1) {
            throw new IllegalArgumentException("Replications deve essere almeno 1");
        }
        for (double lamba : lambdaValues) {
            if (lamba<0) {
                throw new IllegalArgumentException("Ogni Lamba deve essere > 0");
            }
        }

        int maxC=0;

        for (int c : cValues) {
            Math.max(maxC, c);
            if (c<1) {
                throw new IllegalArgumentException("Ogni C deve essere >= 1");
            }
        }
        if (maxState<maxC) {
            throw new IllegalArgumentException("Max State deve essere >= del massimo dei CValues");
        }
    }
}
