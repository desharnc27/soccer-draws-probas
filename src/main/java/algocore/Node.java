/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algocore;

import central.BankAndWaiters;
import central.DebugData;
import central.Misc;
import central.NodeList;
import central.StatByConts;
import central.StateComparator;
import central.Statix;
import central.TimerManager;
import java.util.Arrays;
import tools.GeneralMeths;

/**
 *
 * @author desharnc27
 */
public class Node {

    private int level;
    private byte[][] potsState;
    private int[] trRemains;
    private long totNbPoss;

    public long getNbPoss() {
        return totNbPoss;
    }

    public int getLevel() {
        return level;
    }

    public int getRound() {
        return level / Statix.nbGROUPS();
    }

    public int get() {
        return level / Statix.nbGROUPS();
    }

    public byte[][] copyPSWithPlusVirginLine() {
        return Misc.copyPlusVirginLine(potsState);
    }

    public byte[][] copyPotsState() {
        return GeneralMeths.getCopy(potsState);
    }

    public boolean hazardeousVirginLineCheck() {
        return level / Statix.nbGROUPS() < potsState.length;

    }

    public byte getCont(int round, int group) {
        return potsState[round][group];
    }

    public int getPotStateLen() {
        return potsState.length;
    }

    public int potLevCompare(Node other) {
        int comp0 = getLevel() - other.getLevel();
        if (comp0 != 0) {
            return comp0;
        }
        comp0 = potsState.length - other.potsState.length;
        if (comp0 != 0) {
            return comp0;
        }
        return Misc.compareRev(potsState, other.potsState);
    }

    //Using this function should never be necessary, unless for debugging purposes
    public void setPotState(byte[][] potState) {
        this.potsState = potState;
    }

    //Using this function should never be necessary, unless for debugging purposes
    public void resyncLevel() {
        level = 0;
        for (byte[] potsState1 : potsState) {
            for (int j = 0; j < potsState1.length; j++) {
                if (potsState1[j] >= 0) {
                    level++;
                }
            }
        }
    }

    public double probability() {
        /*int fullRounds = level / Statix.nbGROUPS();
        int inTrLevel = level % Statix.nbGROUPS();
        long bigFact = Misc.fact(Statix.nbGROUPS());
        long smallFact = Misc.fact(Statix.nbGROUPS() - inTrLevel);
        long denom = Misc.power(bigFact, fullRounds + 1) / (Statix.nbGROUPS() * smallFact);
        return this.totNbPoss / ((double) (denom));*/
        return totNbPoss / (double) Statix.commonDenominator(level);
    }

