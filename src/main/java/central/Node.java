/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package central;

import java.util.Arrays;
import java.util.Collections;
import tools.GeneralMeths;

/**
 *
 * @author desharnc27
 */
public final class Node {

    int level;
    byte[][] potsState;
    int[] trRemains;
    long totNbPoss;

    double probability() {
        return CalculusMain.probability(level, totNbPoss);
    }

    void expand(NodeList childs, NodeList waiters, AscendStorer asSt, byte[] perm) {

        if (level < Statix.initConstDraws.size()) {
            //Host case
            byte group = (byte) level;
            byte cont = Statix.initConstDraws.get(group);
            Node child = new Node(this, cont, group, 1);
            childs.list.add(child);
            return;
        }
        for (byte cont = 0; cont < trRemains.length; cont++) {
            if (trRemains[cont] == 0) {
                continue;
            }
            byte group = asSt == null ? this.leftMostPlace(cont) : this.leftMostPlace(cont, asSt, perm);
            long newNbPoss = totNbPoss * trRemains[cont];
            Node child = new Node(this, cont, group, newNbPoss);

            boolean disorderHint = child.hasDisorderHint(group);

            if (disorderHint) {
                waiters.list.add(child);
            } else {
                childs.list.add(child);
            }

        }
    }

    void expand(NodeList childs, NodeList waiters) {
        expand(childs, waiters, null, null);
    }

    void expand() {
        expand(BankAndWaiters.bank[level + 1], BankAndWaiters.waiters[level + 1]);
    }

    /**
     * Constructor of level 1 node (qatar hosts so always is in group A)
     *
     * @param qatarFloat useless parameter, just to distinguish from level 0
     * constructors
     */
    public Node(float qatarFloat) {
        this();
        for (int i = 0; i < Statix.initConstDraws.size(); i++) {
            byte cont = Statix.initConstDraws.get(i);
            potsState[0][level] = cont;
            trRemains[cont]--;
            level++;

        }
    }

    public Node() {
        level = 0;
        potsState = new byte[1][Statix.NB_GROUPS];
        for (int i = 0; i < Statix.NB_GROUPS; i++) {
            potsState[0][i] = -1;
        }
        trRemains = Statix.getCopyOfPot(0);
        totNbPoss = 1;
    }

