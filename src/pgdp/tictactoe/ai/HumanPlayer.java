package pgdp.tictactoe.ai;

import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.stream.IntStream;

import pgdp.tictactoe.Field;
import pgdp.tictactoe.Game;
import pgdp.tictactoe.Move;
import pgdp.tictactoe.PenguAI;

public class HumanPlayer extends PenguAI {
    private Scanner scanner;

    public HumanPlayer() {
        scanner = new Scanner(System.in, Charset.defaultCharset());
    }

    @Override
    public Move makeMove(Field[][] board, boolean firstPlayer, boolean[] firstPlayedPieces,
            boolean[] secondPlayedPieces) {
        System.out.println("Du bist der " + (firstPlayer ? "erste" : "zweite")
                + "Spieler und das Spielbrett sieht aktuell so aus:");
        Game.printBoard(board);
        System.out.println("Der erste Spieler hat noch folgende Steine: "
                + IntStream.range(0, firstPlayedPieces.length).filter(i -> !firstPlayedPieces[i]).boxed().toList());
        System.out.println("Der zweite Spieler hat noch folgende Steine: "
                + IntStream.range(0, secondPlayedPieces.length).filter(i -> !secondPlayedPieces[i]).boxed().toList());

        System.out.println("An welcher x-Koordinate willst du legen?");
        int x = scanner.nextInt();
        System.out.println("An welcher y-Koordinate willst du legen?");
        int y = scanner.nextInt();
        System.out.println("Welchen von deinen Steinen willst du legen?");
        int value = scanner.nextInt();

        return new Move(x, y, value);
    }
}
