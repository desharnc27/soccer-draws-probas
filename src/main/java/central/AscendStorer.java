/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package central;

import java.util.ArrayList;
import java.util.Collections;
import tools.CombMeths;
import tools.GeneralMeths;

/**
 *
 * @author desharnc27
 */
public class AscendStorer implements Comparable<AscendStorer> {

    static ArrayList<AscendStorer> ascendStorerBank = new ArrayList<AscendStorer>();
    byte firstLen;
    byte[][] firsts;
    ArrayList<ByteArray> doableSubs = new ArrayList<>();

    /*public AscendStorer(byte[][] potsState) {
        firstLen = (byte)(potsState.length);
        firsts = new byte[firstLen][Statix.NB_GROUPS];
        for (int i = 0; i < firstLen; i++) {
            System.arraycopy(potsState[i], 0, firsts[i], 0, Statix.NB_GROUPS);
        }
    }*/
    private AscendStorer(byte[][] arr, int arrLen) {
        firstLen = (byte) (arrLen);
        firsts = new byte[firstLen][Statix.NB_GROUPS];
        for (int i = 0; i < firstLen; i++) {
            System.arraycopy(arr[i], 0, firsts[i], 0, Statix.NB_GROUPS);
        }
    }

    public static AscendStorer buildWithAsp(byte[][] ascendStorerPerm) {
        int lenArr = ascendStorerPerm.length - 1;
        return new AscendStorer(ascendStorerPerm, lenArr);
    }

    public static AscendStorer buildWithPots(byte[][] potsState) {
        int lenArr = potsState.length;
        //detect -1 lines
        if (potsState[lenArr - 1][0] < 0) {
            lenArr--;
        }
        return new AscendStorer(potsState, lenArr);
    }

    @Override
    public int compareTo(AscendStorer o) {
        int comp = firstLen - o.firstLen;
        if (comp != 0) {
            return comp;
        }
        return Misc.compare(firsts, o.firsts);
    }

    void injectParts(byte[] b) {
        includePossibleLastPot(b, doableSubs);
    }

    public void print() {
        Misc.print2D(firsts, true);
        for (int i = 0; i < doableSubs.size(); i++) {
            System.out.print("   ");
            Misc.println(doableSubs.get(i).arr, true);
        }
    }

    public void printFirsts() {
        Misc.print2D(firsts, true);
        System.out.println("config subPoints: " + doableSubs.size());
    }

    public static void printSome(int jump) {
        for (int i = 0; i < ascendStorerBank.size(); i += jump) {
            ascendStorerBank.get(i).printFirsts();
        }
    }

    public static void printDetailedSome(int jump) {
        for (int i = 0; i < ascendStorerBank.size(); i += jump) {
            ascendStorerBank.get(i).print();
        }
    }

    /**
     * Adds all subs of sequence to subsBank (at least those which weren't in
     * subsBank yet)
     *
     * @param sequence right-to-left possible display of the last treated pot
     * @param subsBank all possibles subs found yet for one draw sequence of
     * previous pots
     *
     */
    public static void includePossibleLastPot(byte[] sequence, ArrayList<ByteArray> subsBank) {
        boolean[] iterArr = new boolean[Statix.NB_GROUPS];
        do {
            byte[] toInject = new byte[sequence.length];
            for (int i = 0; i < toInject.length; i++) {
                toInject[i] = iterArr[i] ? sequence[i] : -1;
            }
            ByteArray be = new ByteArray(toInject);
            int idx = Collections.binarySearch(subsBank, be);
            if (idx >= 0) {
                continue;
            }
            idx = -idx - 1;
            subsBank.add(idx, be);
        } while (CombMeths.boolIter(iterArr));

    }

    public boolean has(byte[] toResearch) {
        return Collections.binarySearch(doableSubs, new ByteArray(toResearch)) >= 0;
    }

    /**
     * Returns a copy of node.potsState, but the last row (which should
     * initially be only -1s) is replaced by the identity permutation, then
     * columns are sorted in ascending order. The permutation serves as
     * indicating the initial position of every column.
     *
     * @param node
     * @return
     */
    public static byte[][] getAscendStorerPerm(Node node) {
        byte[][] arr;
        if (node.potsState[node.potsState.length - 1][0] != -1) {
            arr = Misc.copyPlusVirginLine(node.potsState);
        } else {
            arr = GeneralMeths.getCopy(node.potsState);
        }
        for (byte i = 0; i < Statix.NB_GROUPS; i++) {
            arr[arr.length - 1][i] = i;
        }
        ascendiumSort(arr);
        return arr;
    }

    public static void ascendiumSort(byte[][] arr) {
        for (int i = 1; i < Statix.NB_GROUPS; i++) {
            for (int j = i; j < Statix.NB_GROUPS; j++) {
                if (columnCompare(arr, i, j) > 0) {
                    columnSwap(arr, i, j);
                }
            }
        }
    }

    public static void columnSwap(byte[][] arr, int c0, int c1) {
        for (int i = 0; i < arr.length; i++) {
            byte temp = arr[i][c0];
            arr[i][c0] = arr[i][c1];
            arr[i][c1] = temp;
        }
    }

    public static int columnCompare(byte[][] arr, int c0, int c1) {
        int nbRows = arr.length;
        int comp;
        for (int i = 0; i < nbRows; i++) {
            comp = arr[i][c0] - arr[i][c1];
            if (comp != 0) {
                return comp;
            }
        }
        return 0;
    }

    static AscendStorer getAscendStorer(byte[][] ascendStorerPerm) {
        AscendStorer temp = AscendStorer.buildWithAsp(ascendStorerPerm);
        int idx = Collections.binarySearch(ascendStorerBank, temp);
        if (idx < 0) {
            System.out.println("---------");
            Misc.print2D(ascendStorerPerm, true);
            idx = -idx - 1;
            System.out.println("---------");
            if (idx != 0) {
                Misc.print2D(ascendStorerBank.get(idx - 1).firsts, true);
            }
            Misc.print2D(ascendStorerBank.get(idx).firsts, true);
            System.out.println("---------");
            printSome(1);
            System.out.println("---------");

            int debug = 1 / 0;
        }
        return ascendStorerBank.get(idx);
    }

    public static boolean deadEndOnNextLevel(Node node) {
        try {
            byte[][] asp = AscendStorer.getAscendStorerPerm(node);
            AscendStorer as = AscendStorer.getAscendStorer(asp);

            return as.isEmpty();
        } catch (Exception e) {
            node.printPotsState();
            e.printStackTrace();
            int a = 1 / 0;
        }
        return false;
    }

    public boolean isEmpty() {
        return doableSubs.size() == 0;
    }
}
