package produtores;


import java.util.UUID;

public class EventoClima {
    private String idEvento;
    private String tipoSensor;
    private double valor;
    private String unidade;
    private String timestamp;

    public EventoClima(String tipoSensor, double valor, String unidade) {
        this.idEvento = UUID.randomUUID().toString();
        this.tipoSensor = tipoSensor;
        this.valor = valor;
        this.unidade = unidade;
        
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %.2f %s", timestamp, tipoSensor, valor, unidade);
    }

	public String getIdEvento() {
		return idEvento;
	}

	public String getTipoSensor() {
		return tipoSensor;
	}

	public double getValor() {
		return valor;
	}

	public String getUnidade() {
		return unidade;
	}

	public String getTimestamp() {
		return timestamp;
	}
}