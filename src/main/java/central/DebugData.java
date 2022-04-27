/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package central;

/**
 *
 * @author desharnc27
 */
public class DebugData {

    static Node node;

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
            {4, 0, 0, 0, 0, 0, 1, 1},
            {0, 0, 0, 1, 2, 2, 0, 0}
        };
        node = new Node();
        node.potsState = potState;
        node.level = 16;
        node.printPotsState();
        //n.trSequence = new Placement[0];
        //n.
    }
    static long debugCounter = 0;
    static long possCounter = 0;
    static int diffCounter = 0;
    static int diff3Counter = 0;
    static int tanosCounter = 0;

    public static int cmdCounter = 0;

    public static void printCounters() {
        System.out.println("debugCounter: " + debugCounter);
        System.out.println("possCounter: " + possCounter);
        System.out.println("diffCounter: " + diffCounter);
    }

    //Debug methods
    public static void nodeDebugTot(Node n) {
        if (n.level % Statix.NB_GROUPS != 0) {
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
        int[] count = new int[Statix.NB_GROUPS];
        byte[] picks = n.potsState[rd];
        for (int i = 0; i < Statix.NB_GROUPS; i++) {
            count[picks[i]]++;
        }
        int[] realCount = Statix.getCopyOfPot(rd);
        for (int i = 0; i < Statix.NB_GROUPS; i++) {
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
        for (int i = 0; i < Statix.NB_GROUPS; i++) {
            byte val = n.potsState[0][i];
            if (val > 0 && val == n.potsState[1][i]) {
                System.out.println("Error: twins on same group " + i + ": " + val + "," + n.potsState[1][i]);
            }
        }
    }

}
