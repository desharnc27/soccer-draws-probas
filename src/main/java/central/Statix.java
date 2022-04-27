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

    static byte NB_MONOCONTS = 5;
    static byte NB_CONTS = 6;
    static byte NB_GROUPS = 8;
    static ArrayList<Byte> initConstDraws;
    static ArrayList<ArrayList<Byte>> hybrids;

    static Team[][] teams;

    static final StateComparator scmp = new StateComparator();

    static int[][] pots;

    static int[] minPerGroup;
    static int[] maxPerGroup;

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
        long smallFact = Misc.fact(NB_GROUPS - Statix.initConstDraws.size());
        return bigFact * bigFact * bigFact * smallFact;
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

    public static int nbGroups() {
        return Statix.NB_GROUPS;
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
                if (teamQuatuorArr[i] < Statix.initConstDraws.size()) {
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

    public static void setContinentBounds(int[] minima, int[] maxima) {
        minPerGroup = minima;
        maxPerGroup = maxima;
    }

    static int getMonoContMax(byte cont) {
        return Statix.maxPerGroup[cont];
    }

    static int getMonoContMin(byte cont) {
        return Statix.minPerGroup[cont];
    }

}
