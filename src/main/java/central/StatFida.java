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
public class StatFida {

    protected long[][][][][] detailedRoundStats;
    protected long globalCount;

    //By default, only host teams are forced in some group at start.
    protected byte[][] forcedTeamIdxByGroup;
    protected int nbForced;

    public StatFida() {
        initDetailed();
        initForced();
        clearForced();
        defaultForced();
    }

    private void initForced() {
        forcedTeamIdxByGroup = new byte[4][Statix.nbGROUPS()];
    }

    private void defaultForced() {
        initForced();
        for (byte i = 0; i < Statix.nbHOSTS(); i++) {
            forcedTeamIdxByGroup[0][i] = i;
        }
        nbForced = Statix.nbHOSTS();
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
        nbForced = 0;
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
            nbForced++;
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

    public void storeInDead(String filename) {
        int chainLen = (int) (2 + Statix.nbGROUPS() * Misc.power(Statix.nbCONTS(), 4) * Long.BYTES);
        byte[] byteChain = new byte[chainLen];
        int idx = 0;
        byteChain[idx++] = Statix.nbGROUPS();
        byteChain[idx++] = Statix.nbCONTS();
        for (int gr = 0; gr < Statix.nbGROUPS(); gr++) {
            for (int p0 = 0; p0 < Statix.nbCONTS(); p0++) {
                for (int p1 = 0; p1 < Statix.nbCONTS(); p1++) {
                    for (int p2 = 0; p2 < Statix.nbCONTS(); p2++) {
                        for (int p3 = 0; p3 < Statix.nbCONTS(); p3++) {
                            byte[] bytes = Misc.longToBytes(detailedRoundStats[gr][p0][p1][p2][p3]);
                            for (byte by : bytes) {
                                byteChain[idx++] = by;
                            }
                        }
                    }
                }
            }
        }

        try {
            Files.write(Paths.get(filename), byteChain);
            System.out.println("Stats were written in " + filename);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.out.println("No file was written");
        }

    }

    public void StoreDankInfile(String filename) {
        boolean indiv = this instanceof StatByTeams;
        ArrayList<Byte> byteChain = new ArrayList<>();
        //byteChain.add((byte)Statix.nbGROUPS());
        //byteChain.add(indiv? -1: Statix.nbCONTS());
        byte xot = indiv ? Statix.nbGROUPS() : Statix.nbCONTS();
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
            FileOutputStream fout = new FileOutputStream(filename + ".statf");
            while (iter.hasNext()) {
                fout.write(iter.next());
            }
            fout.close();
            System.out.println("Stats were written in " + filename);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.out.println("No file was written");
        }
    }

    public void loadFromDead(String filename) throws InitParseException, IOException {
        this.globalCount = 0;
        byte[] byteChain = Files.readAllBytes(Paths.get(filename));

        int idx = 0;
        int tempNbGroups = byteChain[idx++];
        int tempNbConts = byteChain[idx++];

        if (Statix.nbGROUPS() != tempNbGroups || Statix.nbCONTS() != tempNbConts) {
            String mismatchParameter = Statix.nbGROUPS() == tempNbGroups
                    ? "Number of mono-continents or Number of hybrids" : "number of groups";
            throw InitParseException.makeDataMismatch(mismatchParameter);
        }

        for (int gr = 0; gr < Statix.nbGROUPS(); gr++) {
            for (int p0 = 0; p0 < Statix.nbCONTS(); p0++) {
                for (int p1 = 0; p1 < Statix.nbCONTS(); p1++) {
                    for (int p2 = 0; p2 < Statix.nbCONTS(); p2++) {
                        for (int p3 = 0; p3 < Statix.nbCONTS(); p3++) {
                            byte[] bytes = Arrays.copyOfRange(byteChain, idx, idx + Long.BYTES);
                            long val = Misc.bytesToLong(bytes);
                            detailedRoundStats[gr][p0][p1][p2][p3] = val;
                            if (gr == 0) {
                                globalCount += val;
                            }
                            idx += Long.BYTES;
                        }
                    }
                }
            }
        }

    }

    public void loadDankFromFile(String filename) throws InitParseException, IOException {
        this.globalCount = 0;
        byte[] byteChain = Files.readAllBytes(Paths.get(filename));

        int idx = 0;
        //int tempNbGroups = byteChain[idx++];
        //int tempNbConts = byteChain[idx++];

        /*if (Statix.nbGROUPS() != tempNbGroups || Statix.nbCONTS() != tempNbConts) {
            String mismatchParameter = Statix.nbGROUPS() == tempNbGroups
                    ? "Number of mono-continents or Number of hybrids" : "number of groups";
            throw InitParseException.makeDataMismatch(mismatchParameter);
        }*/
        this.initForced();
        for (int i = 0; i < 4 * Statix.nbGROUPS(); i++) {
            int round = i / 4;
            int gr = i % 4;
            byte value = byteChain[idx++];
            if (value >= 0) {
                nbForced++;
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
            long val = Misc.bytesToLong(longArr);
            detailedRoundStats[gr][p0][p1][p2][p3] = val;
            globalCount++;

        }

        /*for (int gr = 0; gr < Statix.nbGROUPS(); gr++) {
            for (int p0 = 0; p0 < Statix.nbCONTS(); p0++) {
                for (int p1 = 0; p1 < Statix.nbCONTS(); p1++) {
                    for (int p2 = 0; p2 < Statix.nbCONTS(); p2++) {
                        for (int p3 = 0; p3 < Statix.nbCONTS(); p3++) {
                            byte[] bytes = Arrays.copyOfRange(byteChain, idx, idx + Long.BYTES);
                            long val = Misc.bytesToLong(bytes);
                            detailedRoundStats[gr][p0][p1][p2][p3] = val;
                            if (gr == 0) {
                                globalCount += val;
                            }
                            idx += Long.BYTES;
                        }
                    }
                }
            }
        }*/
    }

    public int contToIndivNbPoss(int round, int gr, int teamIdx) {
        Team team = Statix.getTeam(round, teamIdx);
        int nbFullyForcedRounds = nbForced / Statix.nbGROUPS();
        boolean leanF = nbForced % Statix.nbGROUPS() == 0;
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

    public int contToIndivNbPoss(int gr, int[] teamQuatuorArr) {
        int res = 1;
        for (int round = 0; round < 4; round++) {
            res *= contToIndivNbPoss(round, gr, teamQuatuorArr[round]);
            if (res < 0) {
                return -1;
            }
        }

        return res;
    }

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
        return nbForced;
    }

    public int getForcedTIdx(int round, int group) {
        return this.forcedTeamIdxByGroup[round][group];
    }

    public void debugVerifyWholeSum() {
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

    //Static
    //private static final StatFida exactStats = new StatFida();
    //private static final StatFida simulStats = new StatFida();

    /*public static StatFida exactStats() {
        return exactStats;
    }

    public static StatFida simulStats() {
        return simulStats;
    }*/
 /*private static HashMap<String,StatFida> catalog = new HashMap<String,StatFida>();
    public static StatFida createAndGet(String statsName){
        StatFida res = catalog.get(statsName);
        if (res==null){
            if (statsName.equals(MegaMain.SIMUL_HARD))
                res = new StatByTeams();
            else
                res = new StatFida();
            catalog.putIfAbsent(statsName, res);
        }
        return res;
    }*/
}
