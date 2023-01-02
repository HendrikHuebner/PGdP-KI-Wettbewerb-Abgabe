package pgdp.tictactoe.ai;

import pgdp.tictactoe.Field;
import pgdp.tictactoe.Move;
import pgdp.tictactoe.PenguAI;

import java.util.*;

public class CompetitionAI extends PenguAI {

    private int movesPlayed = 0;
    private final int maxDepth;
    private Map<PositionWrapper, Integer> transpositions = new HashMap<>();
    private int duplicates = 0;

    //DEBUG
    private int statesSearched = 0;

    public CompetitionAI(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public CompetitionAI() {
        this.maxDepth = 5;
    }

    /**
     * TODO:
     * <p>
     * Improve state heuristic function
     * Ignore symmetric states
     * Add iterative searching with increasing depth. cut off after 0.9s time limit
     * Add transposition table(if needed!)
     * Sort child states to increase pruning
     * Opening table
     */


    @Override
    public Move makeMove(Field[][] board, boolean firstPlayer, boolean[] firstPlayedPieces, boolean[] secondPlayedPieces) {
        this.duplicates = 0;
        //DEBUG
        int prevSearched = this.statesSearched;
        byte[] current = parseByteArr(board, firstPlayedPieces, secondPlayedPieces);

        int alpha = -99999;
        int beta = 99999;

        Ret ret = alphaBeta(current, maxDepth, alpha, beta, true, firstPlayer);

        //DEBUG
        if(Math.abs(ret.value) > 9999) {
            if(ret.value > 9999){
                System.out.println("Evaluation: Win in " + (99999 - ret.value + 1));
            } else {
                System.out.println("Evaluation: Loss in " + (99999 + ret.value + 1));
            }
        }
        else System.out.println("Evaluation: " + ret.value);
        System.out.println("States searched: " + (statesSearched - prevSearched));
        movesPlayed++;
        System.out.println("Positions checked: " + transpositions.size());
        System.out.println("Duplicates: " + duplicates);
        System.out.println("Percentage: " + (double)duplicates/(statesSearched - prevSearched));
        return getMove(current, ret.moves[maxDepth - 1]);
    }

    private Ret alphaBeta(byte[] current, int depth, int alpha, int beta, boolean max, boolean playerX) {
        this.statesSearched++;

        if(maxDepth - depth <= 2) {

        }

        if (depth == 0) {
            byte[][] arr = new byte[maxDepth][];
            arr[0] = current;
            return new Ret(heuristic(current, playerX), arr);
        }

        int value = -99999;
        byte[] bestChild = null;
        Ret bestRet = null;

        for (var child : getChildren(current, max == playerX)) {
            if(child == null) break;

            if(terminal(child, max == playerX)) {
                byte[][] arr = new byte[maxDepth][];
                arr[depth - 1] = child;
                return new Ret(99999 - maxDepth + depth, arr);
            }

            Ret r = alphaBeta(child, depth - 1, -beta, -alpha, !max, playerX);


            if(-r.value > value) {
                bestRet = r;
                bestChild = child;
                value = -r.value;
            }

            alpha = Math.max(alpha, value);
            if (alpha >= beta) break;

        }

        if(bestRet == null) {
            //no children
            byte[][] arr = new byte[maxDepth][];
            arr[depth - 1] = null;

            boolean draw = true;
            for(int i = 0; i < 9; i++) {
                if(current[((max == playerX) ? 9 : 18) + i] == 0) {
                    draw = false;
                    break;
                }
            }

            return new Ret(draw ? 0 : -99999 + maxDepth - depth, arr);
        }

        var l = bestRet.moves();
        l[depth - 1] = bestChild;
        return new Ret(value, l);

    }

    private static record Ret(int value, byte[][] moves) {}

    private int heuristic(byte[] field, boolean playerX) {
        int value = 0;

        for (int i = 0; i < 9; i++) {
            if (field[i] >= 0) {
                if (field[i] >= 16) { //o
                    if(i == 4) value += -90;
                    else if((i & 1) == 0) value += -30;
                    value += -100;//field[i];
                } else  { //x
                    if(i == 4) value += 90;
                    else if((i & 1) == 0) value += 30;
                    value += 100; //field[i] + 16;
                }
            }
        }

        return playerX ? -value : value;
    }

    public boolean terminal(byte[] current, boolean firstPlayer) {
        byte d = (byte) (firstPlayer ? 0 : 16);
        if ((current[4] & 48) == d) {
            if ((current[0] & 48) == d && (current[8] & 48) == d
                    || (current[1] & 48) == d && (current[7] & 48) == d
                    || (current[2] & 48) == d && (current[6] & 48) == d
                    || (current[3] & 48) == d && (current[5] & 48) == d) return true;

        }
        if ((current[0] & 48) == d) {
            if ((current[3] & 48) == d && (current[6] & 48) == d
                    || (current[1] & 48) == d && (current[2] & 48) == d) return true;

        }
        if ((current[8] & 48) == d) {
            if ((current[6] & 48) == d && (current[7] & 48) == d
                    || (current[2] & 48) == d && (current[5] & 48) == d) return true;
        }

        return false;
    }

    public byte[][] getChildren(byte[] current, boolean currentPlayer) {
        byte[][] children = new byte[81][];
        int ptr = 0;

        if(currentPlayer) {
            for (int i = 0; i < 9; i++) {
                if(current[i] < 0) {
                    for(byte j = 0; j < 9; j++) {
                        if(current[9 + j] != 0) continue;
                        byte[] child = copy(current);
                        child[i] = j;
                        child[j + 9] = 1;
                        children[ptr++] = child;
                    }
                } else if(current[i] >= 16) {
                    for(byte j = (byte) (current[i] + 1); j <= 24; j++) {
                        if(current[9 + j - 16] != 0) continue;
                        byte[] child = copy(current);
                        child[i] = (byte) (j - 16);
                        child[j - 16 + 9] = 1;
                        children[ptr++] = child;
                    }
                }
            }
        } else {
            for (int i = 0; i < 9; i++) {
                if(current[i] < 0) {
                    for(byte j = 16; j <= 24; j++) {
                        if(current[j + 2] != 0) continue;
                        byte[] child = copy(current);
                        child[i] = j;
                        child[j - 16 + 18] = 1;
                        children[ptr++] = child;
                    }
                } else if(current[i] < 16) {
                    for(byte j = (byte) (current[i] + 1); j <= 8; j++) {
                        if(current[j + 18] != 0) continue;
                        byte[] child = copy(current);
                        child[i] = (byte) (j + 16);
                        child[j + 18] = 1;
                        children[ptr++] = child;
                    }
                }
            }
        }

        return children;
    }

    private Move getMove(byte[] current, byte[] next) {
        if(next == null) return null;

        Move m = null;
        for(int i = 0; i < 9; i++) {
            if(current[i] != next[i]) {
               m = new Move(i % 3, i / 3, next[i] & 15);
               break;
            }
        }

        return m;
    }

    private byte[] parseByteArr(Field[][] field, boolean[] movesX, boolean[] movesO) {
        byte[] arr = new byte[27];

        for(int i = 0; i < 9; i++) {
            Field f = field[i % 3][i / 3];
            if(f == null) {
                arr[i] = -1;
            } else {
                arr[i] = (byte) (f.firstPlayer() ? 0 : 16);
                arr[i] += f.value();
            }
        }

        for(int i = 9; i < 27; i++) {
            if(i < 18) {
                arr[i] = (byte) (movesX[i - 9] ? 1 : 0);
            } else {
                arr[i] = (byte) (movesO[i - 18] ? 1 : 0);
            }
        }

        return arr;
    }

    private byte[] copy(byte[] arr) {
        byte[] c = new byte[27];
        for(int i = 0; i < 27; i++) {
            c[i] = arr[i];
        }

        return c;
    }
}
