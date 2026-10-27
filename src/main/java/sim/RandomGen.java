package sim;
import java.util.Random;

public class RandomGen {
    private final Random rng;

    public RandomGen(long seed){
        this.rng=new Random(seed);
    }

    public double nextExp(double rate){
        return -Math.log(1-rng.nextDouble())/rate;
    }
}
