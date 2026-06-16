package alertas;

import consumidores.EstadoAmbiental;

public class TempestadeStrategy implements AlertaStrategy {
    
    @Override
    public ResultadoAlerta avaliarRisco(EstadoAmbiental estado) {
        Double temp = estado.getLeitura("TEMPERATURA");
        Double umidade = estado.getLeitura("UMIDADE");
        Double vento = estado.getLeitura("VELOCIDADE"); // Certifique-se de que o sensor envia "VELOCIDADE"

        if (temp == null || umidade == null || vento == null) {
            return new ResultadoAlerta(NivelRisco.NENHUM, "");
        }

        if (temp < 22.0 && umidade >= 90.0 && vento >= 80.0) {
            return new ResultadoAlerta(NivelRisco.GRAVE, "ALERTA GRAVE: Condições para tempestade severa com ventos destrutivos!");
        } else if (umidade >= 85.0 && vento >= 60.0) {
            return new ResultadoAlerta(NivelRisco.MEDIO, "ALERTA MÉDIO: Formação de tempestade moderada. Ventania detectada.");
        } else if (umidade >= 80.0 && vento >= 40.0) {
            return new ResultadoAlerta(NivelRisco.ATENCAO, "Atenção: Clima instável. Possibilidade de chuva forte.");
        }

        return new ResultadoAlerta(NivelRisco.NENHUM, "");
    }
}