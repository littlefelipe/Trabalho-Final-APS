package consumidores;

import produtores.EventoClima;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EstadoAmbiental {
    // Guarda a última leitura associada ao nome do sensor
    private Map<String, Double> ultimasLeituras = new ConcurrentHashMap<>();

    public void atualizar(EventoClima evento) {
        ultimasLeituras.put(evento.getTipoSensor(), evento.getValor());
    }

    public Double getLeitura(String tipoSensor) {
        return ultimasLeituras.get(tipoSensor); // Retorna null se não houver leitura ainda
    }
}