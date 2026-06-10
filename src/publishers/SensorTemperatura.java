package publishers;
import java.util.Random;

public class SensorTemperatura implements Sensor {
	private Random random = new Random();

    @Override
    public EventoClima gerarLeitura() {
        // Temperatura variando entre 15.0 e 40.0 graus
        double valor = 15.0 + (25.0 * random.nextDouble());
        return new EventoClima("TEMPERATURA", valor, "°C");
    }
}
