package pgdp.tictactoe;

// Diese Klasse darf nicht verändert werden
public abstract class PenguAI {

    public abstract Move makeMove(Field[][] board, boolean firstPlayer, boolean[] firstPlayedPieces,
            boolean[] secondPlayedPieces);
}
