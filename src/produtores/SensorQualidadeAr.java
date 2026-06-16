package produtores;

import java.util.Random;

import com.rabbitmq.client.Connection;

public class SensorQualidadeAr extends Sensor {
	private Random random = new Random();
	
    public SensorQualidadeAr(Connection connection, String nomeRoteador) {
        super("QUALIDADE", connection, nomeRoteador);
    }
	@Override
    public EventoClima gerarLeitura() {
		// Qualidade do Ar indo de 0 IqAr a 500 IqAr
        double valor = 500 * random.nextDouble();
        return new EventoClima("QUALIDADE", valor, "IqAr");
    }

}
