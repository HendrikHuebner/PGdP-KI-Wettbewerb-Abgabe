package pgdp.tictactoe.ai;

import java.io.Serializable;
import java.util.Arrays;

public class PositionWrapper implements Serializable {
    private static final long serialVersionUID = 69420L;
    public final byte[] position = new byte[27];

    public PositionWrapper(byte[] p) {
        for(int i = 0; i < 27; i++) {
            this.position[i] = p[i];
        }

    }

    @Override
    public int hashCode() {
        int result = 1;

        for(int i = 0; i < 27; i++) {
            result = 31 * result + this.position[i];
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return Arrays.equals(this.position, ((PositionWrapper) obj).position);
    }
}
