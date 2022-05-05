/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package central;

import exception.ProbaParamEx;
import init.Team;
import java.util.Arrays;
import java.util.ArrayList;
import tools.GeneralMeths;

/**
 *
 * @author desharnc27
 */
public class Statix {

    public static final StateComparator scmp = new StateComparator();

    private static byte NB_MONOCONTS = 5;
    private static byte NB_CONTS = 6;
    private static byte NB_GROUPS = 8;
    private static ArrayList<Byte> initConstDraws;
    private static ArrayList<ArrayList<Byte>> hybrids;

    private static Team[][] teams;

    private static int[][] pots;

    private static int[] minPerGroup;
    private static int[] maxPerGroup;

    public static final int NB_ANALYZED_ROUNDS = 4;
    public static final int ALGO_ASCEND_SWITCH = 1;//Sould always be 1 unless Node.iscompletable() code gets positively changed.

    public static void setStatix(int[][] potsByCont, int nbGroups, int nbMonoConts,
            ArrayList<Byte> initConstDraws, ArrayList<ArrayList<Byte>> hybrids) {
        pots = GeneralMeths.getCopy(potsByCont);
        NB_GROUPS = (byte) nbGroups;
        NB_CONTS = (byte) pots[0].length;
        NB_MONOCONTS = (byte) nbMonoConts;
        Statix.initConstDraws = initConstDraws;
        Statix.hybrids = hybrids;
    }

    public static int[] getCopyOfPot(int pot) {
        return Arrays.copyOf(pots[pot], pots[pot].length);
    }

    public static int getContCount(byte pot, byte cont) {
        return pots[pot][cont];
    }

    public static byte[] getMonoconts(byte cont) {
        if (cont < NB_MONOCONTS) {
            return new byte[]{cont};
        }
        ArrayList<Byte> monoList = hybrids.get(cont - NB_MONOCONTS);
        byte[] res = new byte[monoList.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = monoList.get(i);
        }
        return res;
    }

    public static long commonDenominator() {
        long bigFact = Misc.fact(NB_GROUPS);
        long smallFact = Misc.fact(NB_GROUPS - nbHOSTS());
        return bigFact * bigFact * bigFact * smallFact;
    }

    public static long commonDenominator(int level) {
        if (level <= Statix.nbHOSTS()) {
            return 1;
        }
        int fullRounds = level / NB_GROUPS;
        int inTrLevel = level % NB_GROUPS;
        long bigFact = Misc.fact(NB_GROUPS);
        long smallFact0 = Misc.fact(NB_GROUPS - inTrLevel);
        long smallFact1 = Misc.fact(NB_GROUPS - nbHOSTS());
        long denom = smallFact1 * Misc.power(bigFact, fullRounds) / (smallFact0);
        return denom;
    }

    public static double probability(int level, long nbPoss) {
        return nbPoss / (double) commonDenominator(level);
    }

    public static void setContinentBounds(int[] minima, int[] maxima) {
        minPerGroup = minima;
        maxPerGroup = maxima;
    }

    public static int getMonoContMax(byte cont) {
        return Statix.maxPerGroup[cont];
    }

    public static int getMonoContMin(byte cont) {
        return Statix.minPerGroup[cont];
    }

    public static byte getHostCont(int hostIdx) {
        return Statix.initConstDraws.get(hostIdx);
    }

    public static int nbHOSTS() {
        return Statix.initConstDraws.size();
    }

    public static byte nbMONOCONTS() {
        return Statix.NB_MONOCONTS;
    }

    public static byte nbCONTS() {
        return Statix.NB_CONTS;
    }

    public static byte nbGROUPS() {
        return Statix.NB_GROUPS;
    }

    //Methods for teams
    public static int indexValidateByPrefix(int round, String input) throws ProbaParamEx {
        input = Misc.decapitalize(input);
        int res = -2;//Not found value
        for (int i = 0; i < teams[0].length; i++) {
            String teamName = Misc.decapitalize(teams[round][i].name());
            if (teamName.startsWith(input)) {
                if (res < 0) {
                    res = i;
                } else {
                    throw ProbaParamEx.makeAmbiguousArg(input, teams[round][res].name(), teams[round][i].name()); // ;//ambiguous value
                }
            }
        }
        if (res == -2) {
            throw ProbaParamEx.makeBadArg(input);
        }
        return res;
    }

    public static String allTeamsToString(boolean detailed) {
        String[] bigArr = new String[teams.length];
        for (int i = 0; i < bigArr.length; i++) {
            Team[] in = teams[i];
            String[] inStr = new String[in.length];
            for (int j = 0; j < in.length; j++) {
                inStr[j] = detailed ? in[j].toString() : in[j].name();
            }
            bigArr[i] = String.join(",", inStr);
        }
        return String.join("\n", bigArr);
    }

    public static Team getTeam(int rd, int idx) {
        return teams[rd][idx];
    }

    public static void setTeams(Team[][] arr) {
        teams = arr;
    }

    public static double getProba(int gr, int[] teamQuatuorArr) {
        //byte [] conts = new byte[teamQuatuor.length];
        Team[] teamQuatuor = new Team[4];
        for (int i = 0; i < 4; i++) {
            teamQuatuor[i] = Statix.getTeam(i, teamQuatuorArr[i]);
        }
        long denomAcc = 1;
        for (int i = 0; i < 4; i++) {
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
        }
        byte[] conts = new byte[4];
        for (int i = 0; i < 4; i++) {
            conts[i] = teamQuatuor[i].cont();
        }
        return Stats.getSpecificProbLong(gr, conts) / denomAcc + 0.0;
    }

}
