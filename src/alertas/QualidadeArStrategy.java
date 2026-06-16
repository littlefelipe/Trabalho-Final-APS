package alertas;

import consumidores.EstadoAmbiental;

public class QualidadeArStrategy implements AlertaStrategy {
    
    @Override
    public ResultadoAlerta avaliarRisco(EstadoAmbiental estado) {
        // Supondo que o valor seja o Índice de Qualidade do Ar (AQI)
        Double qualidadeAr = estado.getLeitura("QUALIDADE");

        if (qualidadeAr == null) return new ResultadoAlerta(NivelRisco.NENHUM, "");

        if (qualidadeAr >= 200.0) {
            return new ResultadoAlerta(NivelRisco.GRAVE, "ALERTA GRAVE: Ar muito insalubre. Risco respiratório alto para toda a população.");
        } else if (qualidadeAr >= 100.0) {
            return new ResultadoAlerta(NivelRisco.MEDIO, "ALERTA MÉDIO: Qualidade do ar ruim. Grupos sensíveis devem evitar esforço ao ar livre.");
        } else if (qualidadeAr >= 50.0) {
            return new ResultadoAlerta(NivelRisco.ATENCAO, "Atenção: Qualidade do ar moderada. Poluentes presentes.");
        }

        return new ResultadoAlerta(NivelRisco.NENHUM, "");
    }
}