package pgdp.tictactoe.ai;

import pgdp.tictactoe.Field;
import pgdp.tictactoe.Move;
import pgdp.tictactoe.PenguAI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompetitionAI extends PenguAI {

    private int movesPlayed = 0;
    private int maxDepth = 7;

    //DEBUG
    private int statesSearched = 0;

    public static void main(String[] args) {
        CompetitionAI ai = new CompetitionAI();

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
        //DEBUG
        int prevSearched = this.statesSearched;
        byte[] current = parseByteArr(board, firstPlayedPieces, secondPlayedPieces);

        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        int bestVal = Integer.MIN_VALUE;
        byte[] bestChild = null;

        for (var child : getChildren(current, firstPlayer)) {
            if (terminal(child, firstPlayer)) {
                bestChild = child;
                bestVal = Integer.MAX_VALUE;
                break;
            }

            int value = alphaBeta(child, maxDepth - 1, alpha, beta, false, firstPlayer);
            if (value > bestVal) {
                bestVal = value;
                bestChild = child;
            }

            if (bestVal >= beta) break;
            alpha = Math.max(alpha, bestVal);
        }

        this.movesPlayed++;

        //DEBUG
        if(Math.abs(bestVal) > 999999) {
            System.out.println("Evaluation: Win in " + (Integer.MAX_VALUE - bestVal + 1));
        }
        else System.out.println("Evaluation: " + bestVal);
        System.out.println("States searched: " + (statesSearched - prevSearched));
        return getMove(current, bestChild);
    }

    private int alphaBeta(byte[] current, int depth, int alpha, int beta, boolean max, boolean firstPlayer) {
        //if (!firstPlayer) System.out.println(Arrays.toString(current));

        if (depth == 0)
            return heuristic(current, max);

        if (max) {
            int value = Integer.MIN_VALUE;
            for (var child : getChildren(current, firstPlayer)) {
                if (terminal(child, firstPlayer))
                    return Integer.MAX_VALUE - maxDepth + depth;

                this.statesSearched++;
                value = Math.max(value, alphaBeta(child, depth - 1, alpha, beta, false, firstPlayer));

                if (value >= beta) break;
                alpha = Math.max(alpha, value);
            }

            return value;
        } else {
            int value = Integer.MAX_VALUE;
            for (var child : getChildren(current, !firstPlayer)) {
                if (terminal(child, !firstPlayer))
                    return Integer.MIN_VALUE + maxDepth - depth;

                this.statesSearched++;
                int tv = alphaBeta(child, depth - 1, alpha, beta, true, firstPlayer);
                if(depth == 4 && current[3] == 7 && current[6] == 23)
                    System.out.println();
                value = Math.min(value, tv);
                if (value <= alpha) break;
                beta = Math.min(beta, value);
            }

            return value;
        }
    }

    private int heuristic(byte[] field, boolean max) {
        int value = 0;

        for (int i = 0; i < 9; i++) {
            if (field[i] > 0) {
                if (field[i] >= 16) {
                    value += field[i];
                } else {
                    value -= field[i] - 10;
                }
            }
        }

        return max ? value : -value;
    }

    public boolean terminal(byte[] current, boolean firstPlayer) {
        byte d = (byte) (firstPlayer ? 0 : 16);
        if ((current[4] & 48) == d) {
            if ((current[0] & 48) == d && (current[8] & 48) == d
                    || (current[1] & 48) == d && (current[7] & 48) == d
                    || (current[2] & 48) == d && (current[6] & 48) == d
                    || (current[3] & 48) == d && (current[5] & 48) == d) return true;

        } else if ((current[0] & 48) == d) {
            if ((current[3] & 48) == d && (current[6] & 48) == d
                    || (current[1] & 48) == d && (current[2] & 48) == d) return true;

        } else if ((current[8] & 48) == d) {
            if ((current[6] & 48) == d && (current[7] & 48) == d
                    || (current[2] & 48) == d && (current[5] & 48) == d) return true;
        }

        return false;
    }

    private List<byte[]> getChildren(byte[] current, boolean currentPlayer) {
        List<byte[]> children = new ArrayList<>(20);

        if(currentPlayer) {
            for (int i = 0; i < 9; i++) {
                if(current[i] < 0) {
                    for(byte j = 0; j < 9; j++) {
                        if(current[9 + j] != 0) continue;
                        byte[] child = copy(current);
                        child[i] = j;
                        child[i + 9] = 1;
                        children.add(child);
                    }
                } else if(current[i] >= 16) {
                    for(byte j = (byte) (current[i] + 1); j <= 24; j++) {
                        if(current[9 + j - 16] != 0) continue;
                        byte[] child = copy(current);
                        child[i] = (byte) (j - 16);
                        child[i + 9] = 1;
                        children.add(child);
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
                        child[i + 16] = 1;
                        children.add(child);
                    }
                } else if(current[i] < 16) {
                    for(byte j = (byte) (current[i] + 1); j <= 8; j++) {
                        if(current[j + 18] != 0) continue;
                        byte[] child = copy(current);
                        child[i] = (byte) (j + 16);
                        child[i + 16] = 1;
                        children.add(child);
                    }
                }
            }
        }

        return children;
    }

    private Move getMove(byte[] current, byte[] next) {
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
