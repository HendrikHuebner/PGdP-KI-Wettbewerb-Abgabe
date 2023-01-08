package pgdp.tictactoe.ai;

import java.io.Serializable;

public record PositionInfo(byte[] nextMove, int evaluation) implements Serializable {}

