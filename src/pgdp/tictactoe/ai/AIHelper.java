package pgdp.tictactoe.ai;

import pgdp.tictactoe.Field;
import pgdp.tictactoe.Move;

public class AIHelper {

    public static Move getMove(byte[] current, byte[] next) {
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

    public static byte[] parseByteArr(Field[][] field, boolean[] movesX, boolean[] movesO) {
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

    public static byte[] copy(byte[] arr) {
        byte[] c = new byte[27];
        for(int i = 0; i < 27; i++) {
            c[i] = arr[i];
        }

        return c;
    }

    public static int getNumberOfPieces(byte[] f) {
        int n = 0;
        for(int i = 0; i < 9; i++){
            if(f[i] != -1) n++;
        }

        return n;
    }

    public static int getHalfMove(byte[] f) {
        int n = 0;
        for(int i = 9; i < 27; i++){
            if(f[i] != 0) n++;
        }

        return n;
    }

    public static void rotateBoard(byte[] f, int amount, boolean clockwise) {
        if(clockwise) amount = 4 - amount;
        amount %= 4;

        if(amount == 0) return;
        var corners = new byte[]{f[0], f[2], f[8],  f[6]};
        var edges = new byte[]{f[1], f[5], f[7], f[3]};


        f[0] = corners[amount];
        f[2] = corners[(amount + 1) % 4];
        f[8] = corners[(amount + 2) % 4];
        f[6] = corners[(amount + 3) % 4];

        f[1] = edges[amount];
        f[5] = edges[(amount + 1) % 4];
        f[7] = edges[(amount + 2) % 4];
        f[3] = edges[(amount + 3) % 4];

    }

    public static void print(byte[] current) {
        String s = "[";

        for(int i = 0; i < 9; i++) {
            if(current[i] == -1) {
                s += "  ";
            } else if (current[i] < 16) {
                s += "x" + current[i];
            } else  {
                s += "o" + (current[i] - 16);
            }

            if(i == 2 || i == 5) s += " | ";
            else if(i != 8) s += ", ";
        }

        s += "]";
        System.out.println(s);
    }
}
