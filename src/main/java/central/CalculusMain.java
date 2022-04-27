/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package central;

import init.MegaMain;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author desharnc27
 */
public class CalculusMain {

    public static double probability(int level, long nbPoss) {
        int fullRounds = level / Statix.NB_GROUPS;
        int inTrLevel = level % Statix.NB_GROUPS;
        long bigFact = Misc.fact(Statix.NB_GROUPS);
        long smallFact = Misc.fact(Statix.NB_GROUPS - inTrLevel);
        long denom = Misc.power(bigFact, fullRounds + 1) / (Statix.NB_GROUPS * smallFact);
        return nbPoss / ((double) (denom));

    }

    public static void main0() {
        BankAndWaiters.intitializeLists();
        Node root = new Node();
        root.expand();
        //Qatar auto number one
        for (int i = 0; i < BankAndWaiters.bank[1].list.size(); i++) {
            Node n = BankAndWaiters.bank[1].list.get(i);
            if (n.potsState[0][0] == 4) {
                //nothing to doIfLong
                //n.probability =1.0;
            } else {
                BankAndWaiters.bank[1].list.remove(i);
                i--;
            }
        }
        StateComparator scmp = new StateComparator();
        System.out.println("NbNodes one level " + (1) + ": " + BankAndWaiters.bank[1].list.size());
        long tStart = System.currentTimeMillis();
        for (int i = 1; i < 32; i++) {

            long probCount = 0;

            int limit = BankAndWaiters.bank[i].list.size();
            if (i == 24) {
                limit /= 1000;
            }
            for (int j = 0; j < limit; j++) {
                Node node = BankAndWaiters.bank[i].list.get(j);

                //node.printPotsState();
                //nodeDebug(node);
                //nodeAscending(node);
                //nodeDebugTot(node);
                probCount += node.totNbPoss;
                node.expand();
                //if (i==9 || i==8)
                //    node.printPotsState();
            }
            System.out.println("NbNodes one level " + (i + 1) + ": " + BankAndWaiters.bank[i + 1].list.size());
            System.out.println("Sum of Probability: " + probability(i, probCount));

            BankAndWaiters.bank[i + 1].list.sort(scmp);
            for (int j = 0; j < BankAndWaiters.waiters[i + 1].list.size(); j++) {
                Node wn = BankAndWaiters.waiters[i + 1].list.get(j);
                int idx = Collections.binarySearch(BankAndWaiters.bank[i + 1].list, wn, scmp);
                if (idx < 0) {
                    /*System.out.println("WTF");
                    wn.printPotsState();
                    idx = -idx - 1;
                    BankAndWaiters.bank[i+1].list.get(idx-1).printPotsState();
                    BankAndWaiters.bank[i+1].list.get(idx).printPotsState();
                    int a= 1/0;*/

                    //An extremely rare case but possible
                    idx = -idx - 1;
                    BankAndWaiters.bank[i + 1].list.add(idx, wn);

                } else {
                    Node bn = BankAndWaiters.bank[i + 1].list.get(idx);
                    bn.totNbPoss += wn.totNbPoss;
                }
            }
            System.out.println("Time: " + (System.currentTimeMillis() - tStart) / 60000.0);

            //Clerance of memory
            if (i % Statix.NB_GROUPS != 0) {
                freeLevel(i);
            }
        }
        long accProb0 = 0;
        long accProb1 = 0;
        long accProb2 = 0;
        long accProb3 = 0;
        for (int i = 0; i < BankAndWaiters.bank[16].list.size(); i++) {
            //System.out.print(i+": ");
            Node n = BankAndWaiters.bank[16].list.get(i);
            for (int j = 0; j < Statix.NB_GROUPS; j++) {
                if (n.potsState[0][j] == 0 && n.potsState[1][j] == 0) {
                    accProb0 += n.totNbPoss;
                }
                if (n.potsState[0][j] == 0 && n.potsState[1][j] == 1) {
                    accProb1 += n.totNbPoss;
                }
                if (n.potsState[0][j] == 0 && n.potsState[1][j] == 2) {
                    accProb2 += n.totNbPoss;
                }
                if (n.potsState[0][j] == 0 && n.potsState[2][j] == 0) {
                    accProb3 += n.totNbPoss;
                }
            }
            //n.printPotsState();
            //System.out.println(Arrays.toString(n.trSequence));
        }
        int level = 16;
        double b0 = probability(level, accProb0) / 25;
        double b1 = probability(level, accProb1) / 5;
        double b2 = probability(level, accProb2) / 10;
        double b3 = probability(level, accProb3) / 10;
        System.out.println(accProb0);
        System.out.println(b0);
        System.out.println(accProb1);
        System.out.println(b1);
        System.out.println(accProb2);
        System.out.println(b2);
        System.out.println(accProb3);
        System.out.println(b3);
        System.out.println(5 * b0 + b1 + 2 * b2);
        System.out.println(pairProbability(0, 0, 2, 0));
        System.out.println(pairProbability(0, 0, 2, 3));
        System.out.println(pairProbability(0, 0, 2, 4));

    }

    public static double pairProbability(int pot0, int cont0, int pot1, int cont1) {
        return pairProbability((byte) pot0, (byte) cont0, (byte) pot1, (byte) cont1);
    }

    public static double pairProbability(byte pot0, byte cont0, byte pot1, byte cont1) {
        byte dataLevel = 24;
        long sum = 0;
        for (int i = 0; i < BankAndWaiters.bank[dataLevel].list.size(); i++) {
            //System.out.print(i+": ");
            Node n = BankAndWaiters.bank[dataLevel].list.get(i);
            for (int j = 0; j < Statix.NB_GROUPS; j++) {
                if (n.potsState[pot0][j] == cont0 && n.potsState[pot1][j] == cont1) {
                    sum += n.totNbPoss;
                }
            }
            //n.printPotsState();
            //System.out.println(Arrays.toString(n.trSequence));
        }
        if (sum == 0) {
            return 0;
        }
        double res = probability(dataLevel, sum);
        int isomorphisms = Statix.pots[pot0][cont0] * Statix.pots[pot1][cont1];
        return res / isomorphisms;
    }

    public static void freeLevel(int level) {
        destroy(BankAndWaiters.bank[level]);
        destroy(BankAndWaiters.waiters[level]);
        BankAndWaiters.bank[level] = null;
        BankAndWaiters.waiters[level] = null;
        System.gc();
    }

    private static void destroy(NodeList nodeList) {
        /*while (!nodeList.list.isEmpty()){
            Node n = nodeList.list.remove(0);
        }*/
        nodeList.list.clear();
    }

    public static void main1() {
        main3();
        Node root = new Node();
        root.bigExpand();

        Stats.print();
        DebugData.printCounters();
        Stats.printPairs();

    }

    public static void main3() {
        Node qatarNode = new Node(0f);

        qatarNode.wakProcess();
        AscendStorer.printSome(1);
    }

    public static void main(String[] args) {
        run(MegaMain.ROOT_STR + File.separator + "tempSavior");

    }

    public static void run(String filename) {
        DebugData.initialize();
        long t0 = System.currentTimeMillis();
        main1();
        long t1 = System.currentTimeMillis();
        Stats.StoreInfile(filename);
        //AscendStorer.printDetailedSome(100000);
        //AscendStorer.printSome(1);
        System.out.println("Total time: " + (t1 - t0 + 0.0) / 1000);
    }

}
