package publishers;

import com.rabbitmq.client.Connection;

public class SensorFactory {
    public static Sensor criarSensor(String tipo, Connection connection, String nomeRoteador) {
        switch (tipo.toUpperCase()) {
            case "TEMPERATURA":
                return new SensorTemperatura(connection, nomeRoteador);
            case "RUIDO":
                return new SensorPoluicaoSonora(connection, nomeRoteador);
            case "UMIDADE":
                return new SensorUmidade(connection, nomeRoteador);
            case "VELOCIDADE":
                return new SensorVelocidadeVento(connection, nomeRoteador);
            case "QUALIDADE":
                return new SensorQualidadeAr(connection, nomeRoteador);
            default:
                throw new IllegalArgumentException("Tipo de sensor desconhecido: " + tipo);
        }
    }
}