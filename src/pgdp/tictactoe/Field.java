package pgdp.tictactoe;

public record Field(int value, boolean firstPlayer) {

    @Override
    public String toString() {
        String player = firstPlayer ? "X" : "O";
        return (value > 9 ? "" : " ") + player + value;
    }
}

/*
 * Same as:
 * 
 * public class Field {
 * 
 * private final int value;
 * private final boolean firstPlayer;
 * 
 * public Field(int value, boolean firstPlayer) {
 * this.value = value;
 * this.firstPlayer = firstPlayer
 * }
 * 
 * public int value() { return value; }
 * 
 * public boolean firstPlayer() { return firstPlayer; }
 *
 * @Override
 * public String toString() {
 * String player = firstPlayer ? "X" : "O";
 * return (value > 9 ? "" : " ") + player + value;
 * }
 *
 * @Override
 * public boolean equals(Object o) {
 * if (o instanceof Field other) {
 * return value == other.value && firstPlayer == other.firstPlayer;
 * }
 * return false;
 * }
 *
 * @Override
 * public int hashCode() {
 * return Objects.hash(value, firstPlayer);
 * }
 * }
 */