    public Node(Node parent, byte cont, byte group, long newNbPoss) {
        level = parent.level + 1;
        int round = level / Statix.NB_GROUPS;

        int inTrLevel = level % Statix.NB_GROUPS;

        if (inTrLevel == 0) {
            if (round < 4) {
                trRemains = Statix.getCopyOfPot(round);
                potsState = new byte[round + 1][Statix.NB_GROUPS];
                for (int i = 0; i < round; i++) {
                    System.arraycopy(parent.potsState[i], 0, potsState[i], 0, Statix.NB_GROUPS);
                }
                for (int i = 0; i < Statix.NB_GROUPS; i++) {
                    potsState[round][i] = -1;
                }
                potsState[round - 1][group] = cont;
            } else {
                trRemains = new int[0];
                potsState = GeneralMeths.getCopy(parent.potsState);
                potsState[round - 1][group] = cont;
                if (!this.completeValidation()) {
                    this.printPotsState();
                    int a = 1 / 0;
                }
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
        totNbPoss = newNbPoss;

    }

    /**
     * Checks wether last element was inserted int a group left to another group
     * that has already been filled to same level
     *
     * @param lastGroup
     * @return
     */
    final boolean hasDisorderHint(byte lastGroup) {
        int lineToCheck = (level - 1) / 8;
        if (lineToCheck < 0) {
            return false;
        }
        for (int i = lastGroup + 1; i < Statix.NB_GROUPS; i++) {
            if (potsState[lineToCheck][i] >= 0) {
                return true;
            }
        }
        return false;

    }

    /**
     * Checks if after adding teamAdd in groupAdd, the resulting configuration
     * has a viable outcome.
     *
     * @param teamAdd
     * @param groupAdd
     * @return
     */
    boolean isCompletable(byte teamAdd, byte groupAdd) {
        int round = level / Statix.NB_GROUPS;
        potsState[round][groupAdd] = teamAdd;
        trRemains[teamAdd]--;
        boolean res;
        if (level % Statix.NB_GROUPS == -1) {
            res = true;
        } else {
            res = isCompletable();
        }
        trRemains[teamAdd]++;
        potsState[round][groupAdd] = -1;
        return res;
    }

    /**
     * Checks if actual configuration has a viable outcome.
     *
     * @param teamAdd
     * @param groupAdd
     * @return Does not work on last round TODO: remake? and explain
     */
    //TODO this whole function has been deleted
    boolean isCompletable() {
        return true;
    }

    boolean groupCanTake(int group, byte multiCont) {
        int round = potsState.length - 1;
        if (potsState[round][group] >= 0) {
            return false;
        }
        byte[] conts = getMonoConts(multiCont);

        //maxima checks
        for (byte cont : conts) {
            int tolerance = Statix.getMonoContMax(cont);
            for (int i = 0; i < round; i++) {
                if (cont == potsState[i][group]) {
                    tolerance--;
                }
                /*try {
                    if (cont == potsState[i][group]) {
                        if (!euroExtra) {
                            return false;
                        }
                        euroExtra = false;
                    }
                } catch (Exception e) {
                    int a = 1 / 0;
                }*/

            }
            if (tolerance == 0) {
                return false;
            }

        }
        if (round < 3) {
            return true;
        }
        //minima checks
        int[] bilan = new int[Statix.NB_MONOCONTS];
        for (int i = 0; i < conts.length; i++) {
            bilan[conts[i]]++;
        }
        for (int r = 0; r < 3; r++) {
            multiCont = potsState[r][group];
            conts = getMonoConts(multiCont);
            for (int i = 0; i < conts.length; i++) {
                bilan[conts[i]]++;
            }
        }
        for (byte i = 0; i < bilan.length; i++) {
            if (bilan[i] < Statix.getMonoContMin(i)) {
                return false;
            }
        }
        /*//At least one euro team
        if (round >= 3 && multiCont != 0) {
            for (int i = 0; i < 3; i++) {
                if (potsState[i][group] == 0) {
                    return true;
                }
            }
            return false;
        }*/
        return true;

    }

    /**
     * checks if continents cont and monoCont match. if cont is not a monocont,
     * checks if one of its contients matches cont.
     *
     * @param monoCont continent (cannot be a multiple continent)
     * @param cont continent (can be a multiple continent)
     * @return true if monoCont and cont match, false otherwise.
     */
    static boolean sameCont(byte monoCont, byte cont) {
        if (cont < Statix.NB_CONTS) {
            return (monoCont == cont);
        }
        if (cont == Statix.NB_MONOCONTS + 1) {
            switch (monoCont) {
                case 1,4:
                    return true;
                default:
                    return false;

            }
        }
        return false;
    }

    static byte[] getMonoConts(byte cont) {
        if (cont < Statix.NB_MONOCONTS) {
            return (new byte[]{cont});
        }
        if (cont == Statix.NB_MONOCONTS) {
            return (new byte[]{1, 4});
        }

        System.out.println("WTF: static byte [] getMonoConts(byte multiCont) ");
        return null;

    }

    byte leftMostPlace(byte cont) {
        for (byte i = 0; i < Statix.NB_GROUPS; i++) {

            //You may uncomment 2 following lines while debugging to break continental constraints
            //if (potsState[potsState.length-1][i]==-1)
            //    return i;
            if (!groupCanTake(i, cont)) {
                continue;
            }
            if (isCompletable(cont, i)) {
                return i;
            }

        }
        System.out.println("Fatal error: dead end, this continent can't fit anywhere: " + cont);
        //Major bug, program should never reach this line 
        this.printPotsState();
        int a = 1 / 0;

        return -1;
    }

    public byte leftMostPlace(byte cont, AscendStorer ascendStorer, byte[] perm) {
        if (new StateComparator().compare(this, DebugData.node) == 0) {
            int debug = 1;
        }
        int round = level / Statix.NB_GROUPS;
        byte[] toResearch = new byte[Statix.NB_GROUPS];
        byte[] invPerm = Misc.reciprocal(perm);
        for (int i = 0; i < Statix.NB_GROUPS; i++) {
            toResearch[invPerm[i]] = potsState[round][i];
        }
        for (byte i = 0; i < Statix.NB_GROUPS; i++) {
            if (potsState[round][i] != -1) {
                continue;
            }
            toResearch[invPerm[i]] = cont;
            //TODO might euro check needed
            boolean doable = ascendStorer.has(toResearch);
            if (doable) {
                return i;
            }
            toResearch[invPerm[i]] = -1;

        }

        System.out.println("Fatal error: dead end, this continent can't fit anywhere: " + cont);
        //Major bug, program should never reach this line 
        this.printPotsState();
        int a = 1 / 0;

        return -1;
    }

    final void putInWaiters() {
        BankAndWaiters.waiters[level].list.add(this);
    }

    final void putInBank() {
        BankAndWaiters.bank[level].list.add(this);
    }

    @Override
    public String toString() {
        //TODO
        return null;

    }

    public void printPotsState() {
        System.out.println("NodeState: " + level);
        for (byte[] potsState1 : potsState) {
            System.out.println(Arrays.toString(potsState1));
        }

    }

    /**
     * Generate all doable nodes 8 levels (a full round) after this node
     * (probabilities included)
     *
     * @param asSt catalog containing potent children. May and should be left to
     * null for early rounds (0-1-2)
     * @param perm indicates column permutation that allows to look in asSt.
     * Must be null iff asSt is null
     * @return all doable nodes 8 levels (a full round) after this node
     * (probabilities included)
     */
    public NodeList octoChilds(AscendStorer asSt, byte[] perm) {
        NodeList actual = new NodeList();
        actual.list.add(this);

        //breath first search , level by level
        for (int i = 0; i < Statix.NB_GROUPS; i++) {
            NodeList nextChilds = new NodeList();
            NodeList nextWaiters = new NodeList();

            //Qatar first mandatory
            if (this.level == 0 && i == 1) {
                for (int k = 0; k < actual.list.size(); k++) {
                    Node n = actual.list.get(k);
                    if (n.potsState[0][0] == 4) {

                    } else {
                        actual.list.remove(k);
                        k--;
                    }
                }
            }

            int limit = actual.list.size();
            for (int j = 0; j < limit; j++) {
                Node node = actual.list.get(j);
                node.expand(nextChilds, nextWaiters, asSt, perm);
            }
            nextChilds.sort(Statix.scmp);
            for (int j = 0; j < nextWaiters.list.size(); j++) {
                Node wn = nextWaiters.list.get(j);
                int idx = Collections.binarySearch(nextChilds.list, wn, Statix.scmp);
                if (idx < 0) {
                    //An extremely rare case but possible
                    idx = -idx - 1;
                    nextChilds.list.add(idx, wn);

                } else {
                    Node bn = nextChilds.list.get(idx);
                    bn.totNbPoss += wn.totNbPoss;
                }
            }
            actual = nextChilds;

        }
        return actual;

    }

    public void bigExpand() {
        int round = level / Statix.NB_GROUPS;
        if (round == Stats.analyzedRounds) {
            /*if (DebugData.diffCounter==0){
                this.printPotsState();
                leftRightExpand((byte)0, null);
            }*/

            DebugData.diffCounter++;
            DebugData.possCounter += this.totNbPoss;

            Stats.feed(potsState, this.totNbPoss);
            return;
        }
        NodeList octoChilds;
        if (round >= Stats.noRefRounds && round < Stats.analyzedRounds) {
            byte[][] ascendStorerPerm = AscendStorer.getAscendStorerPerm(this);
            byte[] perm = ascendStorerPerm[round];

            AscendStorer asSt = AscendStorer.getAscendStorer(ascendStorerPerm);
            octoChilds = octoChilds(asSt, perm);
            if (round == 3) {
                DebugData.tanosCounter++;
                if (DebugData.tanosCounter % 1000 == 0) {
                    System.out.println(DebugData.tanosCounter);
                }
            }
        } else {
            octoChilds = octoChilds(null, null);
        }
        for (int i = 0; i < octoChilds.list.size(); i++) {
            Node n = octoChilds.list.get(i);
            n.bigExpand();
        }

    }

    protected void bigLeftRightExpandWak(AscendStorer asSt, int roundToDetail) {
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
            if (round == roundToDetail + 1) {
                if (round == Stats.analyzedRounds || !AscendStorer.deadEndOnNextLevel(this)) {
                    asSt.injectParts(potsState[roundToDetail]);
                }
                return;
            }
            if (!hasAscendingColumns()) {
                return;
            }
            if (round == roundToDetail) {
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
            /*if (!isCompletable( i,groupIdx)) {
                continue;
            }*/
            trRemains[i]--;
            potsState[round][groupIdx] = i;
            level++;

            bigLeftRightExpandWak(asSt, roundToDetail);
            level--;
            trRemains[i]++;
            potsState[round][groupIdx] = -1;
        }
        if (virginLineAdd) {
            potsState = Misc.removeVirginLine(potsState);
            trRemains = new int[Statix.NB_CONTS];
        }
        if (newDygon) {
            int ascendStorerIdx = Collections.binarySearch(AscendStorer.ascendStorerBank, asSt);
            ascendStorerIdx = -ascendStorerIdx - 1;
            AscendStorer.ascendStorerBank.add(ascendStorerIdx, asSt);
        }

    }

    public void wakProcess() {
        for (int i = Stats.analyzedRounds - 1; i >= Stats.noRefRounds; i--) {
            bigLeftRightExpandWak(null, i);
            System.out.println("QwErtYuIoPP");
        }
    }

    public boolean hasAscendingColumns() {
        for (int i = 1; i < Statix.NB_GROUPS - 1; i++) {
            if (AscendStorer.columnCompare(potsState, i, i + 1) > 0) {
                return false;
            }
        }
        return true;
    }

    private boolean completeValidation() {
        for (byte gr = 0; gr < Statix.NB_GROUPS; gr++) {
            if (!completeValidation(gr)) {
                return false;
            }
        }
        return true;

    }

    //Should only be used at final level 32
    private boolean completeValidation(byte gr) {
        boolean[] used = new boolean[Statix.NB_MONOCONTS];
        boolean euroExtra = true;
        for (int i = 0; i < 4; i++) {
            byte[] conts = Node.getMonoConts(potsState[i][gr]);
            for (byte cont : conts) {
                if (used[cont]) {
                    if (cont == 0 || euroExtra) {
                        euroExtra = false;
                    } else {
                        return false;
                    }
                } else {
                    used[cont] = true;
                }
            }

        }
        return true;
    }

    public boolean imbalance() {
        //int trIdx = level/8;
        //if (trIdx==0)
        //    return false;
        int lastLine = potsState.length - 1;
        int[] copyOfTr = Arrays.copyOf(trRemains, Statix.NB_CONTS);
        for (int i = 0; i < Statix.NB_GROUPS; i++) {
            if (potsState[lastLine][i] >= 0) {
                copyOfTr[potsState[lastLine][i]] += 1;
            }
        }
        int[] initPot = Statix.getCopyOfPot(lastLine);
        for (int i = 0; i < Statix.NB_CONTS; i++) {
            if (initPot[i] != copyOfTr[i]) {
                return true;
            }
        }
        return false;
    }

}
