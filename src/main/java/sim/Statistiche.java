package sim;

public class Statistiche {
    private final int maxState;
    private final double[] tempoPerStato;
    private double areaCoda = 0;
    private double areaServizio = 0;
    private int clientiServiti = 0;
    private int clientiAttesi = 0;
    private double sommaAttesa = 0;
    private double sommaSistema = 0;

    public Statistiche(int maxState){
        this.maxState = maxState;
        this.tempoPerStato = new double[maxState + 1];
    }

    // Registra che il sistema è rimasto per dt unità di tempo con n clienti
    public void aggiornaStato(int inServizio, int inCoda, double dt){
        int n = inServizio + inCoda;
        tempoPerStato[Math.min(n, maxState)] += dt;
        areaServizio += inServizio * dt;
        areaCoda += inCoda * dt;
    }

    public RisultatiRun risultati(double durataOsservata){
        double[] prob = new double[maxState + 1];
        for ( int n = 0; n <= maxState; n++ ){
            prob[n] = tempoPerStato[n] / durataOsservata;
        }

        double Ls = areaServizio / durataOsservata;
        double Lq = areaCoda / durataOsservata;
        double Ws = clientiServiti > 0 ? sommaSistema / clientiServiti : 0;
        double Wq = clientiServiti > 0 ? sommaAttesa / clientiServiti : 0;
        double probAttesa = clientiServiti > 0 ? (double)clientiAttesi / clientiServiti : 0;

        return new RisultatiRun(Ls, Lq, Ws, Wq, probAttesa, prob, clientiServiti);
    }
}
