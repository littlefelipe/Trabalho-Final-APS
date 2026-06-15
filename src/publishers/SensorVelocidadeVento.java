package publishers;
import java.util.Random;

import com.rabbitmq.client.Connection;

public class SensorVelocidadeVento extends Sensor {
	private Random random = new Random();
	
    public SensorVelocidadeVento(Connection connection, String nomeRoteador) {
        super("VELOCIDADE", connection, nomeRoteador);
    }

	@Override
	public EventoClima gerarLeitura() {
		// Velocidade indo de 0 km/h 120 km/h
		double valor = 120 * random.nextDouble();
		return new EventoClima("VELOCIDADE_VENTO", valor, "Km/h");
	}

}
