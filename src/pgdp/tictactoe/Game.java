package pgdp.tictactoe;

import pgdp.tictactoe.ai.HumanPlayer;

import java.util.Random;

public class Game {

    public Field[][] board = new Field[3][3];

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
        while(this.winner == null && !checkForDraw()) {
            this.step();
        }
    }

    public void playGame(boolean firstStarts) {
        if(!firstStarts) {
            validate(false, second.makeMove(board, false, firstPlayedPieces, secondPlayedPieces));
            if(this.winner != null) {
                System.out.println("invalid move");
                return;
            }

            printBoard(board);

            this.winner = checkForWinner(true);
        }

        playGame();
    }

    public void setBoard(Field[][] board) {
        this.board = board;
    }

    public Field[][] getBoard() {
        return this.board;
    }

    public void step() { //TODO: draw when no moves
        validate(true, first.makeMove(board, true, firstPlayedPieces, secondPlayedPieces));
        if(this.winner != null) {
            System.out.println("invalid move");
            return;
        }
        printBoard(board);

        this.winner = checkForWinner(false);
        if(this.winner != null) return;

        validate(false, second.makeMove(board, false, firstPlayedPieces, secondPlayedPieces));
        if(this.winner != null) {
            System.out.println("invalid move");
            return;
        }

        printBoard(board);

        this.winner = checkForWinner(true);
    }

    private PenguAI checkForWinner(boolean firstPlayer) {
        if(checkForCantMove(firstPlayer)) return firstPlayer ? second : first;

        for(int i = 0; i < 3; i++) if(board[i][0] != null && board[i][1] != null && board[i][2] != null && board[i][0].firstPlayer() == board[i][1].firstPlayer() && board[i][1].firstPlayer() == board[i][2].firstPlayer())
            return board[i][0].firstPlayer() ? first : second;
        for(int i = 0; i < 3; i++) if(board[0][i] != null && board[1][i] != null && board[2][i] != null && board[0][i].firstPlayer() == board[1][i].firstPlayer() && board[1][i].firstPlayer() == board[2][i].firstPlayer())
            return board[0][i].firstPlayer() ? first : second;
        if(board[0][0] != null && board[1][1] != null && board[2][2] != null && board[0][0].firstPlayer() == board[1][1].firstPlayer() && board[1][1].firstPlayer() == board[2][2].firstPlayer())
            return board[0][0].firstPlayer() ? first : second;
        if(board[0][2] != null && board[1][1] != null && board[2][0] != null && board[0][2].firstPlayer() == board[1][1].firstPlayer() && board[1][1].firstPlayer() == board[2][0].firstPlayer())
            return board[0][2].firstPlayer() ? first : second;
        return null;
    }

    private boolean checkForDraw() {
        for(int i = 0; i < 9; i++){
            if(!firstPlayedPieces[i] || !secondPlayedPieces[i])
                return false;
        }

        return true;
    }

    private boolean checkForCantMove(boolean firstPlayer) {
        int highest = -1;
        for(int i = 8; i >= 0; i--) {
            if(!(firstPlayer ? firstPlayedPieces[i] : secondPlayedPieces[i])) {
                highest = i;
                break;
            }
        }

        for(int i = 0; i < 9; i++){
            Field f = board[i / 3][i % 3];
            if(f == null) return false;
            if(f.firstPlayer() != firstPlayer && f.value() < highest) return false;
        }

        return true;
    }

    private void validate(boolean first, Move move) {
        if(move == null) {
            this.winner = first ? this.second : this.first;
            return;
        }

        if(move.x() < 0 || move.x() >= 3 || move.y() < 0 || move.y() >= 3
                || move.value() < 0 || move.value() >= 9
                || (first && firstPlayedPieces[move.value()])
                || (!first && secondPlayedPieces[move.value()])
                || board[move.x()][move.y()] != null && board[move.x()][move.y()].firstPlayer() == first
                || board[move.x()][move.y()] != null && board[move.x()][move.y()].value() >= move.value()) {
            //invalid
            this.winner = first ? this.second : this.first;
        } else {
            board[move.x()][move.y()] = new Field(move.value(), first);
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
        PenguAI secondPlayer = new CompetitionAI();

        //tournament(firstPlayer, secondPlayer);

        Game game = new Game(firstPlayer, secondPlayer);


        long t1 = System.nanoTime();
        game.playGame();

        System.out.println("Time taken: " + (System.nanoTime() - t1) / 1000000 + "ms");

        if(firstPlayer == game.getWinner()) {
            System.out.println("Herzlichen Glückwunsch erster Spieler");
        } else if(secondPlayer == game.getWinner()) {
            System.out.println("Herzlichen Glückwunsch zweiter Spieler");
        } else {
            System.out.println("Unentschieden");
        }
    }


    private static void tournament(PenguAI x, PenguAI o) {
        int winX = 0;
        int winO = 0;
        int draw = 0;


        boolean prev = false;
        for(int i = 0; i < 50; i++) {
            Random r = new Random(i / 2);
            Game game = (i % 2 == 0) ?  new Game(x, o) :  new Game(o, x);
            //random move

            int f = r.nextInt(9);
            int v = r.nextInt(9);

            game.board[f / 3][f % 3] = new Field(v, true);
            game.getFirstPlayedPieces()[v] = true;
            game.playGame(false);

            if(i % 2 == 0) {
                if(game.getWinner() != null) prev = true;
            } else {
                if((game.getWinner() == null) == prev) System.out.println("beep");
                prev = false;
            }

            if (x == game.getWinner()) {
                winX++;
                System.out.println("win for x");
            } else if (o == game.getWinner()) {
                winO++;
                System.out.println("win for o");
            } else {
                draw++;
                System.out.println("draw");
            }
        }

        System.out.println("Played " + (winO + winX + draw) + " games.");
        System.out.println("x wins: " + winX + ", o wins: " + winO + ", draws: " + draw);
    }
}
