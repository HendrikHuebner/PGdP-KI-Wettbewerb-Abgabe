package pgdp.tictactoe;

import pgdp.tictactoe.Field;
import pgdp.tictactoe.Move;
import pgdp.tictactoe.PenguAI;
import pgdp.tictactoe.ai.OpeningDBGenerator;
import pgdp.tictactoe.ai.PositionInfo;
import pgdp.tictactoe.ai.PositionWrapper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;

import static pgdp.tictactoe.ai.AIHelper.*;

public class CompetitionAI extends PenguAI {

    private final int maxDepth;
    private long statesSearched = 0;
    private boolean checkSymmetric = true;
    private int boardRotation = 0;
    private Map<PositionWrapper, PositionInfo> openingTable;

    public CompetitionAI(int maxDepth) {
        this.maxDepth = maxDepth;

        this.openingTable = OpeningDBGenerator.readDB();
    }

    public CompetitionAI() {
        this(18);
    }

    @Override
    public Move makeMove(Field[][] board, boolean firstPlayer, boolean[] firstPlayedPieces, boolean[] secondPlayedPieces) {
        long prevSearched = this.statesSearched;
        byte[] current = parseByteArr(board, firstPlayedPieces, secondPlayedPieces);

        PositionInfo positionInfo = alphaBeta(current, maxDepth, -99999, 99999, true, firstPlayer);

        //LOG
        if(Math.abs(positionInfo.evaluation()) > 9999) {
            if(positionInfo.evaluation() > 9999){
                System.out.println("Evaluation: Win in " + (99999 - positionInfo.evaluation() + 1));
            } else {
                System.out.println("Evaluation: Loss in " + (99999 + positionInfo.evaluation() + 1));
            }
        } else System.out.println("Evaluation: " + positionInfo.evaluation());
        System.out.println("States searched: " + (statesSearched - prevSearched));

        return getMove(current, positionInfo.nextMove());
    }

    private PositionInfo alphaBeta(byte[] current, int depth, int alpha, int beta, boolean max, boolean playerX) {
        this.statesSearched++;

        if(depth >= 12) {
            PositionInfo info = this.openingTable.get(new PositionWrapper(current));
            if(info != null) return info;
        }

        if (depth == 0) {
            return new PositionInfo(null, heuristic(current, playerX));
        }

        int value = -99999;
        byte[] bestChild = null;
        PositionInfo bestPositionInfo = null;

        var children = getChildren(current, max == playerX, depth);
        for (var child : children) {
            if(child == null) continue;

            if(terminal(child, max == playerX)) {
                return new PositionInfo(child,99999 - maxDepth + depth);
            }

            PositionInfo r = alphaBeta(child, depth - 1, -beta, -alpha, !max, playerX);

            if(-r.evaluation() > value) {
                bestPositionInfo = r;
                bestChild = child;
                value = -r.evaluation();
            }

            alpha = Math.max(alpha, value);
            if (alpha >= beta) {
                break;
            }
        }

        if(bestPositionInfo == null) {
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

            return new PositionInfo(null, draw ? 0 : -99999 + maxDepth - depth);
        }

        return new PositionInfo(bestChild, value);
    }


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

    public byte[][] getChildren(byte[] current, boolean currentPlayer, int depth) {
        byte[][] children = new byte[81][];

        if(this.checkSymmetric) {
            int numOfPieces = getNumberOfPieces(current);
            if (numOfPieces > 1) {
                this.checkSymmetric = false;
            } else {

                int[] toBeSearched;

                if (numOfPieces == 0) {
                    //only consider positions 0, 3, 4
                    toBeSearched = new int[]{0, 3, 4};

                } else {
                    if (current[4] != -1) {
                        //only consider 0, 3, 4
                        toBeSearched = new int[]{0, 3, 4};
                    } else if (current[0] != -1 || current[8] != -1) {
                        //only consider 0, 1, 2, 4, 5, 8
                        toBeSearched = new int[]{0, 1, 2, 4, 5, 8};
                    } else if (current[2] != -1 || current[6] != -1) {
                        //only consider 0, 1, 2, 3, 4, 6
                        toBeSearched = new int[]{0, 1, 2, 3, 4, 6};
                    } else if (current[1] != -1 || current[7] != -1) {
                        //only consider 0, 1, 3, 4, 6, 7
                        toBeSearched = new int[]{0, 1, 3, 4, 6, 7};
                    } else /*if (current[3] != -1 || current[5] != -1)*/ {
                        //only consider 0, 1, 2, 3, 4, 5
                        toBeSearched = new int[]{0, 1, 2, 3, 4, 5};
                    }
                }

                int ptr = 0;
                for (int i : toBeSearched) {
                    for (byte j = 0; j < 9; j++) {
                        if (currentPlayer && current[j + 9] != 0 || !currentPlayer && current[j + 18] != 0) continue;
                        byte[] child = copy(current);
                        if (currentPlayer) {
                            if (current[i] != -1) {
                                if (j + 16 <= current[i]) continue;
                            }
                            child[i] = j;
                            child[j + 9] = 1;
                        } else {
                            if (current[i] != -1) {
                                if (j <= current[i]) continue;
                            }
                            child[i] = (byte) (j + 16);
                            child[j + 18] = 1;
                        }
                        children[ptr++] = child;
                    }
                }

                return children;
            }
        }

        if(currentPlayer) {
            for (int i = 0; i < 9; i++) {
                if(current[i] < 0) {
                    for(byte j = 0; j < 9; j++) {
                        if(current[9 + j] != 0) continue;
                        byte[] child = copy(current);
                        child[i] = j;
                        child[j + 9] = 1;
                        children[(8 - j) * 9 + i] = child;
                    }
                } else if(current[i] >= 16) {
                    for(byte j = (byte) (current[i] + 1); j <= 24; j++) {
                        if(current[9 + j - 16] != 0) continue;
                        byte[] child = copy(current);
                        child[i] = (byte) (j - 16);
                        child[j - 16 + 9] = 1;
                        children[(8 - j + 16) * 9 + i] = child;
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
                        children[(8 - j + 16) * 9 + i] = child;
                    }
                } else if(current[i] < 16) {
                    for(byte j = (byte) (current[i] + 1); j <= 8; j++) {
                        if(current[j + 18] != 0) continue;
                        byte[] child = copy(current);
                        child[i] = (byte) (j + 16);
                        child[j + 18] = 1;
                        children[(8 - j) * 9 + i] = child;
                    }
                }
            }
        }

        return children;
    }
}
