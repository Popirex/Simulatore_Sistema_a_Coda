package sim;

import java.util.Queue;
import java.util.PriorityQueue;

public class Simulator {

    Config config;
    RandomGen generatore;
    PriorityQueue<Event> listaEventi;
    Queue<Customer> codaAttesa;
    int serverOccupati;
    double clock;
    int idProssimoCustomer;
    boolean statoWarmup;
    Statistics stat;

    
    

    
}
