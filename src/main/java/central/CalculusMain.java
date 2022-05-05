/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package central;

import algocore.AscendStorer;
import algocore.Node;
import init.MegaMain;
import java.io.File;

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

    public static void main1() {
        buildAscendStorage();
        Node root = new Node();
        root.bigExpand();

        Stats.print();
        DebugData.printCounters();
        Stats.printPairs();

    }

    public static void buildAscendStorage() {
        //Node qatarNode = new Node(0f);
        Node initNode = Node.postHostsNode();
        initNode.buildAscendStorageLayers();
        AscendStorer.printSome(1);
    }

    public static void main(String[] args) {
        run(MegaMain.ROOT_STR + File.separator + "tempSavior");

    }

    public static void run(String filename) {

        long t0 = System.currentTimeMillis();
        main1();
        long t1 = System.currentTimeMillis();
        Stats.StoreInfile(filename);
        //AscendStorer.printDetailedSome(100000);
        //AscendStorer.printSome(1);
        System.out.println("Total time: " + (t1 - t0 + 0.0) / 1000);
    }

}
