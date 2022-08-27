/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package central;

import exception.InitParseException;
import init.MegaMain;
import init.Team;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author desharnc27
 */
public class StatByConts {

    // detailedRoundStats[g][i0][i1][i2][i3] / globalCount
    // is the probability to have i0,i1,i2,i3 in group g
    protected long[][][][][] detailedRoundStats;
    protected long globalCount;

    //A forced team is a team for which the group is already decided at start.
    //The number of forced teams is at least the number of hosts, but can be more
    //If you build tour statistics from a simulation already partially completed.
    protected int nbForcedTeams;

    //By default, only host teams are forced in some group at start. -1 is the void value
    //forcedTeamIdxByGroup[r][i]= -1 -> means that the team at spot r in group i is still open
    //forcedTeamIdxByGroup[r][i]= someNonNegativeInt -> means that the team at spot r in group i 
    //is held by the team number someNonNegativeInt in pot r
    protected byte[][] forcedTeamIdxByGroup;

    public StatByConts() {
        initDetailed();
        initForced();
        defaultForced();
    }

    private void initForced() {
        forcedTeamIdxByGroup = new byte[4][Statix.nbGROUPS()];
        clearForced();
    }

    private void defaultForced() {
        for (byte i = 0; i < Statix.nbHOSTS(); i++) {
            forcedTeamIdxByGroup[0][i] = i;
        }
        nbForcedTeams = Statix.nbHOSTS();
    }

    private void initDetailed() {
        int ng = Statix.nbGROUPS();
        int nc = Statix.nbCONTS();
        detailedRoundStats = new long[ng][nc][nc][nc][nc];
        globalCount = 0;

    }

