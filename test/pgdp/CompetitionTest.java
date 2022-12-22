package pgdp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pgdp.tictactoe.Field;
import pgdp.tictactoe.Game;
import pgdp.tictactoe.Move;
import pgdp.tictactoe.ai.CompetitionAI;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class CompetitionTest {

    @Test
    public void test1() {
        var init = parseStringField("""
                    x0, x1, -,
                    -, o8, -,
                    -, -, -
                """);

        var expected = parseString("-, 2, 0");
        var testMoves = parseString("");

        testAIMoves(init, true,
                new boolean[] {true, true, false, false, false, false, false, false, false},
                new boolean[] {false, false, false, false, false, false, false, false, true},
                expected, testMoves);
    }

    @Test
    public void test2() {
        var init = parseStringField("""
                    o0, o1, -,
                    -, x8, -,
                    -, -, -
                """);

        var expected = parseString("-, 2, 0");
        var testMoves = parseString("0, 2, 1");

        testAIMoves(init, false,
                new boolean[] {false, false, false, false, false, false, false, false, true},
                new boolean[] {true, true, false, false, false, false, false, false, false},
                expected, testMoves);
    }

    @Test
    public void test3() {
        var init = parseStringField("""
                    x8, -, -,
                    -, o8, -,
                    x7, -, -
                """);

        var expected = parseString("6, 0, 1");

        testAIMoves(init, false,
                new boolean[] {false, false, false, false, false, false, false, true, true},
                new boolean[] {false, false, false, false, false, false, false, false, true},
                expected, new Move[]{});
    }

    @Test
    public void winIn3() {
        var init = parseStringField("""
                    o8, -, x8,
                    -, -, -,
                    -, -, -
                """);

        var expected = parseString("6, 0, 1");

        testAIMoves(init, false,
                new boolean[] {false, false, false, false, false, false, false, true, true},
                new boolean[] {false, false, false, false, false, false, false, false, true},
                expected, new Move[]{});
    }


    public void testAIMoves(Field[][] initialPosition, boolean aiIsFirst, boolean[] firstPieces, boolean[] secondPieces, Move[] expected, Move[] testAIMoves) {
        CompetitionAI comp = new CompetitionAI();
        TestAI test = new TestAI(testAIMoves);
        Game g = aiIsFirst ? new Game(comp, test) : new Game(test, comp);
        g.setFirstPlayedPieces(firstPieces);
        g.setSecondPlayedPieces(secondPieces);
        g.setField(initialPosition);

        for(int i = 0; i < expected.length; i++) {
            Move actual = comp.makeMove(g.getField(), aiIsFirst, g.getFirstPlayedPieces(), g.getSecondPlayedPieces());
            if(expected[i].value() < 0) {
                assertEquals(expected[i].x(), actual.x());
                assertEquals(expected[i].y(), actual.y());
            } else {
                assertEquals(expected[i], actual);
            }
            g.step();
        }
    }

    @Test
    public void testTerminal() {
        CompetitionAI ai = new CompetitionAI();
        assertTrue(ai.terminal(byteField("x1, x2, x3, -, -, -, -, -, -"), true));
        assertFalse(ai.terminal(byteField("x1, x2, x3, -, -, -, -, -, -"), false));
        assertFalse(ai.terminal(byteField("o1, o2, o3, -, -, -, -, -, -"), true));
        assertTrue(ai.terminal(byteField("o1, o2, o3, -, -, -, -, -, -"), false));
        assertTrue(ai.terminal(byteField("-, -, -, x1, x2, x3, -, -, -"), true));
        assertFalse(ai.terminal(byteField("-, -, -, x1, x2, x3, -, -, -"), false));
        assertFalse(ai.terminal(byteField("-, -, -, o1, o2, o3, -, -, -"), true));
        assertTrue(ai.terminal(byteField("-, -, -, o1, o2, o3, -, -, -"), false));
        assertTrue(ai.terminal(byteField("-, -, -, -, -, -, x1, x2, x3"), true));
        assertFalse(ai.terminal(byteField("-, -, -, -, -, -, x1, x2, x3"), false));
        assertFalse(ai.terminal(byteField("-, -, -, -, -, -, o1, o2, o3"), true));
        assertTrue(ai.terminal(byteField("-, -, -, -, -, -, o1, o2, o3"), false));
        assertTrue(ai.terminal(byteField("-, -, x1, -, -, x2, -, -, x3"), true));
        assertFalse(ai.terminal(byteField("-, -, x1, -, -, x2, -, -, x3"), false));
        assertFalse(ai.terminal(byteField("-, -, o1, -, -, o2, -, -, o3"), true));
        assertTrue(ai.terminal(byteField("-, -, o1, -, -, o2, -, -, o3"), false));
        assertTrue(ai.terminal(byteField("-, x1, -, -, x2, -, -, x3, -"), true));
        assertFalse(ai.terminal(byteField("-, x1, -, -, x2, -, -, x3, -"), false));
        assertFalse(ai.terminal(byteField("-, o1, -, -, o2, -, -, o3, -"), true));
        assertTrue(ai.terminal(byteField("-, o1, -, -, o2, -, -, o3, -"), false));
        assertTrue(ai.terminal(byteField("x1, -, -, x2, -, -, x3, -, -"), true));
        assertFalse(ai.terminal(byteField("x1, -, -, x2, -, -, x3, -, -"), false));
        assertFalse(ai.terminal(byteField("o1, -, -, o2, -, -, o3, -, -"), true));
        assertTrue(ai.terminal(byteField("o1, -, -, o2, -, -, o3, -, -"), false));
        assertTrue(ai.terminal(byteField("-, -, x1, -, x2, -, x3, -, -"), true));
        assertFalse(ai.terminal(byteField("-, -, x1, -, x2, -, x3, -, -"), false));
        assertFalse(ai.terminal(byteField("-, -, o1, -, o2, -, o3, -, -"), true));
        assertTrue(ai.terminal(byteField("-, -, o1, -, o2, -, o3, -, -"), false));
        assertTrue(ai.terminal(byteField("x1, -, -, -, x2, -, -, -, x3"), true));
        assertFalse(ai.terminal(byteField("x1, -, -, -, x2, -, -, -, x3"), false));
        assertFalse(ai.terminal(byteField("o1, -, -, -, o2, -, -, -, o3"), true));
        assertTrue(ai.terminal(byteField("o1, -, -, -, o2, -, -, -, o3"), false));
    }

    /**
     * Creates a Move[] array from string. A move consists of three integers separates by commas.
     * The first is the value, the second is the x coordinate, the third is the y coordinate.
     * @param s
     * @return
     */
    public static Move[] parseString(String s) {
        if(s.isEmpty()) return  new Move[] {};
        s = s.replaceAll("\\s+","");
        String[] split = s.split(",");

        Move[] moves = new Move[split.length / 3];

        for(int i = 0; i < split.length; i += 3) {
            int val = split[i].equals("-") ? -1 : Integer.valueOf(split[i]);
            moves[i / 3] = new Move(Integer.valueOf(split[i + 1]), Integer.valueOf(split[i + 2]), val);
        }

        return moves;
    }

    public static byte[] byteField(String s) {
        Field[][] m = parseStringField(s);
        byte[] bytes = new byte[27];
        for(int i = 0; i < 9; i ++) {
            Field f =  m[i / 3][i % 3];
            if(f != null)
                bytes[i] = (byte) ((f.firstPlayer() ? 0 : 16) + f.value());
            else bytes[i] = -1;
        }

        return bytes;
    }

    /**
     * Creates a Move[] array from string. A move consists of three integers separates by commas.
     * The first is the value, the second is the x coordinate, the third is the y coordinate.
     * @param s
     * @return
     */
    public static Field[][] parseStringField(String s) {
        s = s.replaceAll("\\s+","");
        String[] split = s.split(",");

        Field[][] field = new Field[3][3];

        for(int i = 0; i < 9; i++) {
            if(split[i].equals("-")) continue;
            boolean first = split[i].toUpperCase().charAt(0) == 'X';
            int value = Integer.valueOf(split[i].charAt(1)) - 48;

            field[i % 3][i / 3] = new Field(value, first);
        }

        return field;
    }
}
