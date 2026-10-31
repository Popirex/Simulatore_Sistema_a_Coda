package sim;
import java.util.List;

public class RisultatiFinali {

    private Config configurazione;
    private List<RisultatoScenario> risultatiScenari;

    // questa classe serve solo come wrapper da passare al costruttore jSon per il file output
    public RisultatiFinali(Config conf, List<RisultatoScenario> scenari){
        this.configurazione = conf;
        this.risultatiScenari = scenari;
    }
    
}
