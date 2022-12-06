package pgdp.tictactoe.ai;

import java.util.Random;

import pgdp.tictactoe.Field;
import pgdp.tictactoe.Move;
import pgdp.tictactoe.PenguAI;

public class SimpleAI extends PenguAI {

    private Random random;

    public SimpleAI() {
        random = new Random();
    }

    @Override
    public Move makeMove(Field[][] board, boolean firstPlayer, boolean[] firstPlayedPieces,
            boolean[] secondPlayedPieces) {
        return new Move(random.nextInt(board.length), random.nextInt(board.length),
                random.nextInt(firstPlayedPieces.length));
    }
}
