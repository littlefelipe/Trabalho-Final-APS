package alertas;

import consumidores.EstadoAmbiental;

public class InsolacaoStrategy implements AlertaStrategy {
    
    @Override
    public ResultadoAlerta avaliarRisco(EstadoAmbiental estado) {
        Double temp = estado.getLeitura("TEMPERATURA");
        Double umidade = estado.getLeitura("UMIDADE");

        // Se faltam dados para a análise, não há alerta a ser emitido
        if (temp == null || umidade == null) {
            return new ResultadoAlerta(NivelRisco.NENHUM, "");
        }

        // Condição 1: Risco Grave (Extremo)
        if (temp >= 35.0 && umidade >= 70.0) {
            return new ResultadoAlerta(NivelRisco.GRAVE, "ALERTA GRAVE: Risco altíssimo de insolação! Defesa Civil acionada.");
        } 
        // Condição 2: Risco Médio
        else if (temp >= 32.0 && umidade >= 60.0) {
            return new ResultadoAlerta(NivelRisco.MEDIO, "ALERTA MÉDIO: Condições perigosas. Evite exposição prolongada ao sol.");
        } 
        // Condição 3: Atenção (Baixo)
        else if (temp >= 29.0 && umidade >= 50.0) {
            return new ResultadoAlerta(NivelRisco.ATENCAO, "Atenção: Clima abafado iniciando. Mantenha-se hidratado.");
        }

        // Condição Padrão: Tudo Seguro
        return new ResultadoAlerta(NivelRisco.NENHUM, "Condições normais.");
    }
}