    void expand(StatByConts stats, NodeList childs, NodeList waiters, AscendStorer asSt, byte[] perm) {

        if (level < stats.getNbForced()) {
            //Forced case

            int round = level / Statix.nbGROUPS();
            //Finding next group that will be forced
            int virginGroupIdx = Statix.nbGROUPS() - 1;
            while (virginGroupIdx >= 0 && potsState[round][virginGroupIdx] == -1) {
                virginGroupIdx--;
            }
            virginGroupIdx++;
            while (stats.getForcedTeamIdx(round, virginGroupIdx) == -1) {
                virginGroupIdx++;
            }
            int teamIdx = stats.getForcedTeamIdx(round, virginGroupIdx);
            byte group = (byte) virginGroupIdx;
            byte cont = Statix.getTeam(round, teamIdx).cont();
            Node child = new Node(this, cont, group, 1);
            childs.add(child);
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
                waiters.add(child);
            } else {
                childs.add(child);
            }

        }
    }

    void expand(StatByConts stats, NodeList childs, NodeList waiters) {
        expand(stats, childs, waiters, null, null);
    }

    void expand(StatByConts stats) {
        expand(stats, BankAndWaiters.bank[level + 1], BankAndWaiters.waiters[level + 1]);
    }

    public static Node postHostsNode() {
        Node res = new Node();
        for (int i = 0; i < Statix.nbHOSTS(); i++) {
            res = new Node(res, Statix.getHostCont(i), (byte) i, 1);
        }
        return res;

    }

    public Node() {
        level = 0;
        potsState = new byte[1][Statix.nbGROUPS()];
        for (int i = 0; i < Statix.nbGROUPS(); i++) {
            potsState[0][i] = -1;
        }
        trRemains = Statix.getCopyOfPot(0);
        totNbPoss = 1;
    }

    public Node(Node parent, byte cont, byte group, long newNbPoss) {

        level = parent.level;
        potsState = GeneralMeths.getCopy(parent.potsState);
        trRemains = Arrays.copyOf(parent.trRemains, parent.trRemains.length);
        forward(cont, group);
        totNbPoss = newNbPoss;

    }

    /**
     * Dangerous. Removes a draw. Warnings : Should not be called with random
     * arguments since it may lead to corrupted state. It also always corrupts
     * probability attribute so this node should not be used to calculate
     * probabilities.
     *
     * @param cont
     * @param group
     */
    public final void backward(byte cont, byte group) {
        int round = level / Statix.nbGROUPS();
        if (level % Statix.nbGROUPS() == 0) {
            if (this.hazardeousVirginLineCheck()) {
                potsState = Misc.removeVirginLine(potsState);
            }
            trRemains = new int[Statix.nbCONTS()];
            round--;
        }
        level--;
        potsState[round][group] = -1;
        trRemains[cont]++;

    }

    /**
     * Dangerous. Removes a draw. Warnings : Should not be called with random
     * arguments since it may lead to corrupted state. It also always corrupts
     * probability attribute so this node should not be used to calculate
     * probabilities.
     *
     * @param cont
     * @param group
     */
    public final void forward(byte cont, byte group) {
        int prevRound = getRound();
        potsState[prevRound][group] = cont;
        if (level % Statix.nbGROUPS() == Statix.nbGROUPS() - 1) {
            if (level + 1 != 4 * Statix.nbGROUPS()) {
                potsState = Misc.addVirginLine(potsState);
                trRemains = Statix.getCopyOfPot(prevRound + 1);
            }

        } else {
            trRemains[cont]--;
        }
        level++;

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
        for (int i = lastGroup + 1; i < Statix.nbGROUPS(); i++) {
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
        int round = level / Statix.nbGROUPS();
        potsState[round][groupAdd] = teamAdd;
        trRemains[teamAdd]--;
        boolean res;
        if (level % Statix.nbGROUPS() == -1) {
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
        byte[] conts = Statix.getMonoconts(multiCont);

        //maxima checks
        /*for (byte cont : conts) {
            int tolerance = Statix.getMonoContMax(cont);
            for (int i = 0; i < round; i++) {
                if (cont == potsState[i][group]) {
                    tolerance--;
                }

            }
            if (tolerance == 0) {
                return false;
            }

        }*/
        //int[] bilan = new int[Statix.nbMONOCONTS()];
        byte[] upperBilan = new byte[Statix.nbMONOCONTS()];
        byte[] lowerBilan = new byte[Statix.nbMONOCONTS()];
        for (int i = 0; i < conts.length; i++) {
            upperBilan[conts[i]]++;
            if (conts[i] < Statix.nbMONOCONTS()) {
                lowerBilan[conts[i]]++;
            }
        }
        for (int r = 0; r < round; r++) {
            multiCont = potsState[r][group];
            conts = Statix.getMonoconts(multiCont);
            for (int i = 0; i < conts.length; i++) {
                try {
                    upperBilan[conts[i]]++;
                    if (conts[i] < Statix.nbMONOCONTS()) {
                        lowerBilan[conts[i]]++;
                    }
                } catch (Exception e) {
                    int debug = 0;
                }
            }
        }
        for (byte i = 0; i < Statix.nbMONOCONTS(); i++) {
            //minima
            if (lowerBilan[i] < Statix.getMonoContMin(i) + round - 3) {
                return false;
            }
            //maxima
            if (upperBilan[i] > Statix.getMonoContMax(i)) {
                return false;
            }

        }
        return true;

    }

    public byte leftMostPlace(byte cont) {
        for (byte i = 0; i < Statix.nbGROUPS(); i++) {

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
        int crash = 1 / 0;

        return -1;
    }

    public byte leftMostPlace(byte cont, AscendStorer ascendStorer, byte[] perm) {
        if (ascendStorer == null) {
            return leftMostPlace(cont);
        }
        int round = level / Statix.nbGROUPS();
        byte[] toResearch = new byte[Statix.nbGROUPS()];
        byte[] invPerm = Misc.reciprocal(perm);
        for (int i = 0; i < Statix.nbGROUPS(); i++) {
            toResearch[invPerm[i]] = potsState[round][i];
        }
        for (byte i = 0; i < Statix.nbGROUPS(); i++) {
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
        int crash = 1 / 0;

        return -1;
    }

    final void putInWaiters() {
        BankAndWaiters.waiters[level].add(this);
    }

    final void putInBank() {
        BankAndWaiters.bank[level].add(this);
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
     * @param stats
     * @param asSt catalog containing potent children. May and should be left to
     * null for early rounds (0-1-2)
     * @param perm indicates column permutation that allows to look in asSt.
     * Must be null iff asSt is null
     * @return all doable nodes 8 levels (a full round) after this node
     * (probabilities included)
     */
    public NodeList octoChilds(StatByConts stats, AscendStorer asSt, byte[] perm) {
        NodeList actual = new NodeList();
        actual.add(this);

        //breath first search , level by level
        for (int i = 0; i < Statix.nbGROUPS(); i++) {
            NodeList nextChilds = new NodeList();
            NodeList nextWaiters = new NodeList();

            //Qatar first mandatory
            /*if (this.level == 0 && i == 1) {
                for (int k = 0; k < actual.size(); k++) {
                    Node n = actual.get(k);
                    if (n.potsState[0][level] == Statix.) {

                    } else {
                        actual.list.remove(k);
                        k--;
                    }
                }
            }*/
            int limit = actual.size();
            for (int j = 0; j < limit; j++) {
                Node node = actual.get(j);
                node.expand(stats, nextChilds, nextWaiters, asSt, perm);
            }
            nextChilds.sort(Statix.scmp);
            for (int j = 0; j < nextWaiters.size(); j++) {
                Node wn = nextWaiters.get(j);
                int idx = nextChilds.binarysearch(wn);
                if (idx < 0) {
                    //An extremely rare case but possible
                    idx = -idx - 1;
                    nextChilds.add(idx, wn);

                } else {
                    Node bn = nextChilds.get(idx);
                    bn.totNbPoss += wn.totNbPoss;
                }
            }
            actual = nextChilds;

        }
        return actual;

    }

    public void bigExpand(StatByConts stats) {
        /*System.out.println("----");
        this.printPotsState();
        System.out.println("totNbPoss: "+totNbPoss);
        System.out.println("----");*/
        if (new StateComparator().compare(this, DebugData.node) == 0) {
            int debug = 1;
        }

        int round = level / Statix.nbGROUPS();
        if (round == Statix.NB_ANALYZED_ROUNDS) {
            /*if (DebugData.diffCounter==0){
                this.printPotsState();
                leftRightExpand((byte)0, null);
            }*/

            DebugData.diffCounter++;
            DebugData.possCounter += this.totNbPoss;
            DebugData.probCounter += this.probability();

            stats.feed(potsState, this.totNbPoss);
            return;
        }
        NodeList octoChilds;

        if (round >= Statix.ALGO_ASCEND_SWITCH && round < Statix.NB_ANALYZED_ROUNDS) {
            byte[][] ascendStorerPerm = AscendStorer.getAscendStorerPerm(this);
            byte[] perm = ascendStorerPerm[round];

            AscendStorer asSt = AscendStorer.getAscendStorer(ascendStorerPerm);
            octoChilds = octoChilds(stats, asSt, perm);
            if (round == 3) {
                DebugData.tanosCounter++;
                /*if (DebugData.tanosCounter % 1000 == 0) {
                    System.out.println(DebugData.tanosCounter);
                }*/
            }
        } else {
            octoChilds = octoChilds(stats, null, null);
        }
        for (int i = 0; i < octoChilds.size(); i++) {
            if (round < 2 && !DebugData.debging) {
                TimerManager.notifyCompletion(round, octoChilds.size());
            }
            Node n = octoChilds.get(i);
            n.bigExpand(stats);

        }

    }

    protected void asStLeftToRightExpand(AscendStorer asSt, int roundToDetail) {
        byte groupIdx = (byte) (level % Statix.nbGROUPS());
        byte round = (byte) (level / Statix.nbGROUPS());

        boolean newDygon = false;
        if (new StateComparator().compare(this, DebugData.node) == 0) {
            int debug = 1;
        }
        if (groupIdx == 0) {
            //System.out.println(Arrays.toString(potsState[3]));
            if (round == roundToDetail + 1) {
                if (round == Statix.NB_ANALYZED_ROUNDS || !AscendStorer.deadEndOnNextLevel(this)) {

                    asSt.injectParts(potsState[roundToDetail]);
                }
                return;
            }
            if (!hasTraule()) {//!hasAscendingColumns() qscg
                return;
            }
            if (round == roundToDetail) {
                newDygon = true;
                asSt = AscendStorer.buildWithPots(potsState);
                if (new StateComparator().compare(this, DebugData.node) == 0) {
                    int debug = 1;
                }

            }

        }
        for (byte i = 0; i < Statix.nbCONTS(); i++) {
            if (trRemains[i] == 0) {
                continue;
            }
            if (!groupCanTake(groupIdx, i)) {
                continue;
            }
            forward(i, groupIdx);
            asStLeftToRightExpand(asSt, roundToDetail);
            backward(i, groupIdx);
        }
        if (newDygon) {
            AscendStorer.findAndInsert(asSt);
        }

    }

    public void buildAscendStorageLayers() {
        for (int i = Statix.NB_ANALYZED_ROUNDS - 1; i >= Statix.ALGO_ASCEND_SWITCH; i--) {
            asStLeftToRightExpand(null, i);
        }
    }

    public boolean hasAscendingColumns() {
        for (int i = Statix.nbHOSTS(); i < Statix.nbGROUPS() - 1; i++) {
            if (AscendStorer.columnCompare(potsState, i, i + 1) > 0) {
                return false;
            }
        }
        return true;
    }

    public boolean hasTraule() {
        for (int i = 0; i < Statix.nbHOSTS(); i++) {
            for (int j = i + 1; j < Statix.nbGROUPS(); j++) {
                if (potsState[0][i] == potsState[0][j]) {
                    if (AscendStorer.columnCompare(potsState, i, j) > 0) {
                        return false;
                    } else {
                        break;
                    }
                }
            }
        }
        for (int i = Statix.nbHOSTS(); i < Statix.nbGROUPS() - 1; i++) {
            if (AscendStorer.columnCompare(potsState, i, i + 1) > 0) {
                return false;
            }
        }
        return true;
    }

    private boolean completeValidation() {
        for (byte gr = 0; gr < Statix.nbGROUPS(); gr++) {
            if (!completeValidation(gr)) {
                return false;
            }
        }
        return true;

    }

    //Should only be used at final level 32
    private boolean completeValidation(byte gr) {
        //TODO redo completely qwer
        return true;

    }

    //Should not be needed, just for debugging
    //Returns true if the differences between trRemains and Statix.getCopyOfPot
    //do not match the added continents on actual round.
    public boolean imbalance() {

        int lastLine = potsState.length - 1;
        int[] copyOfTr = Arrays.copyOf(trRemains, Statix.nbCONTS());
        for (int i = 0; i < Statix.nbGROUPS(); i++) {
            if (potsState[lastLine][i] >= 0) {
                copyOfTr[potsState[lastLine][i]] += 1;
            }
        }
        int[] initPot = Statix.getCopyOfPot(lastLine);
        for (int i = 0; i < Statix.nbCONTS(); i++) {
            if (initPot[i] != copyOfTr[i]) {
                return true;
            }
        }
        return false;
    }

}
