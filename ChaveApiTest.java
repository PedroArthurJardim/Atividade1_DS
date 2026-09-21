public class ChaveApiTest {
    public static void main(String[] args) {
        deveInicializarComEstadoCorreto();
        deveRegistrarChamadasAteOLimite();
        deveBloquearChamadasDeChaveInativa();
        deveFazerUpgradeSomenteComLimiteValido();
        deveResetarApenasRequisicoesRealizadas();

        System.out.println("Todos os testes passaram.");
    }

    private static void deveInicializarComEstadoCorreto() {
        ChaveApi chave = new ChaveApi("abc", "Basic", 3);

        assertEquals("abc", chave.getToken(), "token inicial");
        assertEquals("Basic", chave.getPlano(), "plano inicial");
        assertEquals(3, chave.getLimiteRequisicoes(), "limite inicial");
        assertEquals(0, chave.getRequisicoesRealizadas(), "contador inicial");
        assertTrue(chave.isAtiva(), "chave deve iniciar ativa");
    }

    private static void deveRegistrarChamadasAteOLimite() {
        ChaveApi chave = new ChaveApi("abc", "Basic", 2);

        chave.registrarChamada();
        chave.registrarChamada();
        assertEquals(2, chave.getRequisicoesRealizadas(), "contador apos chamadas validas");
        assertThrows(IllegalStateException.class,
                "Acesso negado: Limite de requisições excedido.",
                chave::registrarChamada, "deve bloquear chamada acima do limite");
    }

    private static void deveBloquearChamadasDeChaveInativa() {
        ChaveApi chave = new ChaveApi("abc", "Basic", 2);

        chave.bloquearChave();
        assertTrue(!chave.isAtiva(), "chave deve estar inativa");
        assertThrows(IllegalStateException.class, "Acesso negado: Chave inativa.",
                chave::registrarChamada, "chave inativa nao pode registrar chamada");

        chave.desbloquearChave();
        chave.registrarChamada();
        assertEquals(1, chave.getRequisicoesRealizadas(), "chave desbloqueada deve aceitar chamada");
    }

    private static void deveFazerUpgradeSomenteComLimiteValido() {
        ChaveApi chave = new ChaveApi("abc", "Basic", 2);

        chave.fazerUpgrade("Pro", 5);
        assertEquals("Pro", chave.getPlano(), "plano apos upgrade");
        assertEquals(5, chave.getLimiteRequisicoes(), "limite apos upgrade");

        assertThrows(IllegalArgumentException.class,
                "O novo limite deve ser maior ou igual ao limite atual.",
                () -> chave.fazerUpgrade("Basic", 4),
                "upgrade nao pode reduzir o limite");
        assertEquals("Pro", chave.getPlano(), "plano deve permanecer inalterado");
        assertEquals(5, chave.getLimiteRequisicoes(), "limite deve permanecer inalterado");
    }

    private static void deveResetarApenasRequisicoesRealizadas() {
        ChaveApi chave = new ChaveApi("abc", "Basic", 2);
        chave.registrarChamada();
        chave.bloquearChave();

        chave.resetarCiclo();

        assertEquals(0, chave.getRequisicoesRealizadas(), "reset deve zerar contador");
        assertEquals("abc", chave.getToken(), "reset nao deve alterar token");
        assertEquals("Basic", chave.getPlano(), "reset nao deve alterar plano");
        assertEquals(2, chave.getLimiteRequisicoes(), "reset nao deve alterar limite");
        assertTrue(!chave.isAtiva(), "reset nao deve alterar status");
    }

    private static void assertEquals(Object esperado, Object atual, String mensagem) {
        if (!esperado.equals(atual)) {
            throw new AssertionError(mensagem + ": esperado " + esperado + ", recebido " + atual);
        }
    }

    private static void assertTrue(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }

    private static void assertThrows(Class<? extends RuntimeException> tipoEsperado,
            String mensagemEsperada, Runnable acao, String mensagem) {
        try {
            acao.run();
        } catch (RuntimeException erro) {
            if (tipoEsperado.isInstance(erro)) {
                assertEquals(mensagemEsperada, erro.getMessage(), mensagem + ": mensagem da excecao");
                return;
            }
            throw new AssertionError(mensagem + ": excecao incorreta: "
                    + erro.getClass().getSimpleName());
        }
        throw new AssertionError(mensagem + ": nenhuma excecao foi lancada");
    }
}
