/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package central;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

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
        }
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
        if (n <= 0) {
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

    public static String guillemets(String s) {
        return "\"" + s + "\"";
    }

    public static void displayFile(String filename) {
        Path path = Paths.get(filename);
        List<String> lines;
        try {
            lines = Files.readAllLines(path);
            for (int i = 0; i < lines.size(); i++) {
                System.out.println(lines.get(i));
            }
        } catch (IOException ex) {
            System.out.println("File not found: " + filename);
        }

    }

    public static String ambiguousStr(String arg, String poss1, String poss2) {
        return (arg + " is ambiguous because it matches at least two values: " + poss1 + " and " + poss2);
    }

    public static String inputUnderstoodAs(String arg, String interp) {
        return ("Input " + arg + " understood as: " + interp);
    }

    public static String inputUnsolved(String input) {
        return ("Input " + input + " not understood.");
    }

    public static <T> T removeLast(List<T> list) {
        return list.remove(list.size() - 1);
    }

    public static <T> T getLast(List<T> list) {
        return list.get(list.size() - 1);
    }

    public static int digitVal(char digit) {
        if (digit >= '0' || digit <= '9') {
            return -1;
        }
        return digit - '0';
    }

    public static String pad(String s, int len, char defChar) {
        if (s.length() >= len) {
            return s.substring(0, len);
        }
        char[] arr = new char[len];
        for (int i = 0; i < s.length(); i++) {
            arr[i] = s.charAt(i);
        }
        for (int i = s.length(); i < len; i++) {
            arr[i] = defChar;
        }
        return new String(arr);

    }

    /**
     * Returns the value in an any-dimension array at a specific location.
     * Returns an object, which the developer will then have to cast back to the
     * appropriate class. Warning: will throw an exception if the length of
     * indexes does not match the number of dimensions of the array, or if any
     * index is out of bounds for its axis.
     *
     * @param maybeArr an any-dimension array
     * @param indexes coordinates (indexes contains one value for each axis in
     * maybeArr)
     * @return
     */
    public static Object getIntValAt(Object maybeArr, int[] indexes) {
        return getValAt(maybeArr, indexes, 0);
    }

    /**
     * Returns the value in an any-dimension array at a specific location.
     * Returns an object, which the developer will then have to cast back to the
     * appropriate class. Warning: will throw an exception if the length of
     * indexes does not match the number of dimensions of the array, or if any
     * index is out of bounds for its axis.
     *
     * @param maybeArr an any-dimension array
     * @param indexes coordinates (indexes contains one value for each axis in
     * maybeArr)
     * @return
     */
    public static Object getValAt(Object maybeArr, int[] indexes) {
        return getValAt(maybeArr, indexes, 0);
    }

    private static Object getValAt(Object maybeArr, int[] indexes, int currentIdx) {
        if (maybeArr.getClass().isArray()) {
            try {
                Object[] conv = (Object[]) maybeArr;
                return getValAt(conv[indexes[currentIdx]], indexes, currentIdx + 1);
            } catch (ClassCastException e) {
            }
            try {
                int[] conv = (int[]) maybeArr;
                return conv[indexes[currentIdx]];
            } catch (ClassCastException e) {
            }
            try {
                long[] conv = (long[]) maybeArr;
                return conv[indexes[currentIdx]];
            } catch (ClassCastException e) {
            }
        }
        return maybeArr;
    }

    public static boolean closeEnough(double v0, double v1) {
        return (Math.abs(v0 - v1) < 1.0 / (1 << 24));
    }

}
