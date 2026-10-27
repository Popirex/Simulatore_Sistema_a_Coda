package sim;

import java.util.List;

public class RisultatoScenario {

    // valori di T student bilaterale al 95% indicizzati per gradi di libertà
    private static final double[] T95 = {
        12.706, 4.303, 3.182, 2.776, 2.571, 2.447, 2.365, 2.306, 2.262, 2.228,
        2.201, 2.179, 2.160, 2.145, 2.131, 2.120, 2.110, 2.101, 2.093, 2.086,
        2.080, 2.074, 2.069, 2.064, 2.060, 2.056, 2.052, 2.048, 2.045, 2.042
    };


    private double lambda, mu, rho;
    private int c, ripetizioni;
    private Metriche Ls, Lq, Ws, Wq, probAttesa;
    private double[] mediaPk, intConf95Pk;
    
    public RisultatoScenario(double lambda, double mu, int c, List<RisultatiRun> runs){


        // verifico che ci siano effettivamente delle run da caricare nel scenario

        if(runs == null || runs.isEmpty()){
            throw new IllegalArgumentException("Serve almeno una ripetizione per ottenere lambda= " + lambda + " , c= " + c);
        }

        // assegno i valori

        int n = runs.size();

        this.lambda = lambda;
        this.mu = mu;
        this.c = c;
        this.rho = lambda / (c * mu);
        this.ripetizioni = n;

        // creo degli array, con la dimensioni pari al numero di Run che ho fatto, li userò nella funzione riassumi

        double[] ls = new double[n];
        double[] lq = new double[n];
        double[] ws = new double[n];
        double[] wq = new double[n];
        double[] pa = new double[n];

        for(int i = 0; i < n; i++){
            RisultatiRun r = runs.get(i);
            ls[i] = r.getLs();
            lq[i] = r.getLq();
            ws[i] = r.getWs();
            wq[i] = r.getWq();
            pa[i] = r.getProbAttesa();
        }


    }


    // funzione per riassumere i dati ottenuti e salvarli in Metriche
    private static Metriche riassumi(double[] valori){

        int n = valori.length;
        double somma = 0;

        // calcolo la media campionaria

        for(double v : valori){
            somma += v;
        }

        double media = somma/n;

        // se il numero di valori è pari a 1, costruisco Metriche con la media esatta e l'intervallo di confidenza pari a 0.0 perchè non c'è varianza da stimare
        if(n < 2) return new Metriche(media, 0.0);

        double sommaSq = 0; // somma scarti al quadrato : vedo quanto si discosta dalla media e lo elevo alla seconda

        for(double v : valori){
            sommaSq += (v - media) * (v - media);
        }


        // calcolo la deviazione standard
        double deviazioneStandard = Math.sqrt(sommaSq / ( n - 1));

        // calcolo l'intervallo di confidenza al 95%
        double intConf95 = valoreT(n - 1) * deviazioneStandard / Math.sqrt(n);
        return new Metriche(media, intConf95);

    }

    // restituisco il corretto valore della distribuzione t student in base al numero di elementi, se sono più di 30 resituisco il valore 1.96
    private static double valoreT(int df){
        if(df >= 1 && df <= T95.length){
            return T95[df - 1];
        }
        return 1.96;
    }

}
