/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package garbage;

import static algocore.AscendStorer.columnCompare;
import static algocore.AscendStorer.columnSwap;
import central.Statix;

/**
 *
 * @author desharnc27
 */
public class GarbageCode {

    //From Node
    /*
    //xbegin
    
    public Node() {
        level = parent.level + 1;
        int round = level / Statix.nbGROUPS();

        int inTrLevel = level % Statix.nbGROUPS();

        if (inTrLevel == 0) {
            if (round < 4) {
                trRemains = Statix.getCopyOfPot(round);
                potsState = new byte[round + 1][Statix.nbGROUPS()];
                for (int i = 0; i < round; i++) {
                    System.arraycopy(parent.potsState[i], 0, potsState[i], 0, Statix.nbGROUPS());
                }
                for (int i = 0; i < Statix.nbGROUPS(); i++) {
                    potsState[round][i] = -1;
                }
                potsState[round - 1][group] = cont;
            } else {
                trRemains = new int[0];
                potsState = GeneralMeths.getCopy(parent.potsState);
                potsState[round - 1][group] = cont;
            }

        } else {

            potsState = GeneralMeths.getCopy(parent.potsState);
            potsState[round][group] = cont;

            trRemains = Arrays.copyOf(parent.trRemains, parent.trRemains.length);
            trRemains[cont]--;
        }
        if (round == 4) {

            boolean ok = completeValidation();
            if (!ok) {
                printPotsState();
                int debug = 1;
            }

        }
    }
    
    boolean twado(byte teamAdd, byte groupAdd) {
        //ArrayList<Byte> noException = new ArrayList<>();
        boolean[] isException = new boolean[Statix.NB_CONTS];
        boolean euroOne = false;
        int nbc = Statix.NB_CONTS;
        for (int i = 0; i < 4; i++) {
            byte byteTemp = potsState[i][groupAdd];
            if (byteTemp == 0 && !euroOne) {
                euroOne = true;
            } else {
                isException[byteTemp] = true;
                nbc--;
            }

        }

        int idx = 0;
        byte[] trad = new byte[nbc];
        for (int i = 0; i < isException.length; i++) {
            if (!isException[i]) {
                trad[idx++] = (byte) i;
            }
        }

        boolean[] iterArr = new boolean[nbc];

        do {
            int dispo = 0;
            for (int gr = 0; gr < Statix.NB_GROUPS; gr++) {
                boolean takable = false;
                for (int i = 0; i < nbc; i++) {
                    if (!iterArr[i]) {
                        continue;
                    }
                    byte cont = trad[i];
                    if (groupCanTake(gr, cont)) {
                        takable = true;
                        break;
                    }
                }
                if (takable) {
                    dispo++;
                }

            }
            for (int i = 0; i < nbc; i++) {
                if (iterArr[i]) {
                    byte cont = trad[i];
                    dispo -= trRemains[cont];
                }
            }
            if (dispo < 0) {
                return false;
            }

        } while (CombMeths.boolIter(iterArr));
        return true;
    }
    
    protected void leftRightExpand(byte groupIdx, ArrayList<ByteArray> avaBank) {
        if (groupIdx == Statix.NB_GROUPS) {
            //System.out.println(Arrays.toString(potsState[3]));
            AscendStorer.includePossibleLastPot(potsState[3],avaBank);
            return;
        }
        for (byte i = 0; i < Statix.NB_CONTS; i++) {
            if (trRemains[i] == 0) {
                continue;
            }
            if (!groupCanTake(groupIdx, i)) {
                continue;
            }
            if (!isCompletable( i,groupIdx)) {
                continue;
            }
            trRemains[i]--;
            potsState[3][groupIdx] = i;
            leftRightExpand((byte) (groupIdx + 1), avaBank);
            trRemains[i]++;
            potsState[3][groupIdx] = -1;
        }
    }

    
    protected void bigLeftRightExpandOri() {
        bigLeftRightExpandOri(null);
    }

    protected void bigLeftRightExpandOri(AscendStorer asSt) {
        byte groupIdx = (byte) (level % Statix.NB_GROUPS);
        byte round = (byte) (level / Statix.NB_GROUPS);
        //AscendStorer newAscendStorer;
        boolean virginLineAdd = false;

        boolean newDygon = false;
        if (new StateComparator().compare(this, DebugData.node) == 0) {
            int debug = 1;
        }
        if (groupIdx == 0) {
            //System.out.println(Arrays.toString(potsState[3]));
            if (round == 4) {
                asSt.injectParts(potsState[3]);
                return;
            }
            if (!hasAscendingColumns()) {
                return;
            }
            if (round == 3) {
                newDygon = true;
                asSt = AscendStorer.buildWithPots(potsState);

            }
            virginLineAdd = true;
            potsState = Misc.addVirginLine(potsState);
            trRemains = Statix.getCopyOfPot(round);

        }
        for (byte i = 0; i < Statix.NB_CONTS; i++) {
            if (trRemains[i] == 0) {
                continue;
            }
            if (!groupCanTake(groupIdx, i)) {
                continue;
            }

            trRemains[i]--;
            potsState[round][groupIdx] = i;
            level++;
            bigLeftRightExpandOri(asSt);
            level--;
            trRemains[i]++;
            potsState[round][groupIdx] = -1;
        }
        if (virginLineAdd) {
            potsState = Misc.removeVirginLine(potsState);
            trRemains = trRemains = new int[Statix.NB_CONTS];
        }
        if (newDygon) {
            int ascendStorerIdx = Collections.binarySearch(AscendStorer.ascendStorerBank, asSt);
            ascendStorerIdx = -ascendStorerIdx - 1;
            AscendStorer.ascendStorerBank.add(ascendStorerIdx, asSt);
        }

    }
    protected void bigLeftRightExpand() {
        bigLeftRightExpand(new AscendStorer[3]);
    }
    
    protected void bigLeftRightExpand(AscendStorer  [] asArray) {
        byte groupIdx = (byte) (level % Statix.NB_GROUPS);
        byte round = (byte) (level / Statix.NB_GROUPS);
        //AscendStorer newAscendStorer;
        boolean virginLineAdd = false;
        
        byte newDygon = -1;
        if (new StateComparator().compare(this, DebugData.node) == 0) {
            int debug = 1;
        }
        if (groupIdx == 0) {
            //System.out.println(Arrays.toString(potsState[3]));
            if (round >= 2) {
                asArray[round-2].injectParts(potsState[round-1]);
            }
            if (round == 4)
                return;
            if (round == 1){
                    int debug = 1;
            }
            if (!hasAscendingColumns()) {
                return;
            }
            if (round >0 && round <4) {
                if (round == 1){
                    int debug = 1;
                }
                    
                asArray[round-1] =  AscendStorer.buildWithPots(potsState);
                newDygon = round;

            }
            virginLineAdd = true;
            potsState = Misc.addVirginLine(potsState);
            trRemains = Statix.getCopyOfPot(round);

        }
        for (byte i = 0; i < Statix.NB_CONTS; i++) {
            if (trRemains[i] == 0) {
                continue;
            }
            if (!groupCanTake(groupIdx, i)) {
                continue;
            }

            trRemains[i]--;
            potsState[round][groupIdx] = i;
            level++;
            bigLeftRightExpand(asArray);
            level--;
            trRemains[i]++;
            potsState[round][groupIdx] = -1;
        }
        if (virginLineAdd) {
            potsState = Misc.removeVirginLine(potsState);
            trRemains = trRemains = new int[Statix.NB_CONTS];
        }
        if (newDygon>=0) {
            //????
            int arrIdx = newDygon -1;
            

            int ascendStorerIdx = Collections.binarySearch(AscendStorer.ascendStorerBank, asArray[arrIdx]);
            ascendStorerIdx = -ascendStorerIdx - 1;
            AscendStorer.ascendStorerBank.add(ascendStorerIdx, asArray[arrIdx]);
            asArray[arrIdx] = null;
        }
    
    }
    
    boolean isCompletable() {

        if (new StateComparator().compare(this, DebugData.node) == 0) {
            int debug = 1;
        }

        int[] pseudoRemain = Arrays.copyOf(trRemains, Statix.NB_MONOCONTS);
        for (byte cont : getMonoConts((byte) (Statix.NB_MONOCONTS))) {
            try {
                pseudoRemain[cont] += trRemains[Statix.NB_MONOCONTS];
            } catch (Exception e) {
                int a = 1 / 0;
            }
        }

        for (byte cont = 0; cont < Statix.NB_MONOCONTS; cont++) {
            int dispo = 0;
            for (int gr = 0; gr < Statix.NB_GROUPS; gr++) {
                if (groupCanTake(gr, cont)) {
                    dispo++;
                }
            }
            if (dispo < pseudoRemain[cont]) {
                return false;
            }

        }
        //extreme euro case: even without immediate rule break, completing round 2 (pot 3)
        //leads to undoable round 3 if two groups don't have a euro team before last round. 
        int round = potsState.length - 1;
        if (round == 2) {
            int noEuro = 0;
            int doubleEuro = 0;
            for (int i = 0; i < Statix.NB_GROUPS; i++) {
                //if (potsState[round][i]==-1)
                //    continue;
                int euroCount = 0;
                for (int j = 0; j < 3; j++) {
                    if (potsState[j][i] == 0) {
                        euroCount++;
                    }
                }
                if (euroCount == 0 && potsState[round][i] != -1) {
                    noEuro++;
                } else if (euroCount == 2) {
                    doubleEuro++;
                }
            }
            if (noEuro >= 2) {
                return false;
            }
            if (doubleEuro >= 6) {
                return false;
            }

        }

        //for hybird group, TODO generalize
        if (trRemains[Statix.NB_MONOCONTS] == 0) {
            return true;
        }
        for (int gr = 0; gr < Statix.NB_GROUPS; gr++) {
            if (groupCanTake(gr, (byte) 1) && groupCanTake(gr, (byte) 4)) {
                return true;
            }
        }

        return false;
    }
    //from Old main
    
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
    
    
    //From MegaMain
    
    public static void main1() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome!");
        String filename = askWithDefault(scanner, "What file should the teams be loaded from?", teamsFileName);
        Loading.load(fullName(filename));
        System.out.println("Should we calculate from scratch? If yes, enter " + GUIL_NO);
        System.out.println("Otherwise, enter the name of the file from which data will be imported.");
        String nameToLoad = scanner.next();
        if (nameToLoad.equals(SYMB_NO)) {
            System.out.println("Should calculations be saved in a file for next time?");
            System.out.println("Type the name of the file you wish to use, or  " + GUIL_NO + " for no save.");
            filename = scanner.next();
            //String fullName = filename.length <0 
            if (filename.equals(SYMB_NO)) {
                filename = null;
            }
            CalculusMain.run(fullName(filename));

        } else {
            try {
                Stats.loadFromFile(nameToLoad);
            } catch (InitParseException | IOException ex) {
                System.out.println(ex);
            }
        }
        boolean still = true;

        while (still) {
            DebugData.cmdCounter = 0;
            try {
                still = manageProbaParamCmd(scanner);
            } catch (ProbaParamEx ex) {
                System.out.println(ex);
            }
            System.out.println("DebugData.cmdCounter: " + DebugData.cmdCounter);
        }

    }
    
    //From FormerMain
    public static double pairProbability(int pot0, int cont0, int pot1, int cont1) {
        return pairProbability((byte) pot0, (byte) cont0, (byte) pot1, (byte) cont1);
    }

    public static double pairProbability(byte pot0, byte cont0, byte pot1, byte cont1) {
        byte dataLevel = 24;
        long sum = 0;
        for (int i = 0; i < BankAndWaiters.bank[dataLevel].size(); i++) {
            //System.out.print(i+": ");
            Node n = BankAndWaiters.bank[dataLevel].get(i);
            for (int j = 0; j < Statix.nbGROUPS(); j++) {
                if (n.getCont(pot0, j) == cont0 && n.getCont(pot1, j) == cont1) {
                    sum += n.getNbPoss();
                }
            }
            //n.printPotsState();
            //System.out.println(Arrays.toString(n.trSequence));
        }
        if (sum == 0) {
            return 0;
        }
        double res = Statix.probability(dataLevel, sum);
        int isomorphisms = Statix.getContCount(pot0, cont0) * Statix.getContCount(pot1, cont1);
        return res / isomorphisms;
    }

    //From AscendStorer
    
    public static void ascendiumSort(byte[][] arr) {
        for (int i = Statix.nbHOSTS(); i < Statix.nbGROUPS(); i++) {
            for (int j = i + 1; j < Statix.nbGROUPS(); j++) {
                if (columnCompare(arr, i, j) > 0) {
                    columnSwap(arr, i, j);
                }
            }
        }
    }

    //xend
     */
}
