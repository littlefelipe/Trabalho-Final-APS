package alertas;

import consumidores.EstadoAmbiental;

public interface AlertaStrategy {
	ResultadoAlerta avaliarRisco(EstadoAmbiental estado);
}
