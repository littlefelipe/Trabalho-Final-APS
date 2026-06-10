package publishers;

import java.util.ArrayList;
import java.util.List;

public class SimuladorApp {
    public static void main(String[] args) throws InterruptedException {
        List<Sensor> redeDeSensores = new ArrayList<>();
        
        // Inicializando nossa rede de sensores via Factory
        redeDeSensores.add(SensorFactory.criarSensor("TEMPERATURA"));
        redeDeSensores.add(SensorFactory.criarSensor("RUIDO"));
        redeDeSensores.add(SensorFactory.criarSensor("UMIDADE"));
        redeDeSensores.add(SensorFactory.criarSensor("VELOCIDADE"));
        redeDeSensores.add(SensorFactory.criarSensor("QUALIDADE"));

        System.out.println("Iniciando simulação de sensores...");

        // Loop infinito simulando a leitura contínua
        while (true) {
            for (Sensor sensor : redeDeSensores) {
                EventoClima evento = sensor.gerarLeitura();
                
                // Futuramente: rabbitTemplate.convertAndSend(exchange, routingKey, eventoJson);
                System.out.println("Publicando evento: " + evento.toString());
            }
            
            // Pausa de 2 segundos entre as leituras para não inundar o console
            Thread.sleep(2000); 
        }
    }
}