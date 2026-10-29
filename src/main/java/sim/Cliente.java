package sim;

public class Cliente {
    public final int id;
    public final double tempoArrivo;
    public double tempoInizioServizio=-1;
    public double tempoPartenza=-1;


    public Cliente(int id, double tempoArrivo){
        this.id=id;
        this.tempoArrivo=tempoArrivo;
    }

    // Tempo trascorso in coda (Wq=inizio servizio - arrivo)
    public double waitingTime(){
        return tempoInizioServizio-tempoArrivo;
    }

    // Tempo totale nel sistema (W=partenza - arrivo)
    public double systemTime(){
        return tempoPartenza-tempoArrivo;
    }
}


