import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean executando = true;

        while (executando) {
            exibirMenuInicial();
            switch (lerInteiro(scanner, "Escolha uma opcao: ")) {
                case 1:
                    executarDemonstracao();
                    break;
                case 2:
                    executarTesteManual(scanner);
                    break;
                case 0:
                    executando = false;
                    System.out.println("Programa encerrado.");
                    break;
                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }

    private static void exibirMenuInicial() {
        System.out.println("\n=== Controle de Cotas de API ===");
        System.out.println("1 - Executar demonstracao automatica");
        System.out.println("2 - Testar uma chave manualmente");
        System.out.println("0 - Sair");
    }

    private static void executarDemonstracao() {
        ChaveApi chave = new ChaveApi("token-demo", "Basic", 2);
        System.out.println("Estado inicial: " + chave);

        chave.registrarChamada();
        chave.registrarChamada();
        System.out.println("Apos duas chamadas: " + chave);

        tentarRegistrarChamada(chave, "limite atingido");

        chave.bloquearChave();
        tentarRegistrarChamada(chave, "chave bloqueada");

        chave.desbloquearChave();
        chave.fazerUpgrade("Pro", 5);
        chave.registrarChamada();
        System.out.println("Apos desbloqueio e upgrade: " + chave);

        tentarFazerUpgrade(chave, "Basic", 4);

        chave.resetarCiclo();
        System.out.println("Apos reset do ciclo: " + chave);
    }

    private static void executarTesteManual(Scanner scanner) {
        System.out.print("Token: ");
        String token = scanner.nextLine();
        String plano = lerPlano(scanner, "Plano inicial");
        int limite = lerInteiro(scanner, "Limite de requisicoes: ");
        ChaveApi chave = new ChaveApi(token, plano, limite);
        boolean testando = true;

        while (testando) {
            exibirMenuChave(chave);
            switch (lerInteiro(scanner, "Escolha uma opcao: ")) {
                case 1:
                    try {
                        chave.registrarChamada();
                        System.out.println("Chamada registrada com sucesso.");
                    } catch (IllegalStateException erro) {
                        System.out.println(erro.getMessage());
                    }
                    break;
                case 2:
                    chave.bloquearChave();
                    System.out.println("Chave bloqueada.");
                    break;
                case 3:
                    chave.desbloquearChave();
                    System.out.println("Chave desbloqueada.");
                    break;
                case 4:
                    String novoPlano = lerPlano(scanner, "Novo plano");
                    int novoLimite = lerInteiro(scanner, "Novo limite de requisicoes: ");
                    try {
                        chave.fazerUpgrade(novoPlano, novoLimite);
                        System.out.println("Upgrade realizado com sucesso.");
                    } catch (IllegalArgumentException erro) {
                        System.out.println(erro.getMessage());
                    }
                    break;
                case 5:
                    chave.resetarCiclo();
                    System.out.println("Ciclo resetado com sucesso.");
                    break;
                case 6:
                    System.out.println(chave);
                    break;
                case 0:
                    testando = false;
                    break;
                default:
                    System.out.println("Opcao invalida.");
            }
        }
    }

    private static void exibirMenuChave(ChaveApi chave) {
        System.out.println("\n=== Teste Manual ===");
        System.out.println("Chave atual: " + chave);
        System.out.println("1 - Registrar chamada");
        System.out.println("2 - Bloquear chave");
        System.out.println("3 - Desbloquear chave");
        System.out.println("4 - Fazer upgrade");
        System.out.println("5 - Resetar ciclo");
        System.out.println("6 - Exibir estado da chave");
        System.out.println("0 - Voltar ao menu inicial");
    }

    private static int lerInteiro(Scanner scanner, String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String entrada = scanner.nextLine();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException erro) {
                System.out.println("Informe um numero inteiro valido.");
            }
        }
    }

    private static String lerPlano(Scanner scanner, String mensagem) {
        while (true) {
            System.out.println(mensagem + ":");
            System.out.println("1 - Basic");
            System.out.println("2 - Pro");
            System.out.println("3 - Enterprise");

            switch (lerInteiro(scanner, "Escolha o plano: ")) {
                case 1:
                    return "Basic";
                case 2:
                    return "Pro";
                case 3:
                    return "Enterprise";
                default:
                    System.out.println("Opcao de plano invalida.");
            }
        }
    }

    private static void tentarRegistrarChamada(ChaveApi chave, String contexto) {
        try {
            chave.registrarChamada();
        } catch (IllegalStateException erro) {
            System.out.println("Erro esperado (" + contexto + "): " + erro.getMessage());
        }
    }

    private static void tentarFazerUpgrade(ChaveApi chave, String plano, int limite) {
        try {
            chave.fazerUpgrade(plano, limite);
        } catch (IllegalArgumentException erro) {
            System.out.println("Erro esperado (upgrade invalido): " + erro.getMessage());
        }
    }
}