    public final void clearForced() {
        //if (forcedTeamIdxByGroup == null)
        //    initForced();
        nbForcedTeams = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < Statix.nbGROUPS(); j++) {
                forcedTeamIdxByGroup[i][j] = -1;
            }
        }

    }

    public void setForcedTeams(List<Byte> teamIndexes, List<Byte> groups) {
        clearForced();
        for (int i = 0; i < teamIndexes.size(); i++) {
            int round = i / Statix.nbGROUPS();
            forcedTeamIdxByGroup[round][groups.get(i)] = teamIndexes.get(i);
            nbForcedTeams++;
        }
    }

    public long getGlobalCount() {
        return globalCount;
    }

    public long getSpecificProbLong(int gr, byte[] conts) {
        return detailedRoundStats[gr][conts[0]][conts[1]][conts[2]][conts[3]];
    }

    public void feed(byte[][] potsState, long num) {
        for (int gr = 0; gr < Statix.nbGROUPS(); gr++) {
            int t0 = potsState[0][gr];
            int t1 = potsState[1][gr];
            int t2 = potsState[2][gr];
            int t3 = potsState[3][gr];
            detailedRoundStats[gr][t0][t1][t2][t3] += num;
        }
        globalCount += num;
    }

    //Must be overriden by any subclass and must be called only by an object of subclass
    public void feed(ArrayList<Byte> teamBdOrder, ArrayList<Byte> groupOrder) {
        System.out.println("Warning: can't feed with lists if this is not an extended class.");
    }

    public void StoreStatsInfile(String filename) {
        boolean indiv = this instanceof StatByTeams;
        ArrayList<Byte> byteChain = new ArrayList<>();
        //byteChain.add((byte)Statix.nbGROUPS());
        //byteChain.add(indiv? -1: Statix.nbCONTS());
        byte xot = indiv ? Statix.nbGROUPS() : Statix.nbCONTS();
        System.out.println("indiv: " + indiv);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < Statix.nbGROUPS(); j++) {
                byteChain.add(this.forcedTeamIdxByGroup[i][j]);
            }
        }
        for (byte gr = 0; gr < Statix.nbGROUPS(); gr++) {
            for (byte p0 = 0; p0 < xot; p0++) {
                for (byte p1 = 0; p1 < xot; p1++) {
                    for (byte p2 = 0; p2 < xot; p2++) {
                        for (byte p3 = 0; p3 < xot; p3++) {
                            long val = detailedRoundStats[gr][p0][p1][p2][p3];
                            if (val == 0) {
                                continue;
                            }
                            //System.out.printf("%d-%d-%d-%d-%d: ",gr,p0,p1,p2,p3);
                            //System.out.println(detailedRoundStats[gr][p0][p1][p2][p3]);
                            byteChain.add(gr);
                            byteChain.add(p0);
                            byteChain.add(p1);
                            byteChain.add(p2);
                            byteChain.add(p3);

                            byte[] bytes = Misc.longToBytes(detailedRoundStats[gr][p0][p1][p2][p3]);
                            for (byte by : bytes) {
                                byteChain.add(by);
                            }
                        }
                    }
                }
            }
        }
        Iterator<Byte> iter = byteChain.iterator();
        try {
            FileOutputStream fout = new FileOutputStream(filename);
            while (iter.hasNext()) {
                fout.write(iter.next());
            }
            fout.close();
            System.out.println("Stats were written in " + filename);
            System.out.println("byteChain.size(): " + byteChain.size());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.out.println(filename + " wasn't written");
        }
        //int a = 1 / 0;//TODO delete
    }

    public void loadStatsFromFile(String filename) throws InitParseException, IOException {
        this.globalCount = 0;
        byte[] byteChain = Files.readAllBytes(Paths.get(filename));
        int idx = 0;
        this.initForced();
        for (int i = 0; i < 4 * Statix.nbGROUPS(); i++) {
            int round = i / Statix.nbGROUPS();
            int gr = i % Statix.nbGROUPS();
            byte value = byteChain[idx++];
            if (value >= 0) {
                nbForcedTeams++;
            }
            this.forcedTeamIdxByGroup[round][gr] = value;
        }
        this.initDetailed();
        while (idx < byteChain.length) {
            byte gr = byteChain[idx++];
            byte p0 = byteChain[idx++];
            byte p1 = byteChain[idx++];
            byte p2 = byteChain[idx++];
            byte p3 = byteChain[idx++];
            byte[] longArr = Arrays.copyOfRange(byteChain, idx, idx + Long.BYTES);
            idx += Long.BYTES;
            long val = Misc.bytesToLong(longArr);
            detailedRoundStats[gr][p0][p1][p2][p3] = val;
            globalCount += val;
        }
        globalCount /= Statix.nbGROUPS();

    }

    /**
     * Used to convert TODO
     *
     * @param round
     * @param gr
     * @param teamIdx
     * @return
     */
    protected int contToIndivNbPoss(int round, int gr, int teamIdx) {
        Team team = Statix.getTeam(round, teamIdx);
        int nbFullyForcedRounds = nbForcedTeams / Statix.nbGROUPS();
        boolean leanF = nbForcedTeams % Statix.nbGROUPS() == 0;
        if (round > nbFullyForcedRounds || (round == nbFullyForcedRounds && leanF)) {
            return Statix.getContCount((byte) round, team.cont());
        } else if (round < nbFullyForcedRounds) {

            if (forcedTeamIdxByGroup[round][gr] != teamIdx) {
                return -1;
            }
            return 1;

        } else {
            boolean forced = false;
            if (forcedTeamIdxByGroup[round][gr] >= 0 && forcedTeamIdxByGroup[round][gr] != teamIdx) {
                return -1;
            }
            for (int k = 0; k < Statix.nbGROUPS(); k++) {
                if (forcedTeamIdxByGroup[round][k] == teamIdx) {
                    if (k != gr) {
                        return -1;
                    }
                    forced = true;
                    break;
                }
            }
            if (forced) {
                return 1;
            }

            int divisor = Statix.getContCount((byte) round, team.cont());
            for (int j = 0; j < Statix.nbGROUPS(); j++) {
                if (forcedTeamIdxByGroup[round][j] == -1) {
                    continue;
                }
                if (Statix.getTeam(round, forcedTeamIdxByGroup[round][j]).cont() == team.cont()) {
                    divisor--;
                }

            }
            return divisor;

        }
    }

    protected int contToIndivNbPoss(int gr, int[] teamQuatuorArr) {
        int res = 1;
        for (int round = 0; round < 4; round++) {
            res *= contToIndivNbPoss(round, gr, teamQuatuorArr[round]);
            if (res < 0) {
                return -1;
            }
        }

        return res;
    }

    /**
     * Complicated...TODO, the number of ways to replace teamQuatuorArr by a
     * quatuor of teams orderly matching the continents of the teams in
     * teamQuatuorArr, where forced teams cannot be used as replacement. This
     * function's purpose is to make the bridge (ratio) between
     * continental-encoded data and individual-encoded data
     *
     * @param gr
     * @param teamQuatuorArr
     * @return
     */
    public long getRatha(int gr, int[] teamQuatuorArr) {

        //StatFida statsToUse = useExact? StatFida.exactStats() : StatFida.simulStats();
        Team[] teamQuatuor = new Team[4];
        for (int i = 0; i < 4; i++) {
            teamQuatuor[i] = Statix.getTeam(i, teamQuatuorArr[i]);
        }
        /*for (int i = 0; i < 4; i++) {
            if (i != 0) {
                denomAcc *= Statix.getContCount((byte) i, teamQuatuor[i].cont());
            } else {
                if (teamQuatuorArr[i] < nbHOSTS()) {
                    //Host case
                    if (teamQuatuorArr[i] != gr) {
                        return 0;
                    }
                    // else certaincy so no change
                } else {
                    int divisor = Statix.getContCount((byte) i, teamQuatuor[i].cont());
                    for (int j = 0; j < initConstDraws.size(); j++) {
                        if (initConstDraws.get(j) == teamQuatuor[i].cont()) {
                            divisor--;
                        }

                    }
                    denomAcc *= divisor;
                }

            }
        }*/
        //TODO change
        int contToIndivRatio = contToIndivNbPoss(gr, teamQuatuorArr);

        if (contToIndivRatio == -1) //-1 means infinity here
        {
            return 0;
        }

        byte[] conts = new byte[4];
        for (int i = 0; i < 4; i++) {
            conts[i] = teamQuatuor[i].cont();
        }
        long contMatchLong = getSpecificProbLong(gr, conts);
        //System.out.println("contMatchLong: "+contMatchLong);
        //System.out.println("contToIndivRatio: "+contToIndivRatio);
        //System.out.println("globalCount: "+globalCount);

        return contMatchLong / (contToIndivRatio + 0);
    }

    public void printForced() {
        System.out.println("printForced(): ");
        Misc.print2D(forcedTeamIdxByGroup);
    }

    public int getNbForced() {
        return nbForcedTeams;
    }

    public int getForcedTeamIdx(int round, int group) {
        return this.forcedTeamIdxByGroup[round][group];
    }

    /**
     * Checks if that all probabilities sum to 1.
     */
    public void verifySums() {
        long[] sums = new long[Statix.nbCONTS()];
        boolean mistake = false;
        for (int p3 = 0; p3 < Statix.nbCONTS(); p3++) {

            for (int gr = 0; gr < Statix.nbGROUPS(); gr++) {
                for (int p0 = 0; p0 < Statix.nbCONTS(); p0++) {
                    for (int p1 = 0; p1 < Statix.nbCONTS(); p1++) {
                        for (int p2 = 0; p2 < Statix.nbCONTS(); p2++) {
                            sums[p3] += detailedRoundStats[gr][p0][p1][p2][p3];
                        }
                    }
                }
            }
            if (sums[p3] != globalCount * Statix.getContCount((byte) 3, (byte) p3)) {
                mistake = true;
                System.out.printf("debug failure, %d: %d!=%d\n", p3, sums[p3], globalCount);
            }
        }
        if (!mistake) {
            System.out.println("no debug failure in counting");
        }
    }
}
