package publishers;
import java.util.Random;

public class SensorUmidade implements Sensor {
	private Random random = new Random();
	
	@Override
	public EventoClima gerarLeitura() {
		// Umidade indo de 0% a 99%
		double valor = 99 * random.nextDouble();
		return new EventoClima("UMIDADE", valor, "%");
	}

}
