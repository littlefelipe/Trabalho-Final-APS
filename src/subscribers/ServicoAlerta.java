package subscribers;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import publishers.EventoClima;
// import publishers.EventoClima; // Descomente e ajuste se necessário

public class ServicoAlerta {
    private final static String NOME_ROTEADOR = "eventos_ambientais";

    public static void main(String[] args) throws Exception {
        // 1. Configuração da Conexão
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri("amqps://jppfeqgq:U6t2UZu_i-Y43_2ndr5kRB2O8NvHUghf@beaver.rmq.cloudamqp.com/jppfeqgq");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 2. Declarando o roteador (precisa ser o mesmo dos sensores)
        channel.exchangeDeclare(NOME_ROTEADOR, "fanout");

        // 3. Criando uma fila exclusiva para este serviço e conectando ao roteador
        String nomeFila = channel.queueDeclare().getQueue();
        channel.queueBind(nomeFila, NOME_ROTEADOR, "");

        System.out.println("Serviço de Alerta iniciado. Aguardando leituras dos sensores...");

        Gson gson = new Gson();

        // 4. Definindo a rotina (callback) executada sempre que uma mensagem chegar
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String mensagemJson = new String(delivery.getBody(), "UTF-8");
            
            try {
                // Transformando o JSON de volta para Objeto Java
                // OBS: A classe EventoClima precisa ter os mesmos atributos do JSON
                EventoClima evento = gson.fromJson(mensagemJson, EventoClima.class);
                
                System.out.println("\n[Alerta] Analisando leitura de: " + evento.getTipoSensor());
                System.out.println("-> Valor registrado: " + evento.getValor() + " " + evento.getUnidade());
                
                // O passo "Avaliar limiar" do seu diagrama de atividades entrará aqui
                
            } catch (Exception e) {
                System.err.println("Erro ao processar evento: " + mensagemJson);
                // Aqui seria o roteamento para a DLQ (Dead Letter Queue) em caso de erro grave
            }
        };

        // 5. Iniciando o consumo contínuo
        channel.basicConsume(nomeFila, true, deliverCallback, consumerTag -> { });
    }
}