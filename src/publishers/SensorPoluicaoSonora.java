package publishers;

import java.util.Random;

import com.rabbitmq.client.Connection;

public class SensorPoluicaoSonora extends Sensor{
	private Random random = new Random();
	
    public SensorPoluicaoSonora(Connection connection, String nomeRoteador) {
        super("RUIDO", connection, nomeRoteador);
    }
    @Override
    public EventoClima gerarLeitura() {
        // Decibéis variando entre 40 (tranquilo) e 120 (muito barulhento/risco)
        double valor = 40.0 + (80.0 * random.nextDouble());
        return new EventoClima("POLUICAO_SONORA", valor, "dB");
    }
}
