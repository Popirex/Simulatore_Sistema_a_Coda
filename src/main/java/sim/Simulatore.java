package sim;

import java.util.LinkedList;
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
    private long seqEventi;


    public Simulatore(Config conf, double lambda, int c, long seme){
        this.config = conf;
        this.lambda = lambda;
        this.c = c;
        this.seme = seme; 
    }

    public RisultatiRun run(){
        inizializza();

        while (!listaEventi.isEmpty()) {

            Evento evento = listaEventi.poll();

            if (evento.tempo > config.tempoSimulazione) {
                avanzaTempo(config.tempoSimulazione);
                break;
            }

            avanzaTempo(evento.tempo);

            switch (evento.tipo) {
                case ARRIVAL:
                    
                    gestisciArrivo(evento);
                    break;
            
                case DEPARTURE:

                    gestisciPartenza(evento);
                    break;
            }
        }

        // Se non ci sono più eventi (lambda = 0) chiede comunque il tempo rimasto
        if (clock < config.tempoSimulazione) {
            avanzaTempo(config.tempoSimulazione);
        }

        return stat.risultati(config.tempoSimulazione - config.tempoWarmup);

    }

    // Prima di ogni run inizializziamo
    public void inizializza(){
        generatore = new GeneratoreRandom(seme);
        listaEventi = new PriorityQueue<>();
        codaAttesa = new LinkedList<>();
        serverOccupati = 0;
        clock = 0;
        idProssimoCustomer = 0;
        seqEventi= 0;
        statoWarmup = config.tempoWarmup > 0;
        stat = new Statistiche(config.maxState);

        schedulaProssimoArrivo();
    }

    // Crea il prossimo cliente e il relativo evento
    public  void schedulaProssimoArrivo(){
        double t = clock + generatore.nextExp(lambda);
        Cliente nuovoCliente = new Cliente(idProssimoCustomer++, t);
        listaEventi.add(new Evento(t, TipoEvento.ARRIVAL, nuovoCliente, seqEventi++));
    }

    /*
        Porta il clock a nuovoTempo, registrando per quanto tempo 
        il sistema è rimasto allo stato corrente
    */
    private void avanzaTempo(double nuovoTempo){

        if (!statoWarmup) {
            stat.aggiornaStato(serverOccupati, codaAttesa.size(), nuovoTempo - clock);
        }
        else if (nuovoTempo >= config.tempoWarmup){
            statoWarmup = false;
            stat.aggiornaStato(serverOccupati, codaAttesa.size(), nuovoTempo - config.tempoWarmup);
        }

        clock = nuovoTempo;
    }

    /*
        Si controlla se ci sono server liberi, in caso ci siano si fa iniziare il servizio
        se non ci sono server liberi si aggiunge in coda
    */
    public void gestisciArrivo(Evento evento){

        schedulaProssimoArrivo();

        if (serverOccupati < c) {
            serverOccupati++;
            iniziaServizio(evento.cliente);
        }
        else {
            codaAttesa.add(evento.cliente);
        }
    }

    /*
        Si prendono i tempi del cliente senza contare quelli del warmup
        Se ci sono clienti in coda si fanno partire
    */
   public void gestisciPartenza(Evento evento){

        Cliente cliente = evento.cliente;
        cliente.tempoPartenza = clock;

        if (cliente.tempoArrivo >= config.tempoWarmup) {
            stat.registraCliente(cliente);
        }

        if (!codaAttesa.isEmpty()) {
            iniziaServizio(codaAttesa.poll());
        }
        else {
            serverOccupati--;
        }
   }

   // Assegna ad un server un cliente e ne programma la sua partenza
   private void iniziaServizio(Cliente cliente){
        cliente.tempoInizioServizio = clock;

        double fine = clock + generatore.nextExp(config.mu);

        listaEventi.add(new Evento(fine, TipoEvento.DEPARTURE, cliente, seqEventi++));
   }
}
