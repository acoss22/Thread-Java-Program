package tpwin;

import java.util.ArrayList;
import java.util.Timer;

public class Jogador extends Thread implements Comparable {

    private int nome;
    private int golosMarcados;
    private int numeroRemates;
    private long posseBola;
    private int faltas;
    private boolean temBola;
    private ArrayList<Bola> bolas;
    private Jogo jogo;
    private Baliza baliza;
    volatile boolean acabado = false;

    public Jogador(int nome, Jogo jogo) {
        this.nome = nome;
        this.golosMarcados = 0;
        this.numeroRemates = 0;
        this.posseBola = 0;
        this.faltas = 0;
        this.jogo = jogo;
        this.baliza = new Baliza(this);
        this.bolas = new ArrayList<>();

    }
    
    public int getNome() {
        return nome;
    }

    public int getFaltas() {
        return faltas;
    }

    public long getPosseBola() {
        return posseBola;
    }

    public void stopMe() {
        acabado = true;
    }

    public int getGolosMarcados() {
        return golosMarcados;
    }

    public boolean possuiBola() {
        return this.bolas.isEmpty();

    }

    public int numeroBolas() {
        return this.bolas.size();
    }

    public void adicionarBola(int i) {
        this.bolas.add(new Bola(i));
    }

    public Bola chutaBola(int i) {
        this.numeroRemates++;
        return this.bolas.remove(i);
    }
    
    public void chutaBola() {
        this.numeroRemates++;
    }
    

    public void recebeBola(Bola bola) {
        this.bolas.add(bola);
    }

    public int golosSofridos() {
        return this.baliza.getNumGolosSofridos();
    }

    public void sofrerGolo() {
        int golosSofridos = golosSofridos();
        this.baliza.setNumGolosSofridos(golosSofridos + 1);
    }

    public void marcouGolo() {
        this.golosMarcados++;
    }

    public void cometeuFalta() {
        this.faltas++;
    }

    public int getNumeroRemates() {
        return this.numeroRemates;
    }

    public void atualizarPosseBola(long posse) {
        this.posseBola += posse;
    }

    public void entregarBola() {
        bolas.removeAll(bolas);
    }
    
    
    
    @Override

    public void run() {
        while (true) {
            if (!acabado) {
                jogo.turno(this);
            } else {
                break;
            }
        }
    }

    @Override
    public int compareTo(Object o) {
        final int MENOR = -1;
        final int IGUAL = 0;
        final int MAIOR = 1;
        if (this.golosMarcados < ((Jogador) o).getGolosMarcados()) {
            return MAIOR;
        }
        if (this.golosMarcados > ((Jogador) o).getGolosMarcados()) {
            return MENOR;
        }

        return IGUAL;
    }

}
