package pgdp.tictactoe.ai;

import pgdp.tictactoe.ai.PositionInfo;
import pgdp.tictactoe.ai.PositionWrapper;

import java.util.Arrays;
import java.util.HashMap;

public class OpeningDB {

    public static void main(String[] args) {
        parse();
    }
    public static HashMap<PositionWrapper, PositionInfo> parse() {
        HashMap<PositionWrapper, PositionInfo> m = new HashMap<>();

        String[] entries = s
                .replaceAll("\\[", "")
                .replaceAll("\n", "")
                .replaceAll("]", "")
                .split(";");

        for(String e : entries) {
            String[] string = e.split(":");
            String[] posStr = string[0].split(",");
            String[] nextStr = string[1].split(",");
            int value = Integer.valueOf(string[2]);

            byte[] pos = new byte[27];
            byte[] next = new byte[27];

            for (int i = 0; i < 27; i++) {
                pos[i] = Byte.valueOf(posStr[i]);
                next[i] = Byte.valueOf(nextStr[i]);
            }

            System.out.println(Arrays.toString(pos) + " :  " + Arrays.toString(next) + " " + value);
            m.put(new PositionWrapper(pos), new PositionInfo(next, value));
        }


        return m;
    }

    public static final String s = """
            0,0,0,0,0,0,0,0,7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1:0,0,0,0,6,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0:99;
            0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1:0,0,0,0,6,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0:98;
            0,0,0,0,0,0,0,0,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1:0,0,0,0,6,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0:97;
            """;
}