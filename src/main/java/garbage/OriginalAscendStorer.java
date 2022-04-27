/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package garbage;

import central.ByteArray;
import central.Misc;
import central.Node;
import central.Statix;
import java.util.ArrayList;
import java.util.Collections;
import tools.CombMeths;
import tools.GeneralMeths;

/**
 *
 * @author desharnc27
 */
public class OriginalAscendStorer  {

    /*static ArrayList<OriginalAscendStorer> ascendStorerBank = new ArrayList<OriginalAscendStorer>();

    byte first3[][] = new byte[3][Statix.NB_GROUPS];
    ArrayList<ByteArray> doableSubs = new ArrayList<>();

    public OriginalAscendStorer(byte[][] potsState) {
        for (int i = 0; i < 3; i++) {
            System.arraycopy(potsState[i], 0, first3[i], 0, Statix.NB_GROUPS);
        }
    }

    @Override
    public int compareTo(OriginalAscendStorer o) {
        return Misc.compare(first3, o.first3);
    }

    void injectParts(byte[] b) {
        includePossibleLastPot(b, doableSubs);
    }

    public void print() {
        Misc.print2D(first3,true);
        for (int i = 0; i < doableSubs.size(); i++) {
            System.out.print("   ");
            Misc.println(doableSubs.get(i).arr,true);
        }
    }

    public void print3First() {
        Misc.print2D(first3,true);
        System.out.println("config subPoints: " + doableSubs.size());
    }

    public static void printSome(int jump) {
        for (int i = 0; i < ascendStorerBank.size(); i += jump) {
            ascendStorerBank.get(i).print3First();
        }
    }

    public static void printDetailedSome(int jump) {
        for (int i = 0; i < ascendStorerBank.size(); i += jump) {
            ascendStorerBank.get(i).print();
        }
    }


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


    public static byte[][] getAscendStorerOriPerm(Node node) {
        byte[][] arr = GeneralMeths.getCopy(node.potsState);
        //Assuming new line of all -1s
        if (arr.length != 4) {
            int a = 1 / 0;
        }
        for (byte i = 0; i < Statix.NB_GROUPS; i++) {
            arr[3][i] = i;

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

    static OriginalAscendStorer getAscendStorerOri(byte[][] ascendStorerPerm) {
        OriginalAscendStorer temp = new OriginalAscendStorer(ascendStorerPerm);
        int idx = Collections.binarySearch(ascendStorerBank, temp);
        if (idx < 0) {
            Misc.print2D(ascendStorerPerm,true);
            idx = -idx - 1;
            System.out.println("---------");
            if (idx != 0) {
                Misc.print2D(ascendStorerBank.get(idx - 1).first3,true);
            }
            Misc.print2D(ascendStorerBank.get(idx).first3,true);
            System.out.println("---------");
            printSome(1);
            int a = 1 / 0;
        }
        return ascendStorerBank.get(idx);
    }
*/
}
