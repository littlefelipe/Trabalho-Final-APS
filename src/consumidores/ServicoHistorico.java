package consumidores;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import produtores.EventoClima;

import java.sql.DriverManager;
import java.sql.PreparedStatement;



public class ServicoHistorico {
    private final static String NOME_ROTEADOR = "eventos_ambientais";
    
    // Credenciais do Banco de Dados
    private final static String DB_URL = "jdbc:postgresql://ep-aged-poetry-acxch3o5-pooler.sa-east-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require";
    private final static String DB_USER = "postgres";
    private final static String DB_PASSWORD = "npg_DnRLdvP3Hfc1";

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
                
                // Mecanismo de segurança para a data (Timestamp)
                java.sql.Timestamp carimboTempo;
                if (evento.getTimestamp() == null) {
                    // Se o Gson não conseguiu ler a data do JSON, usamos a hora atual
                    carimboTempo = new java.sql.Timestamp(System.currentTimeMillis());
                } else {
                    // Limpa o formato para o padrão estrito do PostgreSQL (YYYY-MM-DD HH:MM:SS)
                    String dataLimpa = evento.getTimestamp().replace("T", " ");
                    if (dataLimpa.contains(".")) {
                        dataLimpa = dataLimpa.substring(0, dataLimpa.indexOf("."));
                    }
                    carimboTempo = java.sql.Timestamp.valueOf(dataLimpa);
                }
                
                // Preenche os parâmetros do SQL
                pstmt.setString(1, evento.getIdEvento());
                pstmt.setString(2, evento.getTipoSensor());
                pstmt.setDouble(3, evento.getValor());
                pstmt.setString(4, evento.getUnidade());
                pstmt.setTimestamp(5, carimboTempo);
                
                // Executa a gravação na base de dados
                pstmt.executeUpdate();
                
                System.out.println("[Histórico] Evento " + evento.getTipoSensor() + " gravado localmente com sucesso.");
                
            } catch (Exception e) {
                // Imprime o erro exato e o JSON problemático para análise
                System.err.println("Falha ao gravar. Erro: " + e.getMessage());
                System.err.println("JSON recebido do RabbitMQ: " + mensagemJson);
            }
        };

        // 4. Inicia o consumo
        channel.basicConsume(nomeFila, true, deliverCallback, consumerTag -> { });
    }
}