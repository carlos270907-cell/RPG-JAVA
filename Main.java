import java.util.Random;
import java.util.Scanner;

public class Main {

    // ===================== RECURSOS COMPARTILHADOS =====================
    static final Scanner sc = new Scanner(System.in);
    static final Random random = new Random();

    // ===================== ESTADO DO JOGADOR =====================
    static String nome;
    static int vida, vidaMaxima, ataque, defesa;
    static int energia = 0;
    static boolean temArma = false;
    static boolean temArmadura = false;
    static boolean jogoAtivo = true;

    // PARTE 2 - contador de inimigos mortos
    static int inimigosMortos = 0;

    // PARTE 3 - novas funcionalidades: nível/XP, poções, ataque especial + crítico (D20)
    static int nivel = 1;
    static int xp = 0;
    static int pocoes = 0;

    // ===================== TELAS / ENTRADAS =====================
    public static void mostrarInicio() {
        System.err.println("*******************");
        System.err.println("     JOGO RPG      ");
        System.err.println("*******************");
    }

    public static int escolhaPersonagem() {
        int opcaoClasse;
        do {
            System.out.println("Escolha sua classe :");
            System.out.println("1- Guerreiro");
            System.out.println("2- Arqueiro");
            System.out.println("3- Bardo ");
            System.out.println("4- Mago");
            opcaoClasse = sc.nextInt();
        } while (opcaoClasse <= 0 || opcaoClasse >= 5);
        return opcaoClasse;
    }

    public static void definirAtributos(int opcao) {
        if (opcao == 1) { // Guerreiro
            vida = 120; ataque = 80; defesa = 50;
        } else if (opcao == 2) { // Arqueiro
            vida = 100; ataque = 70; defesa = 40;
        } else if (opcao == 3) { // Bardo
            vida = 80; ataque = 50; defesa = 60;
        } else { // Mago
            vida = 90; ataque = 40; defesa = 20;
        }
        vidaMaxima = vida;
    }

    public static int mostrarMenuPrincipal() {
        System.out.println("Inicio da Jornada");
        System.out.println("1-Explorar");
        System.out.println("2-Ver Status");
        System.out.println("3-Recuperar");
        System.out.println("4-Usar Poção");
        System.out.println("0-Sair");
        return sc.nextInt();
    }

    // ===================== EXPLORAÇÃO =====================
    public static int explorar() {
        /*
         *  1 - Encontrou um inimigo (40%)
         *  2 - Encontrou uma energia (20%)
         *  3 - Segue sua vidinha (35%)
         *  4 - Encontrou um item raro (5%)
         */
        int sorteio = random.nextInt(100) + 1;

        if (sorteio <= 40) {
            return 1;
        } else if (sorteio <= 60) {
            return 2;
        } else if (sorteio <= 95) {
            return 3;
        } else {
            return 4;
        }
    }

    public static void tratarExploracao() {
        int resultado = explorar();

        if (resultado == 1) {
            eventoInimigo();
        } else if (resultado == 2) {
            eventoEnergia();
        } else if (resultado == 3) {
            System.out.println("Você deve seguir sua vidinha");
        } else {
            eventoItemRaro();
        }
    }

    public static void eventoEnergia() {
        System.out.println("Você encontrou um bau de energia");
        if (energia < 10) {
            energia += 1;
        } else {
            System.out.println("Energia Cheia !");
        }
    }

    public static void eventoItemRaro() {
        System.out.println("Sortudinho, ganhou um item raro");
        int itemSorteado = random.nextInt(2) + 1;

        if (itemSorteado == 1) {
            if (temArmadura) {
                System.out.println("Você já tem, rala");
            } else {
                temArmadura = true;
            }
        } else {
            if (temArma) {
                System.out.println("Você ja ta armado !");
            } else {
                temArma = true;
            }
        }
    }

    // ===================== COMBATE =====================
    public static int sortearDadoD20() {
        return random.nextInt(20) + 1;
    }

    public static int ataqueTotal() {
        return temArma ? ataque + 20 : ataque;
    }

    public static int defesaTotal() {
        return temArmadura ? defesa + 20 : defesa;
    }

    public static void eventoInimigo() {
        System.out.println("Você encontrou um Goblin");
        combater();
    }

    public static void combater() {
        int monstroHasteroide = random.nextInt(20) + 1;
        int vidaMonstro = random.nextInt(80) + 40 + monstroHasteroide;
        int ataqueMonstro = random.nextInt(30) + 5 + monstroHasteroide;

        if (vidaMonstro >= 100) {
            System.out.println("Você encontrou o Marcelo Goblin !");
        }

        System.out.println("Seu inimigo tem : ");
        System.out.println("Vida: " + vidaMonstro);
        System.out.println("Ataque: " + ataqueMonstro);

        while (vidaMonstro > 0 && vida > 0) {
            boolean defendendo = false;

            System.out.println("\nSua vida: " + vida + " | Vida do inimigo: " + vidaMonstro);
            System.out.println("Você quer fazer oque ?");
            System.out.println("1- Atacar");
            System.out.println("2- Defender");
            System.out.println("3- Fugir");
            System.out.println("4- Ataque Especial (custa 3 de energia)");
            System.out.println("5- Usar Poção (" + pocoes + ")");
            int opcaoLuta = sc.nextInt();

            if (opcaoLuta == 1) {
                vidaMonstro -= atacarInimigo(1);
            } else if (opcaoLuta == 2) {
                defendendo = true;
                System.out.println("Você se prepara para o golpe!");
            } else if (opcaoLuta == 3) {
                System.out.println("Seu Babaca");
                return;
            } else if (opcaoLuta == 4) {
                if (energia >= 3) {
                    energia -= 3;
                    System.out.println("ATAQUE ESPECIAL!");
                    vidaMonstro -= atacarInimigo(2);
                } else {
                    System.out.println("Energia insuficiente!");
                    continue;
                }
            } else if (opcaoLuta == 5) {
                if (!usarPocao()) {
                    continue;
                }
            } else {
                System.out.println("Opção Invalida");
                continue;
            }

            if (vidaMonstro > 0) {
                turnoInimigo(ataqueMonstro, defendendo);
            }
        }

        if (vida <= 0) {
            vida = 0;
            jogoAtivo = false;
        } else {
            vencerInimigo();
        }
    }

