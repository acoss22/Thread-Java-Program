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
public class Baliza {

    private Jogador jogador;
    private int numGolosSofridos;

    public Baliza(Jogador jogador) {
        this.jogador = jogador;
        this.numGolosSofridos = 0;
    }

    public Jogador getJogador() {
        return jogador;
    }

    public void setJogador(Jogador jogador) {
        this.jogador = jogador;
    }

    public int getNumGolosSofridos() {
        return numGolosSofridos;
    }

    public void setNumGolosSofridos(int numGolosSofridos) {
        this.numGolosSofridos = numGolosSofridos;
    }

}
