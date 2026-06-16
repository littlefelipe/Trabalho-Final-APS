package consumidores;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import alertas.*;
import produtores.EventoClima;

import java.util.Arrays;
import java.util.List;

public class ServicoAlerta {
    private final static String NOME_ROTEADOR_EVENTOS = "eventos_ambientais";
    private final static String NOME_ROTEADOR_ALERTAS = "alertas_ambientais"; // Novo roteador

    // Classe auxiliar para gerar o JSON do alerta
    static class PayloadAlerta {
        String tipoMensagem = "ALERTA";
        String nivel;
        String mensagem;

        public PayloadAlerta(String nivel, String mensagem) {
            this.nivel = nivel;
            this.mensagem = mensagem;
        }
    }

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri("amqps://jppfeqgq:U6t2UZu_i-Y43_2ndr5kRB2O8NvHUghf@beaver.rmq.cloudamqp.com/jppfeqgq");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Declara os dois roteadores
        channel.exchangeDeclare(NOME_ROTEADOR_EVENTOS, "fanout");
        channel.exchangeDeclare(NOME_ROTEADOR_ALERTAS, "fanout");

        // O Serviço de Alerta só escuta os eventos dos sensores
        String nomeFila = channel.queueDeclare().getQueue();
        channel.queueBind(nomeFila, NOME_ROTEADOR_EVENTOS, "");

        System.out.println("Serviço de Alerta iniciado. Avaliando riscos e publicando alertas...");

        Gson gson = new Gson();
        EstadoAmbiental estadoAtual = new EstadoAmbiental();
        List<AlertaStrategy> regrasAtivas = Arrays.asList(
            new InsolacaoStrategy(),
            new TempestadeStrategy(),
            new RuidoStrategy(),
            new QualidadeArStrategy()
        );

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String mensagemJson = new String(delivery.getBody(), "UTF-8");
            
            try {
                EventoClima evento = gson.fromJson(mensagemJson, EventoClima.class);
                estadoAtual.atualizar(evento);
                
                for (AlertaStrategy regra : regrasAtivas) {
                    ResultadoAlerta resultado = regra.avaliarRisco(estadoAtual);
                    
                    if (resultado.getNivel() != NivelRisco.NENHUM) {
                        System.out.println("Ameaça detectada! Publicando alerta nível: " + resultado.getNivel());
                        
                        // Cria o objeto de alerta e converte para JSON
                        PayloadAlerta payload = new PayloadAlerta(resultado.getNivel().name(), resultado.getMensagem());
                        String jsonAlerta = gson.toJson(payload);
                        
                        // Publica de volta no RabbitMQ no canal exclusivo de alertas
                        channel.basicPublish(NOME_ROTEADOR_ALERTAS, "", null, jsonAlerta.getBytes("UTF-8"));
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro ao processar evento: " + e.getMessage());
            }
        };

        channel.basicConsume(nomeFila, true, deliverCallback, consumerTag -> { });
    }
}