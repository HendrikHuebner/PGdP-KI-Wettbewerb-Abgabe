package pgdp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.InputMismatchException;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import pgdp.tictactoe.Field;
import pgdp.tictactoe.Game;
import pgdp.tictactoe.Move;
import pgdp.tictactoe.ai.SimpleAI;

public class UnitTests {

    @Test
    @DisplayName("Should play Artemis example")
    public void artemisExample() {
        Move[] movesX = parseString("""
            7, 0, 1,
            6, 0, 2,
            8, 1, 2,
            1, 2, 1,
            5, 2, 0,
            4, 0, 0,
            3, 1, 1""");

        Move[] movesO = parseString("""
            8, 2, 2,
            7, 0, 2,
            0, 2, 1,
            6, 2, 1,
            3, 0, 0,
            5, 0, 0,
            4, 1, 1""");


        runTest(movesX, movesO, false);
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("Should be able to handle out of bounds moves")
    public void outOfBoundsTest(Move[] movesX, Move[] movesO, boolean firstWon) {
        runTest(movesX, movesO, firstWon);
    }

    public static Stream<Arguments> outOfBoundsTest() {
        Move[] emptyMove = parseString("");
        Move[] move = parseString("0, 0, 0");

        Move[] outOfBounds1 = parseString("0, 0, -1");
        Move[] outOfBounds2 = parseString("0, 0, 9");
        Move[] outOfBounds3 = parseString("0, -1, 0");
        Move[] outOfBounds4 = parseString("0, 9, 0");
        Move[] outOfBounds5 = parseString("-1, 0, 0");
        Move[] outOfBounds6 = parseString("9, 0, 0");

        return Stream.of(
            arguments(outOfBounds1, emptyMove, false),
            arguments(outOfBounds2, emptyMove, false),
            arguments(outOfBounds3, emptyMove, false),
            arguments(outOfBounds4, emptyMove, false),
            arguments(outOfBounds5, emptyMove, false),
            arguments(outOfBounds6, emptyMove, false),
            arguments(move, outOfBounds1, true),
            arguments(move, outOfBounds2, true),
            arguments(move, outOfBounds3, true),
            arguments(move, outOfBounds4, true),
            arguments(move, outOfBounds5, true),
            arguments(move, outOfBounds6, true)
        );
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("Should be able to handle invalid moves")
    public void invalidMoveTest(Move[] movesX, Move[] movesO, boolean firstWon) {
        runTest(movesX, movesO, firstWon);
    }

    public static Stream<Arguments> invalidMoveTest() {
        Move[] move1 = parseString("5, 0, 0");
        Move[] invalid1 = parseString("0, 0, 0");
        Move[] invalid2 = parseString("5, 0, 0");
        Move[] invalid3 = parseString("0, 1, 0, 0, 1, 0");
        Move[] invalid4 = parseString("0, 1, 0, 8, 1, 0");

        return Stream.of(
                arguments(move1, invalid1, true),
                arguments(move1, invalid2, true),
                arguments(invalid3, move1, false),
                arguments(invalid4, move1, false)
        );
    }


    /**
     * If !expectedWinner there has to be at least one O on the board
     * @param init
     * @param winningMove
     * @param expectedWinner
     */
    @ParameterizedTest
    @MethodSource
    @DisplayName("Should detect winning patterns")
    public void winTest(Field[][] init, Move[] winningMove, boolean expectedWinner) {
        if(expectedWinner) {
            runTest(init, winningMove, null, true);
        } else {
            //calculate a move for x
            Move moveO = null;
            for(int i = 0; i < 9; i++) {
                Field f = init[i / 3][i % 3];
                if(f != null && f.firstPlayer()) {
                    moveO = new Move(i / 3, i % 3, f.value());
                    f = null;
                    break;
                }
            }

            runTest(init, new Move[]{moveO}, winningMove, false);
        }
    }

    public static Stream<Arguments> winTest() {
        return Stream.of(
                arguments(parseStringField("-, x2, x3, -, -, -, -, -, -"), parseString("1, 0, 0"), true),
        arguments(parseStringField("-, o2, o3, -, x8, -, -, -, -"), parseString("1, 0, 0"), false),
        arguments(parseStringField("-, -, -, -, x2, x3, -, -, -"), parseString("1, 0, 1"), true),
        arguments(parseStringField("-, x8, -, -, o2, o3, -, -, -"), parseString("1, 0, 1"), false),
        arguments(parseStringField("-, -, -, -, -, -, -, x2, x3"), parseString("1, 0, 2"), true),
        arguments(parseStringField("-, x8, -, -, -, -, -, o2, o3"), parseString("1, 0, 2"), false),
        arguments(parseStringField("-, -, -, -, -, x2, -, -, x3"), parseString("1, 2, 0"), true),
        arguments(parseStringField("-, -, -, x8, -, o2, -, -, o3"), parseString("1, 2, 0"), false),
        arguments(parseStringField("-, -, -, -, x2, -, -, x3, -"), parseString("1, 1, 0"), true),
        arguments(parseStringField("-, -, -, x8, o2, -, -, o3, -"), parseString("1, 1, 0"), false),
        arguments(parseStringField("-, -, -, x2, -, -, x3, -, -"), parseString("1, 0, 0"), true),
        arguments(parseStringField("-, -, -, o2, -, x8, o3, -, -"), parseString("1, 0, 0"), false),
        arguments(parseStringField("-, -, -, -, x2, -, x3, -, -"), parseString("1, 2, 0"), true),
        arguments(parseStringField("-, -, -, -, o2, -, o3, x8, -"), parseString("1, 2, 0"), false),
        arguments(parseStringField("-, -, -, -, x2, -, -, -, x3"), parseString("1, 0, 0"), true),
        arguments(parseStringField("-, -, -, -, o2, -, x8, -, o3"), parseString("1, 0, 0"), false));
    }

    private void runTest(Move[] x, Move[] o, boolean b) {
        runTest(null, x, o, b);
    }

    public void runTest(Field[][] init, Move[] movesX, Move[] movesO, boolean firstWon) {
        TestAI X = new TestAI(movesX);
        TestAI O = new TestAI(movesO);
        Game g = new Game(X, O);
        if(init != null)g.setField(init);
        g.playGame();

        assertEquals(firstWon ? X : O, g.getWinner());
        assertEquals(movesX.length, X.getIndex());
        if(movesO != null) assertEquals(movesO.length, O.getIndex());
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
            moves[i / 3] = new Move(Integer.valueOf(split[i + 1]), Integer.valueOf(split[i + 2]), Integer.valueOf(split[i]));
        }

        return moves;
    }

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