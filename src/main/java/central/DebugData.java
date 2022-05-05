/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package central;

import algocore.Node;

/**
 *
 * @author desharnc27
 */
public class DebugData {

    public static final boolean debging = false;
    public static final boolean printDebugSums = true;

    public static Node node;

    public static void initialize() {
        /*byte[][] potState = new byte[][]{
            {4, 0, 0, 0, 0, 0, 1, 1},
            {0, 0, 0, 0, 0, 1, 2, 2},
            {0, 3, 4, 4, 4, 0, 3, 3},
            {-1, -1, -1, -1, -1, -1, -1, -1}
        };*/
 /*byte[][] potState = new byte[][]{
            {4, 0, 0, 0, 0, 0, 1, 1},
            {0, 0, 0, 0, 0, 1, 2, 2},
            {0, 3, 4, 3, 3, 0, 4, 4},
            {-1, -1, -1, -1, -1, -1, -1, -1}
        };*/
        byte[][] potState = new byte[][]{
            {4, 0, 1, 0, 1, 5},
            {0, 2, 2, 0, 5, 3}, // {0,3,5,5,0,4}
        };
        potState = Misc.addVirginLine(potState);
        node = new Node();
        node.setPotState(potState);
        node.resyncLevel();
        //node.printPotsState();
        //n.trSequence = new Placement[0];
        //n.
    }
    public static long debugCounter = 0;
    public static long possCounter = 0;
    public static int diffCounter = 0;
    public static int diff3Counter = 0;
    public static int tanosCounter = 0;

    public static int cmdCounter = 0;

    public static double probCounter = 0;

    public static void printCounters() {
        System.out.println("debugCounter: " + debugCounter);
        System.out.println("possCounter: " + possCounter);
        System.out.println("diffCounter: " + diffCounter);
        System.out.println("probCounter: " + probCounter);
    }

    //Debug methods
    /*public static void nodeDebugTot(Node n) {
        if (n.getLevel() % Statix.nbGROUPS() != 0) {
            return;
        }
        int rd = n.potsState.length - 1;
        if (rd < 0) {
            return;
        }
        if (n.potsState[rd][0] == -1) {
            rd--;
        }
        if (rd < 0) {
            return;
        }
        int[] count = new int[Statix.nbGROUPS()];
        byte[] picks = n.potsState[rd];
        for (int i = 0; i < Statix.nbGROUPS(); i++) {
            count[picks[i]]++;
        }
        int[] realCount = Statix.getCopyOfPot(rd);
        for (int i = 0; i < Statix.nbGROUPS(); i++) {
            if (count[i] != realCount[i]) {
                System.out.println("Bad count:");
                n.printPotsState();
                return;
            }

        }

    }

    public static void nodeDebug(Node n) {
        if (n.potsState.length < 2) {
            return;
        }
        for (int i = 0; i < Statix.nbGROUPS(); i++) {
            byte val = n.potsState[0][i];
            if (val > 0 && val == n.potsState[1][i]) {
                System.out.println("Error: twins on same group " + i + ": " + val + "," + n.potsState[1][i]);
            }
        }
    }*/
}
