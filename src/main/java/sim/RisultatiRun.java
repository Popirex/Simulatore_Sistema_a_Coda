package sim;

public class RisultatiRun {
    
    private double Ls, Lq, Ws, Wq, probAttesa;
    private double[] Pk;
    private int clientiCompletati;

    public RisultatiRun(double ls, double lq, double ws, double wq, double probAttesa, double[] pk, int clienti){
        this.Ls = ls;
        this.Lq = lq;
        this.Ws = ws;
        this.Wq = wq;
        this.probAttesa = probAttesa;
        this.Pk = pk;
        this.clientiCompletati = clienti;
    }

    public double getLs(){
        return this.Ls;
    }
    
    public double getLq(){
        return this.Lq;
    }

    public double getWs(){
        return this.Ws;
    }

    public double[] getPk(){
        return this.Pk;
    }

    public double getWq(){
        return this.Wq;
    }

    public double getProbAttesa(){
        return this.probAttesa;
    }

}
