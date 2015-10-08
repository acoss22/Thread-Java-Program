package tpwin;

import java.util.Scanner;

public class TPWin {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int jogadores;
        int bolas;

        do {
            System.out.println("Introduza o numero de jogadores:");
            jogadores = sc.nextInt();
        } while (jogadores <= 2 || jogadores > 10);

        do {
            System.out.println("Introduza o numero de bolas:");
            bolas = sc.nextInt();
        } while (bolas <= 0 || bolas > jogadores);

        Jogo jogo = new Jogo(jogadores, bolas);
        jogo.inicioPartida();
    }

}
