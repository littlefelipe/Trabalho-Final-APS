package publishers;

import java.util.Random;

public class SensorQualidadeAr implements Sensor {
	private Random random = new Random();
	@Override
    public EventoClima gerarLeitura() {
		// Qualidade do Ar indo de 0 IqAr a 500 IqAr
        double valor = 500 * random.nextDouble();
        return new EventoClima("QUALIDADE_AR", valor, "IqAr");
    }

}
