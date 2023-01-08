package pgdp.tictactoe.ai;

import java.util.Random;

import pgdp.tictactoe.*;

public class SimpleAI extends PenguAI {

    private CompetitionAI ai = new CompetitionAI(2);

    public SimpleAI() {}

    @Override
    public Move makeMove(Field[][] board, boolean firstPlayer, boolean[] firstPlayedPieces, boolean[] secondPlayedPieces) {
        Game.printBoard(board);
        return ai.makeMove(board, firstPlayer, firstPlayedPieces, secondPlayedPieces);
    }
}
