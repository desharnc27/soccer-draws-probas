/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package central;

import algocore.AscendStorer;
import algocore.Node;

/**
 *
 * @author desharnc27
 */
public class CalculusMain {

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
        nodeList.clear();
    }

    public static void buildExactStats(StatByConts stats) {
        //buildAscendStorage();
        Node root = new Node();
        long t0 = System.currentTimeMillis();
        root.bigExpand(stats);
        long t1 = System.currentTimeMillis();
        System.out.println("Total time: " + (t1 - t0 + 0.0) / 1000);

    }

    public static void buildAscendStorage() {
        //MAke sure it's not already built
        if (AscendStorer.detectBuild()) {
            return;
        }
        Node initNode = Node.postHostsNode();
        initNode.buildAscendStorageLayers();
        AscendStorer.printSome(1);
        //AscendStorer.printDetailedSome(100000);
    }

    /*public static void run(StatFida stats,String filename) {

        //long t0 = System.currentTimeMillis();
        buildExactStats( stats);
        //long t1 = System.currentTimeMillis();
        stats.StoreInfile(filename);
        //AscendStorer.printDetailedSome(100000);
        //AscendStorer.printSome(1);
        DebugData.printCounters();
        
    }*/
}
