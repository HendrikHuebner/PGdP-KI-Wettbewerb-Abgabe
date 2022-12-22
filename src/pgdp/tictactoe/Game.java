package pgdp.tictactoe;

import pgdp.tictactoe.ai.CompetitionAI;
import pgdp.tictactoe.ai.HumanPlayer;
import pgdp.tictactoe.ai.SimpleAI;

public class Game {

    private Field[][] field = new Field[3][3];

    public boolean[] getFirstPlayedPieces() {
        return firstPlayedPieces;
    }

    public boolean[] getSecondPlayedPieces() {
        return secondPlayedPieces;
    }

    public void setFirstPlayedPieces(boolean[] firstPlayedPieces) {
        this.firstPlayedPieces = firstPlayedPieces;
    }

    public void setSecondPlayedPieces(boolean[] secondPlayedPieces) {
        this.secondPlayedPieces = secondPlayedPieces;
    }

    private boolean[] firstPlayedPieces = new boolean[9];
    private boolean[] secondPlayedPieces = new boolean[9];
    private PenguAI first;
    private PenguAI second;
    private PenguAI winner = null;

    public Game(PenguAI first, PenguAI second) {
        this.first = first;
        this.second = second;
    }

    public void playGame() {
        while(this.winner == null) {
            this.step();
        }
    }

    public void setField(Field[][] field) {
        this.field = field;
    }

    public Field[][] getField() {
        return this.field;
    }

    public void step() {
        validate(true, first.makeMove(field, true, firstPlayedPieces, secondPlayedPieces));
        printBoard(field);
        this.winner = checkForWinner();

        if(this.winner != null) return;

        validate(false, second.makeMove(field, false, firstPlayedPieces, secondPlayedPieces));
        printBoard(field);
        this.winner = checkForWinner();

        if(this.winner != null) return;
    }

    private PenguAI checkForWinner() {
        for(int i = 0; i < 3; i++) if(field[i][0] != null && field[i][1] != null && field[i][2] != null && field[i][0].firstPlayer() == field[i][1].firstPlayer() && field[i][1].firstPlayer() == field[i][2].firstPlayer())
            return field[i][0].firstPlayer() ? first : second;
        for(int i = 0; i < 3; i++) if(field[0][i] != null && field[1][i] != null && field[2][i] != null && field[0][i].firstPlayer() == field[1][i].firstPlayer() && field[1][i].firstPlayer() == field[2][i].firstPlayer())
            return field[0][i].firstPlayer() ? first : second;
        if(field[0][0] != null && field[1][1] != null && field[2][2] != null && field[0][0].firstPlayer() == field[1][1].firstPlayer() && field[1][1].firstPlayer() == field[2][2].firstPlayer())
            return field[0][0].firstPlayer() ? first : second;
        if(field[0][2] != null && field[1][1] != null && field[2][0] != null && field[0][2].firstPlayer() == field[1][1].firstPlayer() && field[1][1].firstPlayer() == field[2][0].firstPlayer())
            return field[0][2].firstPlayer() ? first : second;
        return null;
    }

    private void validate(boolean first, Move move) {
        if(move.x() < 0 || move.x() >= 3 || move.y() < 0 || move.y() >= 3
                || move.value() < 0 || move.value() >= 9
                || (first && firstPlayedPieces[move.value()])
                || (!first && secondPlayedPieces[move.value()])
                || field[move.x()][move.y()] != null && field[move.x()][move.y()].firstPlayer() == first
                || field[move.x()][move.y()] != null && field[move.x()][move.y()].value() >= move.value()) {
            //invalid
            this.winner = first ? this.second : this.first;
        } else {
            field[move.x()][move.y()] = new Field(move.value(), first);
            if(first) {
                firstPlayedPieces[move.value()] = true;
            } else {
                secondPlayedPieces[move.value()] = true;
            }
        }
    }

    public PenguAI getWinner() {
        return winner;
    }

    public static void printBoard(Field[][] board) {
        System.out.println("┏━━━┳━━━┳━━━┓");
        for (int y = 0; y < board.length; y++) {
            System.out.print("┃");
            for (int x = 0; x < board.length; x++) {
                if (board[x][y] != null) {
                    System.out.print(board[x][y] + "┃");
                } else {
                    System.out.print("   ┃");
                }
            }
            System.out.println();
            if (y != board.length - 1) {
                System.out.println("┣━━━╋━━━╋━━━┫");
            }
        }
        System.out.println("┗━━━┻━━━┻━━━┛");
    }

    public static void main(String[] args) {
        PenguAI firstPlayer = new CompetitionAI();
        PenguAI secondPlayer = new HumanPlayer();
        Game game = new Game(firstPlayer, secondPlayer);
        game.setFirstPlayedPieces(new boolean[] {false, false, false, false, false, false, false, false, true});
        game.setSecondPlayedPieces(new boolean[] {false, false, false, false, false, false, false, false, true});
        game.field[0][0] = new Field(8, true);
        game.field[2][0] = new Field(8, false);
        game.playGame();

        if(firstPlayer == game.getWinner()) {
            System.out.println("Herzlichen Glückwunsch erster Spieler");
        } else if(secondPlayer == game.getWinner()) {
            System.out.println("Herzlichen Glückwunsch zweiter Spieler");
        } else {
            System.out.println("Unentschieden");
        }
    }


}
