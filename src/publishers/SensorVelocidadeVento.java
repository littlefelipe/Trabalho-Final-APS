package publishers;
import java.util.Random;

public class SensorVelocidadeVento implements Sensor {
	private Random random = new Random();

	@Override
	public EventoClima gerarLeitura() {
		// Velocidade indo de 0 km/h 120 km/h
		double valor = 120 * random.nextDouble();
		return new EventoClima("VELOCIDADE_VENTO", valor, "Km/h");
	}

}
