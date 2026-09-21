/** Representa uma chave de acesso a uma API e o seu consumo no ciclo atual. */
public class ChaveApi {
    private final String token;
    private String plano;
    private int limiteRequisicoes;
    private int requisicoesRealizadas;
    private boolean ativa;

    public ChaveApi(String token, String plano, int limiteRequisicoes) {
        this.token = token;
        this.plano = plano;
        this.limiteRequisicoes = limiteRequisicoes;
        this.requisicoesRealizadas = 0;
        this.ativa = true;
    }

    public String getToken() {
        return token;
    }

    public String getPlano() {
        return plano;
    }

    public int getLimiteRequisicoes() {
        return limiteRequisicoes;
    }

    public int getRequisicoesRealizadas() {
        return requisicoesRealizadas;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void registrarChamada() {
        if (!ativa) {
            throw new IllegalStateException("Acesso negado: Chave inativa.");
        }
        if (requisicoesRealizadas >= limiteRequisicoes) {
            throw new IllegalStateException("Acesso negado: Limite de requisições excedido.");
        }

        requisicoesRealizadas++;
    }

    public void fazerUpgrade(String novoPlano, int novoLimite) {
        if (novoLimite < limiteRequisicoes) {
            throw new IllegalArgumentException(
                    "O novo limite deve ser maior ou igual ao limite atual.");
        }

        plano = novoPlano;
        limiteRequisicoes = novoLimite;
    }

    public void bloquearChave() {
        ativa = false;
    }

    public void desbloquearChave() {
        ativa = true;
    }

    public void resetarCiclo() {
        requisicoesRealizadas = 0;
    }

    @Override
    public String toString() {
        return "ChaveApi{"
                + "token='" + token + '\''
                + ", plano='" + plano + '\''
                + ", limiteRequisicoes=" + limiteRequisicoes
                + ", requisicoesRealizadas=" + requisicoesRealizadas
                + ", ativa=" + ativa
                + '}';
    }
}
