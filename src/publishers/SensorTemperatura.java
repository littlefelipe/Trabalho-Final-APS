package publishers;
import com.rabbitmq.client.Connection;
import java.util.Random;

public class SensorTemperatura extends Sensor {
    private Random random = new Random();

    public SensorTemperatura(Connection connection, String nomeRoteador) {
        super("TEMPERATURA", connection, nomeRoteador);
    }

    @Override
    protected EventoClima gerarLeitura() {
        double valor = 15.0 + (25.0 * random.nextDouble());
        return new EventoClima(tipoSensor, valor, "°C");
    }
}
