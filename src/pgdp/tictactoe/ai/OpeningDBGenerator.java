package pgdp.tictactoe.ai;

import pgdp.tictactoe.Field;
import pgdp.tictactoe.PenguAI;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class OpeningDBGenerator {

    public static final String PATH = "src/pgdp/tictactoe/opening_db.txt";

    public static void main(String[] args) throws InterruptedException {
        Map<PositionWrapper, PositionInfo> db = Collections.synchronizedMap(new HashMap<>());

        var pool = Executors.newFixedThreadPool(5);
        for(int i : new int[] {0, 1, 4}) {
            for(int j = 0; j < 9; j++) {
                final int ii = i;
                final int jj = j;

                pool.submit(() -> {

                    Field[][] f = new Field[3][3];
                    PenguAI ai = new DBInitializingAI(db);
                    f[ii / 3][ii % 3] = new Field(jj, true);

                    boolean[] firstPlayedP = new boolean[] {false, false, false, false, false, false, false, false, false};
                    firstPlayedP[jj] = true;
                    ai.makeMove(f, false,
                            firstPlayedP,
                            new boolean[] {false, false, false, false, false, false, false, false, false});


                    for(var e : db.entrySet()) {
                        byte[] p = e.getKey().position;
                        boolean[] b = new boolean[9];
                        for(int k = 0; k< 9; k++) {
                            if(p[k] != -1 && p[k] < 16) {
                                if(b[p[k]] == true) {
                                    System.out.println("duplicate!!");
                                    System.out.println(ii + " " + jj);
                                    AIHelper.print(p);
                                }
                                b[p[k]] = true;
                            }
                        }
                    }

                    System.out.println("tested move " + f[ii / 3][ii % 3] + " map size: " + db.size());

                    try {
                        System.out.println("start");
                        Thread.sleep(100);
                        System.out.println("slept");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        pool.shutdown();
        pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        System.out.println("dbsize: " + db.size());

        /*

        try {
            FileOutputStream myFileOutStream = new FileOutputStream("db1.txt");
            ObjectOutputStream myObjectOutStream = new ObjectOutputStream(myFileOutStream);
            myObjectOutStream.writeObject(db);

            myObjectOutStream.close();
            myFileOutStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }*/


        Map<PositionWrapper, PositionInfo> newTable = new HashMap<>();

        for(var e : db.entrySet()) {
            newTable.put(e.getKey(), e.getValue());

            for (int i = 1; i < 4; i++) {

                var nk = new PositionWrapper(new byte[27]);
                System.arraycopy(e.getKey().position, 0, nk.position, 0, 27);
                AIHelper.rotateBoard(nk.position, i, false);

                if(Arrays.equals(nk.position, e.getKey().position)) continue;

                var nv = new PositionInfo(new byte[27], e.getValue().evaluation());
                System.arraycopy(e.getValue().nextMove(), 0, nv.nextMove(), 0, 27);
                AIHelper.rotateBoard(nv.nextMove(), i, false);

                newTable.put(nk, nv);
            }
        }

        try {
            FileOutputStream myFileOutStream = new FileOutputStream("opening_db.txt");
            ObjectOutputStream myObjectOutStream = new ObjectOutputStream(myFileOutStream);
            myObjectOutStream.writeObject(newTable);

            myObjectOutStream.close();
            myFileOutStream.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Map<PositionWrapper, PositionInfo> readDB() {
        Map<PositionWrapper, PositionInfo> db = null;

        try {
            //URL resource = OpeningDBGenerator.class.getClassLoader().getResource("opening_db.txt");
            File f = new File("assignment/src/main/resources/opening_db.txt");
            FileInputStream fileInputStream = new FileInputStream(f);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            db = (Map<PositionWrapper, PositionInfo>) objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return db;
    }
}