/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package central;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 *
 * @author desharnc27
 */
public class Misc {

    public static byte[][] removeVirginLine(byte[][] potsState) {
        byte[][] res = new byte[potsState.length - 1][];
        System.arraycopy(potsState, 0, res, 0, res.length);
        return res;
    }

    public static byte[][] addVirginLine(byte[][] potsState) {
        byte[][] res = new byte[potsState.length + 1][];
        System.arraycopy(potsState, 0, res, 0, res.length - 1);
        res[potsState.length] = new byte[potsState[0].length];
        for (int i = 0; i < potsState[0].length; i++) {
            res[potsState.length][i] = -1;
        }
        return res;
    }

    public static byte[][] copyPlusVirginLine(byte[][] potsState) {

        byte[][] res = new byte[potsState.length + 1][];
        for (int i = 0; i < res.length - 1; i++) {
            res[i] = Arrays.copyOf(potsState[i], potsState[i].length);
            //System.arraycopy(potsState[i], 0, res[i], 0, potsState[i].length);
        }
        res[potsState.length] = new byte[potsState[0].length];
        for (int i = 0; i < potsState[0].length; i++) {
            res[potsState.length][i] = -1;
        }
        return res;

    }

    public static int compare(byte[][] arr0, byte[][] arr1) {
        for (int i = 0; i < arr0.length; i++) {
            for (int j = 0; j < arr0[i].length; j++) {
                int comp = arr0[i][j] - arr1[i][j];
                if (comp != 0) {
                    return comp;
                }
            }
        }
        return 0;
    }

    public static int compareRev(byte[][] arr0, byte[][] arr1) {
        for (int i = arr0.length - 1; i >= 0; i--) {
            for (int j = 0; j < arr0[i].length; j++) {
                int comp = arr0[i][j] - arr1[i][j];
                if (comp != 0) {
                    return comp;
                }
            }
        }
        return 0;
    }

    public static void println(byte[] array, boolean negativeX) {
        String[] strArr = new String[array.length];
        for (int i = 0; i < strArr.length; i++) {
            strArr[i] = negativeX && array[i] < 0 ? "-" : String.valueOf(array[i]);
        }

        System.out.println("[" + String.join(",", strArr) + "]");
    }

    public static void println(byte[] array) {
        println(array, false);
    }

    public static void print2D(byte[][] array, boolean negativeX) {
        for (byte[] array1 : array) {
            println(array1, negativeX);
        }
    }

    public static void print2D(byte[][] array) {
        print2D(array, false);
    }

    public static void println(int[] array, boolean negativeX) {
        String[] strArr = new String[array.length];
        for (int i = 0; i < strArr.length; i++) {
            strArr[i] = negativeX && array[i] < 0 ? "-" : String.valueOf(array[i]);
        }

        System.out.println("[" + String.join(",", strArr) + "]");
    }

    public static void println(int[] array) {
        println(array, false);
    }

    public static void print2D(int[][] array, boolean negativeX) {
        for (int[] array1 : array) {
            println(array1, negativeX);
        }
    }

    public static void print2D(int[][] array) {
        print2D(array, false);
    }

    public static byte[] reciprocal(byte[] tab) {
        byte[] res = new byte[tab.length];
        for (byte i = 0; i < tab.length; i++) {
            res[tab[i]] = i;
        }
        return res;
    }

    public static void write(String content, String filename) {
        PrintWriter writer;
        try {
            writer = new PrintWriter(filename);
            writer.write(content);
            writer.close();
        } catch (FileNotFoundException e) {
            System.out.println("Warning: file " + filename + " could not be written, the path may not exist.");
            return;
        }
    }

    static boolean tooMuchAfrica(byte[] b) {
        int count = 0;
        for (int i = 0; i < b.length; i++) {
            if (b[i] == 3) {
                count++;
            }
        }
        return count > 3;
    }

    static String[] mapToString(byte[] arr) {
        String[] res = new String[arr.length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = String.valueOf(arr[i]);
        }
        return res;
    }

    static String[] mapToString(int[] arr) {
        String[] res = new String[arr.length];
        for (int i = 0; i < arr.length; i++) {
            res[i] = String.valueOf(arr[i]);
        }
        return res;
    }

    /*public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip 
        return buffer.getLong();
    }*/
    public static byte[] longToBytes(long l) {
        byte[] result = new byte[Long.BYTES];
        for (int i = Long.BYTES - 1; i >= 0; i--) {
            result[i] = (byte) (l & 0xFF);
            l >>= Byte.SIZE;
        }
        return result;
    }

    public static long bytesToLong(final byte[] b) {
        long result = 0;
        for (int i = 0; i < Long.BYTES; i++) {
            result <<= Byte.SIZE;
            result |= (b[i] & 0xFF);
        }
        return result;
    }

    public static long fact(int n) {
        if (n == 0) {
            return 1;
        }
        return fact(n - 1) * n;
    }

    public static long power(long base, int exp) {
        if (exp == 0) {
            return 1;
        }
        long sqrt = power(base, exp / 2);
        if (exp % 2 == 0) {
            return sqrt * sqrt;
        }
        return base * sqrt * sqrt;
    }

    public static String decapitalize(String input) {
        char[] arr = input.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] >= 'A' && arr[i] <= 'Z') {
                arr[i] = (char) (arr[i] + 'a' - 'A');
            }
        }
        return new String(arr);
    }

    public static String capitalize(String input) {
        char[] arr = input.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] >= 'a' && arr[i] <= 'z') {
                arr[i] = (char) (arr[i] + 'A' - 'a');
            }
        }
        return new String(arr);
    }

    public static boolean sortAndAvoidReps(int[] req) {
        Arrays.sort(req);
        for (int i = 0; i < req.length - 1; i++) {
            if (req[i] == req[i + 1]) {
                return false;
            }
        }
        return true;
    }
}
