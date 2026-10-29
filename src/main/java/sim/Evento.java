package sim;

public class Evento implements Comparable<Evento>{
    public final double tempo;
    public final TipoEvento tipo;
    public final Cliente cliente;
    public final long seq;

    public Evento(double tempo, TipoEvento tipo, Cliente cliente, long seq){
        this.tempo=tempo;
        this.tipo=tipo;
        this.cliente=cliente;
        this.seq=seq;
    }

    /*
        Ordina per tempo crescente gli eventi
        A parità di tempo per ordine di creazione
    */
    @Override 
    public int compareTo(Evento other){
        int cmp=Double.compare(this.tempo, other.tempo);
        if (cmp!=0) {
            return cmp;
        }
        
        return Long.compare(this.seq, other.seq);
    }
}
