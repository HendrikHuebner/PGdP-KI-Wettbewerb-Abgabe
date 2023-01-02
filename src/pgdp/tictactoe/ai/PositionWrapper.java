package pgdp.tictactoe.ai;

import java.util.Arrays;

public class PositionWrapper {

    private final byte[] position = new byte[9];
    private final int eval;

    public PositionWrapper(byte[] p, int eval) {
        for(int i = 0; i < 9; i++) {
            this.position[i] = p[i];
        }

        this.eval = eval;
    }

    @Override
    public int hashCode() {
        int result = 1;

        for(int i = 0; i < 9; i++) {
            result = 31 * result + this.position[i];
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return Arrays.equals(this.position, ((PositionWrapper) obj).position);
    }
}
