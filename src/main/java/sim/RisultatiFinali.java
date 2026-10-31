package sim;
import java.util.List;

public class RisultatiFinali {

    private Config configurazione;
    private List<RisultatoScenario> risultatiScenari;

    public RisultatiFinali(Config conf, List<RisultatoScenario> scenari){
        this.configurazione = conf;
        this.risultatiScenari = scenari;
    }
    
}
