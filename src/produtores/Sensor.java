package produtores;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.google.gson.Gson;

public abstract class Sensor implements Runnable {
    protected String tipoSensor;
    private Connection connection;
    private String nomeRoteador;

    public Sensor(String tipoSensor, Connection connection, String nomeRoteador) {
        this.tipoSensor = tipoSensor;
        this.connection = connection;
        this.nomeRoteador = nomeRoteador;
    }

    // Método que as subclasses (Temperatura, Ruido, etc.) vão implementar
    protected abstract EventoClima gerarLeitura();

    @Override
    public void run() {
        Gson gson = new Gson(); // Instancia o conversor

        try (Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(nomeRoteador, "fanout");

            while (true) {
                EventoClima evento = gerarLeitura();
                
                // Converte o objeto Java para uma String estruturada em JSON
                String mensagemJson = gson.toJson(evento);
                
                // Publica o JSON no RabbitMQ
                channel.basicPublish(nomeRoteador, "", null, mensagemJson.getBytes("UTF-8"));
                System.out.println("[Thread " + Thread.currentThread().getName() + "] Publicado: " + mensagemJson);
                
                Thread.sleep(2000); 
            }
        } catch (Exception e) {
            System.err.println("Erro no sensor: " + e.getMessage());
        }
    }
}