    // Dado D20: 1 = erra, 20 = crítico (dano dobrado)
    public static int atacarInimigo(int multiplicador) {
        int dado = sortearDadoD20();
        System.out.println("Dado D20: " + dado);

        if (dado == 1) {
            System.out.println("Você errou o golpe!");
            return 0;
        }

        int dano = (ataqueTotal() / 2 + dado) * multiplicador;
        if (dado == 20) {
            System.out.println("CRÍTICO!");
            dano *= 2;
        }
        System.out.println("Você causou " + dano + " de dano!");
        return dano;
    }

    public static void turnoInimigo(int ataqueMonstro, boolean defendendo) {
        int dano = Math.max(1, ataqueMonstro - defesaTotal() / 3);
        if (defendendo) {
            dano = Math.max(1, dano / 2);
        }
        vida -= dano;
        System.out.println("O inimigo causou " + dano + " de dano em você!");
    }

    public static void vencerInimigo() {
        inimigosMortos++; // PARTE 2
        System.out.println("Você derrotou o inimigo! (Total de mortos: " + inimigosMortos + ")");

        ganharXp(20 + random.nextInt(21));

        if (random.nextInt(100) < 30) {
            pocoes++;
            System.out.println("O inimigo dropou uma poção! (Poções: " + pocoes + ")");
        }
    }

    // ===================== PARTE 3: NÍVEL / XP =====================
    public static void ganharXp(int ganho) {
        xp += ganho;
        System.out.println("Você ganhou " + ganho + " de XP!");

        while (xp >= nivel * 50) {
            xp -= nivel * 50;
            nivel++;
            vidaMaxima += 10;
            ataque += 5;
            defesa += 3;
            vida = vidaMaxima;
            System.out.println("LEVEL UP! Você agora é nível " + nivel + "!");
        }
    }

    // ===================== PARTE 3: POÇÕES =====================
    public static boolean usarPocao() {
        if (pocoes <= 0) {
            System.out.println("Você não tem poções!");
            return false;
        }
        if (vida >= vidaMaxima) {
            System.out.println("Sua vida já está cheia!");
            return false;
        }
        pocoes--;
        vida = Math.min(vidaMaxima, vida + 40);
        System.out.println("Você usou uma poção e recuperou vida! Vida: " + vida);
        return true;
    }

    // ===================== STATUS / RECUPERAR =====================
    public static void mostrarStatus() {
        System.out.println("Olá " + nome);
        System.out.println("Nível: " + nivel + " (XP: " + xp + "/" + (nivel * 50) + ")");
        System.out.println("Ataque: " + ataque);
        System.out.println("Defesa: " + defesa);
        System.out.println("Vida: " + vida + "/" + vidaMaxima);
        System.out.println("Energia: " + energia);
        System.out.println("Poções: " + pocoes);
        System.out.println("Inimigos mortos: " + inimigosMortos);
        System.out.println("Arma: " + (temArma ? "Sim" : "Não"));
        System.out.println("Armadura: " + (temArmadura ? "Sim" : "Não"));
    }

    public static void recuperar() throws InterruptedException {
        vida += 10;

        if (vida >= vidaMaxima) {
            vida = vidaMaxima;
            System.out.println("Vida maxima atingida !");
        } else {
            System.out.println("Vida aumentada !");
        }

        Thread.sleep(2000);
        System.out.println("Ainda estamos recuperando");

        Thread.sleep(3000);
        System.out.println("Energia Recuperada !");
        energia += 1;

        System.out.println("Vida e energia restaurados");
    }

    public static void mostrarFimDeJogo() {
        System.out.println("\n===== GAME OVER =====");
        System.out.println(nome + " morreu no nível " + nivel + ".");
        System.out.println("Inimigos mortos antes de morrer: " + inimigosMortos);
    }

    // ===================== MAIN =====================
    public static void main(String[] args) throws InterruptedException {
        mostrarInicio();

        System.out.println("Informe o nome do seu personagem");
        nome = sc.next();
        System.out.println("Seja bem-vindo " + nome + " ! ");

        definirAtributos(escolhaPersonagem());

        int opcaoJogo = -1;

        while (opcaoJogo != 0 && jogoAtivo) {
            opcaoJogo = mostrarMenuPrincipal();

            if (opcaoJogo == 1) {
                tratarExploracao();
            } else if (opcaoJogo == 2) {
                mostrarStatus();
            } else if (opcaoJogo == 3) {
                recuperar();
            } else if (opcaoJogo == 4) {
                usarPocao();
            } else {
                break;
            }
        }

        if (!jogoAtivo) {
            mostrarFimDeJogo();
        }
    }

}