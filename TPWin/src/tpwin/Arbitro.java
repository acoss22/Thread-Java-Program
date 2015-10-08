/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tpwin;

/**
 *
 * @author André
 */
public class Arbitro extends Thread {

    private Jogo jogo;
    private long tempoComeco;
    private int finalPartida;
    private boolean acabado;

    public Arbitro(Jogo jogo, long tempoComeco, int finalPartida) {
        this.jogo = jogo;
        this.acabado = false;
        this.tempoComeco = tempoComeco;
        this.finalPartida = finalPartida;
    }

    public Jogo getJogo() {
        return jogo;
    }

    public void setJogo(Jogo jogo) {
        this.jogo = jogo;
    }

    public long getTempoComeco() {
        return tempoComeco;
    }

    public void setTempoComeco(long tempoComeco) {
        this.tempoComeco = tempoComeco;
    }

    public int getFinalPartida() {
        return finalPartida;
    }

    public void setFinalPartida(int finalPartida) {
        this.finalPartida = finalPartida;
    }

    public void stopMe() {
        acabado = true;
    }

    @Override
    public void run() {
        while (true) {
            if (!acabado) {
                jogo.verHoras(this);
            } else {
                break;
            }
        }
    }

}
