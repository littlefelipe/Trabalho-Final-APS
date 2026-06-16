package consumidores;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;

public class ServicoPainel extends WebSocketServer {


    public ServicoPainel(InetSocketAddress address) {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Novo painel conectado: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Painel desconectado: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) { }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Servidor WebSocket do Painel iniciado na porta: " + getPort());
    }

    public static void main(String[] args) throws Exception {
        ServicoPainel servidorWeb = new ServicoPainel(new InetSocketAddress(8887));
        servidorWeb.start();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri("amqps://jppfeqgq:U6t2UZu_i-Y43_2ndr5kRB2O8NvHUghf@beaver.rmq.cloudamqp.com/jppfeqgq");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Declara os dois roteadores
        channel.exchangeDeclare("eventos_ambientais", "fanout");
        channel.exchangeDeclare("alertas_ambientais", "fanout");

        // Cria uma única fila exclusiva para o WebSocket
        String nomeFila = channel.queueDeclare().getQueue();
        
        // Liga a fila AOS DOIS roteadores simultaneamente
        channel.queueBind(nomeFila, "eventos_ambientais", "");
        channel.queueBind(nomeFila, "alertas_ambientais", "");

        System.out.println("Serviço de Painel conectado. Escutando eventos e alertas...");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String mensagemJson = new String(delivery.getBody(), "UTF-8");
            
            // O WebSocket não precisa saber o que é. Ele só repassa a String JSON em frente.
            servidorWeb.broadcast(mensagemJson); 
        };

        channel.basicConsume(nomeFila, true, deliverCallback, consumerTag -> { });
    }
}