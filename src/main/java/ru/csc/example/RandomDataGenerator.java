package ru.csc.example;

import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * @author Andrey Kokorev
 *         Created on 28.09.2014.
 */
public class RandomDataGenerator {
    public final char[] alphabet = "1234567890-+.qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM".toCharArray();

    private PrintWriter pw;
    public RandomDataGenerator(OutputStream outputStream) {
        pw = new PrintWriter(outputStream);
    }

    public void generate(String[] aspectName, int maxAspectSize, int count) {
        pw.printf("%d %d \n", aspectName.length, count);

        for(int i = 0; i < aspectName.length; i++)
            pw.print(aspectName[i] + " ");
        pw.println();

        int aLength = alphabet.length;
        while(count-- > 0) {
            for(int i = 0; i < aspectName.length; i++) {
                int l = (int) (Math.random() * maxAspectSize) + 1;
                while(l-- > 0) pw.print(alphabet[(int) (Math.random() * aLength)]);
                pw.print(' ');
            }
            pw.println();
        }
        pw.flush();
    }

    public void generateEnum(String[] aspectName, int maxAspectSize, int count, int enumSize) {
        pw.printf("%d %d \n", aspectName.length, count);

        int aLength = alphabet.length;
        String[][] values = new String[aspectName.length][enumSize];

        for (int i = 0; i < aspectName.length; i++) {
            for(int j = 0; j < enumSize; j++) {
                StringBuilder sb = new StringBuilder();
                int l = (int) (Math.random() * maxAspectSize) + 1;
                while(l-- > 0) sb.append(alphabet[(int) (Math.random() * aLength)]);
                values[i][j] = sb.toString();
            }
        }

        for(int i = 0; i < aspectName.length; i++)
            pw.print(aspectName[i] + " ");
        pw.println();

        while(count-- > 0) {
            for(int i = 0; i < aspectName.length; i++) {
                pw.print(values[i][(int) (Math.random() * enumSize)] + " ");
            }
            pw.println();
        }
        pw.flush();
    }

    public void close() {
        pw.close();
    }
}
