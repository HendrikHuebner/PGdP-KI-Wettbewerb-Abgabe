package pgdp;

import org.junit.jupiter.api.Test;
import pgdp.tictactoe.Field;
import pgdp.tictactoe.Game;
import pgdp.tictactoe.Move;
import pgdp.tictactoe.PenguAI;
import pgdp.tictactoe.ai.AIHelper;

import static org.junit.jupiter.api.Assertions.*;

public class CompetitionTest {

    @Test
    public void analyzePos() {
        PenguAI ai = new CompetitionAI(18);

        long t1 = System.nanoTime();

        Move m = ai.makeMove(parseStringField("""
                    -, -, -,
                    -, -, -,
                    -, -, -
                """), true,
                new boolean[] {false, false, false, false, false, false, false, false, false},
                new boolean[] {false, false, false, false, false, false, false, false, false});

        System.out.println(m);
        System.out.println("Time taken: " + (System.nanoTime() - t1) / 1000000 + "ms");

    }

    @Test
    public void analyzeFirstMoves() {
        PenguAI ai = new CompetitionAI(18);

        for(int i : new int[] {0, 1, 4}) {
            for (int j = 0; j < 9; j++) {
                Field[][] f = new Field[3][3];

                f[i / 3][i % 3] = new Field(j, true);

                boolean[] firstPlayedP = new boolean[]{false, false, false, false, false, false, false, false, false};
                firstPlayedP[j] = true;
                var m = ai.makeMove(f, false,
                        firstPlayedP,
                        new boolean[]{false, false, false, false, false, false, false, false, false});
                Game.printBoard(f);
                System.out.println("Best Move: " + m);
            }
        }
    }

    @Test
    public void playLine() {
        PenguAI x = new CompetitionAI(18);
        if ((true)) return;
        PenguAI o = new CompetitionAI(18);

        long t1 = System.nanoTime();

        Game g = new Game(x, o);
        g.setBoard(parseStringField("""
                -, -, -,
                -, -, -,
                -, -, -
                """));



        g.setFirstPlayedPieces(new boolean[] {false, false, false, false, false, false, false, false, false});
        g.setSecondPlayedPieces(new boolean[] {false, false, false, false, false, false, false, false, false});

        g.playGame(true);
        System.out.println("Time taken: " + (System.nanoTime() - t1) / 1000000 + "ms");
    }


    @Test
    public void test2() {
        var init = parseStringField("""
                    o0, o1, -,
                    -, x0, -,
                    -, -, -
                """);

        var expected = parseString("8, 2, 0");
        var testMoves = parseString("");

        testAIMoves(init, true,
                new boolean[] {false, false, false, false, false, false, false, false, false},
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

    //@Test
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
        g.setBoard(initialPosition);

        for(int i = 0; i < expected.length; i++) {
            Move actual = comp.makeMove(g.getBoard(), aiIsFirst, g.getFirstPlayedPieces(), g.getSecondPlayedPieces());
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

    @Test
    public void testChildren() {
        CompetitionAI ai = new CompetitionAI(5);
        CompetitionAI ai2 = new CompetitionAI();

        long t1 = System.nanoTime();

        byte[] b = AIHelper.parseByteArr(
                parseStringField("""
                    x1, -, o6,
                    -, x8, -,
                    o8, -, -
                """), new boolean[] {true, false, false, false, false, false, false, false, true},
                new boolean[] {false, false, false, false, false, false, true, false, true});
        //Set<Integer> children = Arrays.asList(ai.getChildren(b, true)).stream().filter(e -> e != null).map(e -> Arrays.hashCode(e)).collect(Collectors.toSet());
        //Set<Integer> children2 = Arrays.asList(ai2.getChildren(b, true)).stream().filter(e -> e != null).map(e -> Arrays.hashCode(e)).collect(Collectors.toSet());


        //children.stream().forEach(s -> System.out.println(Arrays.toString(s)));//.collect(Collectors.toList());
        //children2.stream().forEach(s -> System.out.println(Arrays.toString(s)));//.collect(Collectors.toList());

        //assertEquals(children2, children);
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

    @Test
    public void testRotate() {
        byte[] b = new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9};
        byte[] b2 = new byte[] {3, 6, 9, 2, 5, 8, 1, 4, 7};
        byte[] b3 = new byte[] {9, 8, 7, 6, 5, 4, 3, 2, 1};
        byte[] b4 = new byte[] {7, 4, 1, 8, 5, 2, 9, 6, 3};
        byte[] b5 = new byte[] {1, 2, 3, 4, 5, 6, 7, 8, 9};

        AIHelper.rotateBoard(b, 1, false);
        assertArrayEquals(b2, b);
        AIHelper.rotateBoard(b, 2, false);
        assertArrayEquals(b4, b);
        AIHelper.rotateBoard(b, 3, false);
        assertArrayEquals(b3, b);
        AIHelper.rotateBoard(b, 3, true);
        assertArrayEquals(b4, b);
        AIHelper.rotateBoard(b, 2, true);
        assertArrayEquals(b2, b);
        AIHelper.rotateBoard(b, 1, true);
        assertArrayEquals(b5, b);


    }
}
