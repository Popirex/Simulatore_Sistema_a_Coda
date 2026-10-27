package sim;

import java.util.PriorityQueue;
import java.util.Queue;

public class Simulatore {

    private Config config;
    private double lambda;
    private int c;
    private long seme;
    private GeneratoreRandom generatore;
    private PriorityQueue<Evento> listaEventi;
    private Queue<Cliente> codaAttesa;
    private int serverOccupati;
    private double clock;
    private int idProssimoCustomer;
    private boolean statoWarmup;
    private Statistiche stat;


    public Simulatore(Config conf, double lambda, int c, long seme){
        this.config = conf;
        this.lambda = lambda;
        this.c = c;
        this.seme = seme; 
    }

    

    
}
