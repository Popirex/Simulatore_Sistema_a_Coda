package sim;

public class Metriche {
    private double media;
    private double intConfid95;

    public Metriche(double m, double intConf){
        this.media = m;
        this.intConfid95 = intConf;
    }

    public double getMedia(){
        return this.media;
    }

    public double getIntConfidenza(){
        return this.intConfid95;
    }
}
