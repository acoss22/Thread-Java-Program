package tpwin;

import static java.lang.Thread.sleep;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Jogo {

    Scanner sc = new Scanner(System.in);
    Random rd = new Random();
    private static final int TEMPO_POSSE = 500;
    private static final int CHANCE_MARCAR = 1;
    public final static int PROLONGAMENTO = 5000;
    public final static int TEMPO_JOGO = 30000;
    private Arbitro arbitroInicial;
    private ArrayList<Jogador> jogadores;
    private ArrayList<Jogador> vencidos;
    private ArrayList<Jogador> podio;
    int nomes = 1;
    private boolean houvePL, houveMS;
    private int nBolas;
    private int interrupcoes;

    public Jogo(int nJogadores, int nBolas) {
        this.jogadores = new ArrayList<>();
        this.vencidos = new ArrayList<>();
        this.podio = new ArrayList<>();
        this.nBolas = nBolas;
        this.interrupcoes = 0;
        for (; nomes <= nJogadores; nomes++) {
            jogadores.add(new Jogador(nomes, this));

        }
        arbitroInicial = new Arbitro(this, System.currentTimeMillis(), TEMPO_JOGO);
    }

    public void inicioPartida() {
        AtribuiBolas(nBolas);
        System.out.println("A Partida Vai começar!");
        arbitroInicial.start();
        for (int i = 0; i < nomes - 1; i++) {
            jogadores.get(i).start();

        }

    }

    public void AtribuiBolas(int nBolas) {
        for (int i = 0; i < nBolas; i++) {
            int random = rd.nextInt(jogadores.size());
            if (jogadores.get(random).possuiBola()) {
                jogadores.get(random).adicionarBola(i);
            }
        }
    }

    public void retirarBolas() {
        for (Jogador jogador : jogadores) {
            jogador.entregarBola();
        }
    }

    public boolean pararam() {
        for (int i = 0; i < jogadores.size(); i++) {
            if (!jogadores.get(i).acabado) {
                return false;
            }

        }
        return true;
    }

    public void verHoras(Arbitro arbitro) {
        long estimatedTime = System.currentTimeMillis() - arbitro.getTempoComeco();
        if (estimatedTime > arbitro.getFinalPartida()) {
            if (verificarResultados()) {
                for (int i = 0; i < jogadores.size(); i++) {
                    jogadores.get(i).stopMe();
                }
                arbitro.stopMe();
                podio();
            } else {
                if (interrupcoes > 0) {
                    for (int i = 0; i < jogadores.size(); i++) {
                        jogadores.get(i).stopMe();
                    }
                    arbitro.stopMe();
                    comecarMS();
                    podio();
                } else {
                    interrupcoes++;
                    comecarProlongamento();
                }
            }
        }
    }

    public boolean verificarResultados() {
        int igualdade = -1;
        int maxGolos = maxGolosMarcados();

        for (int i = 0; i < jogadores.size(); i++) {
            if (jogadores.get(i).getGolosMarcados() == maxGolos) {
                igualdade++;
            }
        }
        return igualdade <= 0;
    }

    public void morteSubita() throws InterruptedException {
        int ronda = 1;
        do {
            System.out.println("O arbitro vai dar inicio a ronda " + ronda);
            for (int i = 0; i < jogadores.size(); i++) {
                System.out.println("O jogador " + jogadores.get(i).getNome() + " vai chutar");
                int jogaDef = calcularJogador(jogadores.get(i));

                System.out.println((char) 27 + "\033[34mO jogador " + jogadores.get(i).getNome() + " chutou a bola a baliza do jogador " + getJogadorByIndex(jogaDef) + "\033");
                if (marcou(jogadores.get(i), jogadores.get(jogaDef))) {
                    System.out.println((char) 27 + "\033[32m O jogador " + jogadores.get(i).getNome() + " marcou!\033");
                    jogadores.get(i).chutaBola();

                } else {
                    System.out.println((char) 27 + "\033[31m O jogador " + getJogadorByIndex(jogaDef) + " defendeu!\033");
                    jogadores.get(i).chutaBola();
                }
                Thread.sleep(500);
            }

            System.out.println("Acabou a ronda " + ronda);
            ronda++;

            Thread.sleep(1000);

        } while (verificarResultados() == false);
    }

    public void comecarMS() {
        houveMS = true;
        System.out.println("Houve um empate.");
        System.out.println("O arbitro vai dar inicio a morte súbita.");
        retirarBolas();
        System.out.println("Os jogadores entregaram as bolas em jogo.");
        selecionarJogadores();
        System.out.println("O arbitro selecionou os jogadores que podem ir a morte súbita.");
        System.out.println("Inicio da morte súbita!");
        try {
            morteSubita();
        } catch (InterruptedException ex) {
            Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void comecarProlongamento() {
        houvePL = true;
        System.out.println("Houve um empate.");
        System.out.println("O arbitro vai dar inicio ao prolongamento.");
        retirarBolas();
        System.out.println("Os jogadores entregaram as bolas em jogo.");
        selecionarJogadores();
        System.out.println("O arbitro selecionou os jogadores que podem ir a prolongamento.");
        AtribuiBolas(this.nBolas);
//        try {
//            sleep(500);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
//        }
        arbitroInicial.setFinalPartida(PROLONGAMENTO);
        arbitroInicial.setTempoComeco(System.currentTimeMillis());
        System.out.println("Inicio do prolongamento!");
    }

    public int maxGolosMarcados() {
        int maxGolos = 0;
        for (int i = 0; i < jogadores.size(); i++) {
            if (jogadores.get(i).getGolosMarcados() > maxGolos) {
                maxGolos = jogadores.get(i).getGolosMarcados();
            }

        }
        return maxGolos;
    }

    public void selecionarJogadores() {
        int maxGolos = maxGolosMarcados();
        Jogador vencido;

        for (int i = 0; i < jogadores.size(); i++) {
            if (jogadores.get(i).getGolosMarcados() != maxGolos) {
                jogadores.get(i).stopMe();
                vencido = jogadores.get(i);
                vencidos.add(vencido);
                jogadores.remove(i);
                i--;
            }
        }
    }

    public void podio() {
        int remates = 0;
        char op;
        podio.addAll(vencidos);
        podio.addAll(jogadores);
        Collections.sort(podio);
        for (int i = 0; i < podio.size(); i++) {
            remates += podio.get(i).getNumeroRemates();
        }
        System.out.println(String.format("%.80s", "******************* Pódio *******************"));
        System.out.println(String.format("%.80s", "                   Jogador " + podio.get(0).getNome() + "               "));
        System.out.println(String.format("%.80s", "              |---------------| "));
        System.out.println(String.format("%.80s", "              |     < 1 >     |  Jogador " + podio.get(1).getNome()));
        System.out.println(String.format("%.80s", "              |---------------|--------------|"));
        System.out.println(String.format("%.80s", "   Jogador " + podio.get(2).getNome() + "  |               |     < 2 >    |"));
        System.out.println(String.format("%.80s", "|-------------|               |--------------|"));
        System.out.println(String.format("%.80s", "|    < 3 >    |               |              |"));
        System.out.println(String.format("%.80s", "|-------------|               |              |"));
        System.out.println(String.format("%.80s", "|             |               |              |"));
        System.out.println(String.format("%.80s", "|             |               |              |"));
        System.out.println(String.format("%.80s", "|_____________|_______________|______________|"));
        System.out.println(" ");
        for (int i = 3; i < podio.size(); i++) {
            System.out.println(String.format("%.80s", (i + 1) + "º Lugar - Jogador " + podio.get(i).getNome()));
        }
        System.out.println(String.format("%.80s", "**************** Estatísticas Gerais ****************\n"));
        statsFasesJogo();
        statsRemates();
        statsGolos();
        System.out.println("*****************************************************");
        System.out.println(String.format("%.80s", "Deseja ver as estatisticas pormenorizadas? (s/n)"));
        do {
            op = sc.nextLine().charAt(0);
        } while (op != 's' && op != 'n');
        if (op == 's') {
            menuStats();
        }
        if (op == 'n') {

        }
    }

    public void statsRemates() {
        int remates = 0;
        for (int i = 0; i < podio.size(); i++) {
            remates += podio.get(i).getNumeroRemates();
        }
        System.out.println("-> Número total de remates: " + remates + "\n");
    }

    public void statsGolos() {
        int golos = 0;
        for (int i = 0; i < podio.size(); i++) {
            golos += podio.get(i).getGolosMarcados();
        }
        System.out.println("-> Número total de golos: " + golos + "\n");
    }

    public void menuStats() {
        int op;
        System.out.println("Escolha um jogador: ");
        for (int i = 0; i < podio.size(); i++) {
            System.out.println("-> Jogador " + (i + 1));
        }
        System.out.println("-> Sair - 0");
        do {
            System.out.print("Opção: ");
            op = sc.nextInt();
        } while (op < 0 || op > podio.size());
        if (op == 0) {
            System.out.println("Obrigado por testar o jogo.");
        } else {
            statsJogador(op);
        }
    }

    public void statsJogador(int nome) {
        Jogador jogador = new Jogador(0, this);
        for (Jogador podio1 : podio) {
            if (nome == podio1.getNome()) {
                jogador = podio1;
            }
        }
        float presisao = (float) jogador.getGolosMarcados() / (float) jogador.getNumeroRemates();
        System.out.println("--------------------------------------------------");
        System.out.println("Jogador " + (jogador.getNome()));
        System.out.println("Golos marcados:   " + jogador.getGolosMarcados());
        System.out.println("Golos sofridos:   " + jogador.golosSofridos());
        System.out.println("Remates:          " + jogador.getNumeroRemates());
        System.out.println("Presisão Remate:  " + (Math.round(presisao * 100) * 100) / 100 + "%");
        System.out.println("Faltas cometidas: " + jogador.getFaltas());
        System.out.println("Posse bola:       " + ((double) jogador.getPosseBola() / (double) 1000) + "min");
        System.out.println("--------------------------------------------------");
        menuStats();
    }

    public void statsFasesJogo() {
        if (houvePL) {
            System.out.println("-> Houve prolongamento neste jogo\n");
        } else {
            System.out.println("-> Não houve prolongamento neste jogo\n");
        }
        if (houveMS) {
            System.out.println("-> Houve morte súbita neste jogo\n");
        } else {
            System.out.println("-> Não houve morte súbita neste jogo\n");
        }
    }

    public boolean houveGolos() {
        for (Jogador jogadore : jogadores) {
            if (jogadore.getGolosMarcados() > 0) {
                return true;
            }
        }
        return false;
    }

    public void calculoPosse(Jogador jogador, long comecou) {
        long acabou = System.currentTimeMillis();
        long calculo = acabou - comecou;
        if (calculo > TEMPO_POSSE) {
            jogador.cometeuFalta();
        }
        jogador.atualizarPosseBola(calculo);
    }

    public boolean marcou(Jogador jogadorOfensivo, Jogador jogadorDefensivo) {
        int remate = rd.nextInt(100) + 1;
        if (remate <= CHANCE_MARCAR) {
            jogadorOfensivo.marcouGolo();
            jogadorDefensivo.sofrerGolo();
            return true;
        } else {
            return false;
        }
    }

    public synchronized void turno(Jogador jogador) {
        try {

            if (!jogador.possuiBola()) {
                long comecou = System.currentTimeMillis();
                System.out.println("O jogador " + jogador.getNome() + " tem " + jogador.numeroBolas() + " bola(s)");

                for (int i = 0; i < jogador.numeroBolas();) {

                    int jogaDef = calcularJogador(jogador);

                    System.out.println((char) 27 + "\033[34mO jogador " + jogador.getNome() + " chutou a bola a baliza do jogador " + getJogadorByIndex(jogaDef) + "\033");
                    if (marcou(jogador, jogadores.get(jogaDef))) {
                        System.out.println((char) 27 + "\033[32m O jogador " + jogador.getNome() + " marcou!\033");
                        Bola aux = jogador.chutaBola(i);
                        jogadores.get(jogaDef).recebeBola(aux);

                    } else {
                        System.out.println((char) 27 + "\033[31m O jogador " + getJogadorByIndex(jogaDef) + " defendeu!\033");
                        Bola aux = jogador.chutaBola(i);
                        jogadores.get(jogaDef).recebeBola(aux);
                    }
                }
                int random = rd.nextInt(TEMPO_POSSE - 100) + 150;
                Thread.sleep(random);
                calculoPosse(jogador, comecou);
                notifyAll();
            } else {
                wait();
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(Jogo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int calcularJogador(Jogador jogador) {
        int target;

        do {
            target = rd.nextInt(jogadores.size());
        } while (getIndexOfJogador(jogador.getNome()) == target);
        return target;
    }

    public int getIndexOfJogador(int nome) {
        for (int i = 0; i < jogadores.size(); i++) {
            if (jogadores.get(i).getNome() == nome) {
                return i;
            }
        }
        return -1;
    }

    public int getJogadorByIndex(int index) {
        return jogadores.get(index).getNome();
    }

}
