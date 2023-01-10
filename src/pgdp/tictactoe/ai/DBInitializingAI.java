package pgdp.tictactoe.ai;

import pgdp.tictactoe.Field;
import pgdp.tictactoe.Move;
import pgdp.tictactoe.PenguAI;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static pgdp.tictactoe.ai.AIHelper.*;


public class DBInitializingAI extends PenguAI {

    private final int maxDepth;
    private long statesSearched = 0;
    private boolean checkSymmetric = true;

    private Map<PositionWrapper, PositionInfo> db;

    public DBInitializingAI(Map<PositionWrapper, PositionInfo> db) {
        this.maxDepth = 18;
        this.db = db;
    }


    @Override
    public Move makeMove(Field[][] board, boolean firstPlayer, boolean[] firstPlayedPieces, boolean[] secondPlayedPieces) {
        long prevSearched = this.statesSearched;
        byte[] current = parseByteArr(board, firstPlayedPieces, secondPlayedPieces);

        long t1 = System.currentTimeMillis();
        PositionInfo positionInfo = alphaBeta(current, maxDepth, -99999, 99999, true, firstPlayer);
        long diff = System.currentTimeMillis() - t1;

        if(diff > 300) {
            db.put(new PositionWrapper(current), positionInfo);
        }

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

        if(depth >= 17) System.out.println(depth);


        if (depth == 0) {
            byte[][] arr = new byte[maxDepth][];
            arr[0] = current;
            return new PositionInfo(null, heuristic(current, playerX));
        }

        int value = -99999;
        byte[] bestChild = null;
        PositionInfo bestRet = null;

        var children = getChildren(current, max == playerX, depth);
        for (var child : children) {
            if(child == null) continue;

            if(terminal(child, max == playerX)) {
                byte[][] arr = new byte[maxDepth][];
                arr[depth - 1] = child;
                return new PositionInfo(child, 99999 - maxDepth + depth);
            }

            PositionInfo r;
            if(depth < 12) {
                r = alphaBeta(child, depth - 1, -beta, -alpha, !max, playerX);
            } else {
                long t1 = System.currentTimeMillis();
                r = alphaBeta(child, depth - 1, -beta, -alpha, !max, playerX);
                long diff = System.currentTimeMillis() - t1;

                if(diff > 300) {
                    db.put(new PositionWrapper(child), r);
                }
            }

            if(-r.evaluation() > value) {
                bestRet = r;
                bestChild = child;
                value = -r.evaluation();
            }

            alpha = Math.max(alpha, value);
            if (alpha >= beta) {
                break;
            }
        }

        if(bestRet == null) {
            //no children
            byte[][] arr = new byte[maxDepth][];
            arr[depth - 1] = null;

            boolean draw = true;
            for (int i = 0; i < 9; i++) {
                if (current[((max == playerX) ? 9 : 18) + i] == 0) {
                    draw = false;
                    break;
                }
            }

            int resVal = -99999 + maxDepth - depth;
            if (draw) {
                int first = 0;
                int second = 0;
                for (int i = 0; i < 9; i++) {
                    if (current[i] == -1) continue;
                    if (current[i] >= 16) {
                        second += current[i] - 16;
                    } else {
                        first += current[i];
                    }
                }

                if (first == second) {
                    resVal = 0;
                } else if (first < second && max == playerX) {
                    resVal = 99999 - maxDepth + depth;
                }

            }

            return new PositionInfo(null, resVal);
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
