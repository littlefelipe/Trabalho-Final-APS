package alertas;

public class ResultadoAlerta {
    private NivelRisco nivel;
    private String mensagem;

    public ResultadoAlerta(NivelRisco nivel, String mensagem) {
        this.nivel = nivel;
        this.mensagem = mensagem;
    }

    public NivelRisco getNivel() { return nivel; }
    public String getMensagem() { return mensagem; }
}