package publishers;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.ArrayList;
import java.util.List;

public class SimuladorApp {
    private final static String NOME_ROTEADOR = "eventos_ambientais";

    public static void main(String[] args) throws Exception {
        // 1. Configurando credenciais e abrindo a Conexão principal (Compartilhada)
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri("amqps://jppfeqgq:U6t2UZu_i-Y43_2ndr5kRB2O8NvHUghf@beaver.rmq.cloudamqp.com/jppfeqgq");
        Connection connection = factory.newConnection();
        
        System.out.println("Conexão com RabbitMQ estabelecida. Inicializando sensores independentes...");

        // 2. Criando a lista de sensores via Factory
        List<Sensor> redeDeSensores = new ArrayList<>();
        redeDeSensores.add(SensorFactory.criarSensor("TEMPERATURA", connection, NOME_ROTEADOR));
        redeDeSensores.add(SensorFactory.criarSensor("RUIDO", connection, NOME_ROTEADOR));
        redeDeSensores.add(SensorFactory.criarSensor("UMIDADE", connection, NOME_ROTEADOR));
        redeDeSensores.add(SensorFactory.criarSensor("VELOCIDADE", connection, NOME_ROTEADOR));
        redeDeSensores.add(SensorFactory.criarSensor("QUALIDADE", connection, NOME_ROTEADOR));

        // 3. Iniciando cada sensor em sua própria Thread
        for (Sensor sensor : redeDeSensores) {
            Thread threadSensor = new Thread(sensor);
            threadSensor.setName("Thread-" + sensor.tipoSensor);
            threadSensor.start();
        }

        System.out.println("Todos os sensores foram despachados. O simulador está rodando em background.");
        
        // A Thread principal (main) finaliza aqui, mas a JVM continuará rodando
        // porque as threads dos sensores não são 'daemon threads'.
    }
}