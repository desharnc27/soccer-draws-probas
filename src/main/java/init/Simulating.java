/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package init;

import algocore.AscendStorer;
import algocore.Node;
import central.Statix;
import tools.GeneralMeths;

/**
 *
 * @author desharnc27
 */
public class Simulating {

    public static void simulateOne(byte verbose) {
        Node node = new Node();
        int[][] drawOrder = getRandomDrawOrder();
        //byte [][] groupOrder = new byte[4][Statix.nbGROUPS()];
        int[][] finalConfig = new int[4][Statix.nbGROUPS()];
        for (int i = 0; i < 4; i++) {
            AscendStorer asSt = null;
            byte[][] asp = null;
            byte[] perm = null;
            if (i > 0) {
                asp = AscendStorer.getAscendStorerPerm(node);
                asSt = AscendStorer.getAscendStorer(asp);
                perm = asp[asp.length - 1];
            }
            for (int j = 0; j < Statix.nbGROUPS(); j++) {
                Team teamDrafted = Statix.getTeam(i, drawOrder[i][j]);
                byte nextCont = (byte) teamDrafted.cont();
                byte nextGroup = (i == 0) ? node.leftMostPlace(nextCont) : node.leftMostPlace(nextCont, asSt, perm);
                node = new Node(node, nextCont, nextGroup, 1);
                finalConfig[i][nextGroup] = drawOrder[i][j];
                //groupOrder[i][j] = nextGroup;
                if (verbose > 1) {
                    char group = (char) ('A' + nextGroup);
                    System.out.println(teamDrafted.name() + " was placed in group " + group);
                }
            }
        }
    }

    public static int[][] getRandomDrawOrder() {
        int ngr = Statix.nbGROUPS();
        int nbh = Statix.nbHOSTS();
        int[] firstPermExtra = GeneralMeths.randomPermArray(ngr - nbh);
        int[] firstPerm = new int[ngr];
        for (int i = 0; i < nbh; i++) {
            firstPerm[i] = i;
        }
        for (int i = nbh; i < ngr; i++) {
            firstPerm[i] = firstPermExtra[i - nbh] + nbh;
        }
        int[][] randomDraw = new int[][]{
            firstPerm,
            GeneralMeths.randomPermArray(ngr),
            GeneralMeths.randomPermArray(ngr),
            GeneralMeths.randomPermArray(ngr)
        };
        return randomDraw;

    }

    public static void simulate(int n) {

    }
}
