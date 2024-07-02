/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algocore;

import central.ByteArray;
import central.Misc;
import central.Statix;
import java.util.ArrayList;
import java.util.Collections;
import tools.CombMeths;

/**
 *
 * @author desharnc27
 */
public class AscendStorer implements Comparable<AscendStorer> {

    private static final ArrayList<AscendStorer> ascendStorerBank = new ArrayList<>();

    private final byte firstLen;
    private final byte[][] firsts;
    AStNode outcomes = new AStNode((byte) 0);

    private AscendStorer(byte[][] arr, int arrLen) {
        firstLen = (byte) (arrLen);
        firsts = new byte[firstLen][Statix.nbGROUPS()];
        for (int i = 0; i < firstLen; i++) {
            System.arraycopy(arr[i], 0, firsts[i], 0, Statix.nbGROUPS());
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

    public static int findAndInsert(AscendStorer asSt) {
        int idx = Collections.binarySearch(AscendStorer.ascendStorerBank, asSt);
        if (idx < 0) {
            ascendStorerBank.add(-idx - 1, asSt);
        } else {
            System.out.println("Warning: findAndInsert(asSt) found already existing instance.");
        }
        return idx;
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
        includePossibleLastPot(b);
    }

    public void print() {
        Misc.print2D(firsts, true);
        /*for (int i = 0; i < doableSubs.size(); i++) {
            System.out.print("   ");
            Misc.println(doableSubs.get(i).arr, true);
        }*/
        ArrayList<ByteArray> outcomesStr = new ArrayList<>();
        byte[] actArr = new byte[Statix.nbGROUPS()];
        for (int i = 0; i < actArr.length; i++) {
            actArr[i] = (byte) -1;
        }
        outcomes.feedForPrint(outcomesStr, actArr);
        for (int i = 0; i < outcomesStr.size(); i++) {
            System.out.print("   ");
            Misc.println(outcomesStr.get(i).arr, true);
        }
    }

    public void printFirsts() {
        Misc.print2D(firsts, true);
        System.out.println("config subPoints: " + outcomes.size());
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
     * Adds all subs of sequence to outcomes (at least those which weren't in it
     * yet)
     *
     * @param sequence right-to-left possible display of the last treated pot
     *
     */
    public void includePossibleLastPot(byte[] sequence) {
        boolean[] iterArr = new boolean[Statix.nbGROUPS()];
        do {
            byte[] toInject = new byte[sequence.length];
            for (int i = 0; i < toInject.length; i++) {
                toInject[i] = iterArr[i] ? sequence[i] : -1;
            }
            outcomes.inject(toInject);
        } while (CombMeths.boolIter(iterArr));

    }

    public boolean has(byte[] toResearch) {
        return outcomes.has(toResearch);
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
        if (!node.hazardeousVirginLineCheck()) {
            arr = node.copyPSWithPlusVirginLine();
        } else {
            arr = node.copyPotsState();
        }
        for (byte i = 0; i < Statix.nbGROUPS(); i++) {
            arr[arr.length - 1][i] = i;
        }
        ascendiumSort(arr);
        return arr;
    }

    public static void ascendiumSort(byte[][] arr, int left, int right) {
        for (int i = left; i < right; i++) {
            for (int j = i + 1; j < right; j++) {
                if (columnCompare(arr, i, j) > 0) {
                    columnSwap(arr, i, j);
                }
            }
        }
    }

    public static void ascendiumSort(byte[][] arr) {
        for (int lowIdx = 0; lowIdx < Statix.nbHOSTS(); lowIdx++) {
            for (int highIdx = lowIdx + 1; highIdx < Statix.nbGROUPS(); highIdx++) {
                if (arr[0][lowIdx] != arr[0][highIdx]) {
                    continue;
                }
                if (columnCompare(arr, lowIdx, highIdx) > 0) {
                    columnSwap(arr, lowIdx, highIdx);
                }
            }
        }
        ascendiumSort(arr, Statix.nbHOSTS(), Statix.nbGROUPS());
    }

    public static void columnSwap(byte[][] arr, int c0, int c1) {
        for (byte[] arrB : arr) {
            byte temp = arrB[c0];
            arrB[c0] = arrB[c1];
            arrB[c1] = temp;
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

    public static AscendStorer getAscendStorer(byte[][] ascendStorerPerm) {
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

            //int crash = 1 / 0;
            idx = 0;//TODO remove
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
            int crash = 1 / 0;
        }
        return false;
    }

    public boolean isEmpty() {
        return outcomes.isEmpty();
    }

    public static boolean detectBuild() {
        return ascendStorerBank.size() > 0;
    }
}
