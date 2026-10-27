package sim;

import java.util.Queue;
import java.util.PriorityQueue;

public class Simulator {

    private Config config;
    private RandomGen generatore;
    private PriorityQueue<Event> listaEventi;
    private Queue<Customer> codaAttesa;
    private int serverOccupati;
    private double clock;
    private int idProssimoCustomer;
    private boolean statoWarmup;
    private Statistics stat;


    public Simulator(Config conf, double lambda, int c, long seed){
        
    }

    

    
}
