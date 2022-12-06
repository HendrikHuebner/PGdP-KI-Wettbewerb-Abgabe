package pgdp.tictactoe;

public record Move(int x, int y, int value) {

}

/*
 * Same as:
 * 
 * public class Move {
 * 
 * private final int x;
 * private final int y;
 * private final int value;
 * 
 * public Move(int x, int y, int value) {
 * this.x = x;
 * this.y = y;
 * this.value = value;
 * }
 * 
 * public int x() { return x; }
 * 
 * public int y() { return y; }
 * 
 * public int value() { return value; }
 *
 * @Override
 * public boolean equals(Object o) {
 * if(o instanceof Move other) {
 * return x == other.x && y == other.y && value == other.value;
 * }
 * return false;
 * }
 *
 * @Override
 * public int hashCode() {
 * return Objects.hash(x, y, value);
 * }
 * }
 */
