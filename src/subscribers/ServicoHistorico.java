package subscribers;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import publishers.EventoClima;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;



public class ServicoHistorico {
    private final static String NOME_ROTEADOR = "eventos_ambientais";
    
    // Credenciais do Banco de Dados
    private final static String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private final static String DB_USER = "postgres";
    private final static String DB_PASSWORD = "12345";

    public static void main(String[] args) throws Exception {
        // 1. Conexão com o Banco de Dados
        java.sql.Connection dbConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        System.out.println("Conexão com o banco de dados estabelecida.");

        // 2. Conexão com o RabbitMQ
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri("amqps://jppfeqgq:U6t2UZu_i-Y43_2ndr5kRB2O8NvHUghf@beaver.rmq.cloudamqp.com/jppfeqgq");
        Connection mqConnection = factory.newConnection();
        Channel channel = mqConnection.createChannel();

        channel.exchangeDeclare(NOME_ROTEADOR, "fanout");
        String nomeFila = channel.queueDeclare().getQueue();
        channel.queueBind(nomeFila, NOME_ROTEADOR, "");

        System.out.println("Serviço de Histórico iniciado. Gravando leituras...");

        Gson gson = new Gson();
        
        // SQL de Inserção
        String sqlInsert = "INSERT INTO historico_clima (id_evento, tipo_sensor, valor, unidade, data_hora) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = dbConnection.prepareStatement(sqlInsert);

        // 3. Callback de recebimento e gravação
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String mensagemJson = new String(delivery.getBody(), "UTF-8");
            
            try {
                // Converte de JSON para o Objeto Java
                EventoClima evento = gson.fromJson(mensagemJson, EventoClima.class);
                
                // Preenche os parâmetros do SQL
                pstmt.setString(1, evento.getIdEvento());
                pstmt.setString(2, evento.getTipoSensor());
                pstmt.setDouble(3, evento.getValor());
                pstmt.setString(4, evento.getUnidade());
                pstmt.setTimestamp(5, Timestamp.valueOf(evento.getTimestamp()));
                
                // Executa a gravação no banco [cite: 23]
                pstmt.executeUpdate();
                
                System.out.println("[Histórico] Evento " + evento.getIdEvento() + " gravado com sucesso.");
                
            } catch (Exception e) {
                System.err.println("Falha ao gravar evento no banco: " + e.getMessage());
            }
        };

        // 4. Inicia o consumo
        channel.basicConsume(nomeFila, true, deliverCallback, consumerTag -> { });
    }
}