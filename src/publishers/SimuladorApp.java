package publishers;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.ArrayList;
import java.util.List;

public class SimuladorApp {
	private final static String nome_roteador = "eventos_ambientais";
    public static void main(String[] args) throws Exception {
        List<Sensor> redeDeSensores = new ArrayList<>();
        
        // Inicializando nossa rede de sensores via Factory
        redeDeSensores.add(SensorFactory.criarSensor("TEMPERATURA"));
        redeDeSensores.add(SensorFactory.criarSensor("RUIDO"));
        redeDeSensores.add(SensorFactory.criarSensor("UMIDADE"));
        redeDeSensores.add(SensorFactory.criarSensor("VELOCIDADE"));
        redeDeSensores.add(SensorFactory.criarSensor("QUALIDADE"));
        
        //Configurando credenciais do Rabbit
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri("amqps://jppfeqgq:U6t2UZu_i-Y43_2ndr5kRB2O8NvHUghf@beaver.rmq.cloudamqp.com/jppfeqgq");
        try(Connection connection = factory.newConnection(); 
        		Channel channel = connection.createChannel()){
        		
        		channel.exchangeDeclare(nome_roteador, "fanout" );
        
        
        System.out.println("Iniciando simulação de sensores...");

        // Loop infinito simulando a leitura contínua
        while (true) {
            for (Sensor sensor : redeDeSensores) {
                EventoClima evento = sensor.gerarLeitura();
                
                String mensagem = evento.toString();
                // Futuramente: rabbitTemplate.convertAndSend(exchange, routingKey, eventoJson);
                channel.basicPublish(nome_roteador, "", null, mensagem.getBytes("UTF-8"));
                System.out.println("Publicando evento: " + evento.toString());
            }
            
            // Pausa de 2 segundos entre as leituras para não inundar o console
            Thread.sleep(2000); 
        }
    }
}
}