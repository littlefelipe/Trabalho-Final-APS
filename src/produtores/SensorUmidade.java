package produtores;
import java.util.Random;

import com.rabbitmq.client.Connection;

public class SensorUmidade extends Sensor {
	private Random random = new Random();
	
    public SensorUmidade(Connection connection, String nomeRoteador) {
        super("UMIDADE", connection, nomeRoteador);
    }
	
	@Override
	public EventoClima gerarLeitura() {
		// Umidade indo de 0% a 99%
		double valor = 99 * random.nextDouble();
		return new EventoClima("UMIDADE", valor, "%");
	}

}
