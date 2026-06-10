package publishers;

import java.util.Random;

public class SensorPoluicaoSonora implements Sensor{
	private Random random = new Random();

    @Override
    public EventoClima gerarLeitura() {
        // Decibéis variando entre 40 (tranquilo) e 120 (muito barulhento/risco)
        double valor = 40.0 + (80.0 * random.nextDouble());
        return new EventoClima("POLUICAO_SONORA", valor, "dB");
    }
}
