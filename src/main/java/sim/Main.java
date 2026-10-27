package sim;

public class Main {

    static Config configurazione;
    static String pathConfigurazione = "config.json";

    public static void main(String[] args) throws Exception {

    configurazione = new Config();
    configurazione.load(pathConfigurazione);
    configurazione.validate();
        
    }

    
}