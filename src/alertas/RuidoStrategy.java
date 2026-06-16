package alertas;

import consumidores.EstadoAmbiental;

public class RuidoStrategy implements AlertaStrategy {
    
    @Override
    public ResultadoAlerta avaliarRisco(EstadoAmbiental estado) {
        Double ruido = estado.getLeitura("RUIDO");

        if (ruido == null) return new ResultadoAlerta(NivelRisco.NENHUM, "");

        if (ruido >= 100.0) {
            return new ResultadoAlerta(NivelRisco.GRAVE, "ALERTA GRAVE: Poluição sonora extrema (" + String.format("%.1f", ruido) + " dB). Risco de dano auditivo imediato!");
        } else if (ruido >= 85.0) {
            return new ResultadoAlerta(NivelRisco.MEDIO, "ALERTA MÉDIO: Ruído acima do limite de saúde. Uso prolongado requer proteção.");
        } else if (ruido >= 70.0) {
            return new ResultadoAlerta(NivelRisco.ATENCAO, "Atenção: Nível de ruído ambiente elevado, causando desconforto.");
        }

        return new ResultadoAlerta(NivelRisco.NENHUM, "");
    }
}