package sim;
import java.util.Random;

public class GeneratoreRandom {
    private final Random rng;

    // Genereo un seme casuale
    public GeneratoreRandom(long seme){
        this.rng=new Random(seme);
    }

    /*  
        Genera un tempo casuale che segue la distribuzione esponenziale
        Serve per generare quanto tempo passa prima del prossimo arrivo (lamba)
        E quanto dura il servizio (mu)
    */ 
    public double nextExp(double rate){
        return -Math.log(1-rng.nextDouble())/rate;
    }
}
