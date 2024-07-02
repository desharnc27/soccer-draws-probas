/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package init;

import algocore.AscendStorer;
import algocore.Node;
import central.CalculusMain;
import central.Misc;
import central.StatByTeams;
import central.StatByConts;
import central.Statix;
import exception.ProbaParamEx;
import java.util.ArrayList;
import scanlol.Flower;
import scanlol.PresetScanner;
import tools.GeneralMeths;

/**
 *
 * @author desharnc27
 */
public class Simulating {

    public static void simulateOne(byte verbose) {
        Node node = new Node();
        int[][] drawOrder = getRandomDrawOrder();
        //byte [][] groupOrder = new byte[4][Statix.nbGROUPS()];
        int[][] finalConfig = new int[4][Statix.nbGROUPS()];
        for (int i = 0; i < 4; i++) {
            AscendStorer asSt = null;
            byte[][] asp;
            byte[] perm = null;
            if (i > 0) {
                asp = AscendStorer.getAscendStorerPerm(node);
                asSt = AscendStorer.getAscendStorer(asp);
                perm = asp[asp.length - 1];
            }
            for (int j = 0; j < Statix.nbGROUPS(); j++) {
                Team teamDrafted = Statix.getTeam(i, drawOrder[i][j]);
                byte nextCont = (byte) teamDrafted.cont();
                byte nextGroup = (i == 0) ? node.leftMostPlace(nextCont) : node.leftMostPlace(nextCont, asSt, perm);
                node = new Node(node, nextCont, nextGroup, 1);
                finalConfig[i][nextGroup] = drawOrder[i][j];
                //groupOrder[i][j] = nextGroup;
                if (verbose > 1) {
                    char group = (char) ('A' + nextGroup);
                    System.out.println(teamDrafted.name() + " was placed in group " + group);
                }
            }
        }
    }

    public static void interactiveLoop(Flower scanner) {
        StatByConts stats;
        while ((stats = interactiveSim(scanner)) == null) {
            //Nothing to do other than the condition fct itself;
        }
        if (stats != null) {
            if (stats instanceof StatByTeams) {
                System.out.printf("Remark: using %s does not allow saving calculations\n", RealMain.SIMUL_HARD);
            } else {
                String question = "How would you name your simulation? (type a single character to avoid saving)";
                String qrRequest = scanner.questionStr(question, "[\\w]*");
                if (qrRequest.length() > 1) {
                    String filename = RealMain.customFile(Statix.getDataName(), qrRequest);
                    stats.StoreStatsInfile(filename);
                }
            }
        }
        RealMain.probaQuast(scanner, stats);
    }

    /**
     * Interactive sim
     *
     * @param scanner
     * @return true if user wants to do another simulation, false otherwise
     */
    public static StatByConts interactiveSim(Flower scanner) {
        int NB_TEAMS = 4 * Statix.nbGROUPS();
        Node actua = new Node();
        ArrayList<Team> teams = new ArrayList<>();
        ArrayList<Byte> groupOrder = new ArrayList<>();
        ArrayList<Byte> teamBdOrder = new ArrayList<>();
        boolean[][] teamDrafted = new boolean[4][Statix.nbGROUPS()];
        for (int i = 0; i < Statix.nbHOSTS(); i++) {
            Simulating.forward(scanner, actua, i, teamDrafted, teamBdOrder, teams, groupOrder);
        }
        while (true) {
            String[] options = new String[]{"undo*", "current", "next*", "exactFromHere",
                RealMain.SIMUL_AVG + "*", RealMain.SIMUL_HARD + "*"};
            String[] understood = RealMain.crazyChooseOption(scanner, options);
            StatByConts stats;
            switch (understood[0]) {
                case "undo":
                    if (teams.size() <= Statix.nbHOSTS()) {
                        System.out.println("Nothing to undo.");
                        break;
                    }
                    int userNumber;
                    try {
                        userNumber = Integer.parseInt(understood[1]);
                    } catch (NumberFormatException e) {
                        userNumber = -1;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        userNumber = 1;
                    }
                    if (userNumber <= 0) {
                        System.out.println(understood[1] + " is a wrong parameter for " + understood[0]);
                        break;
                    }
                    if (userNumber > teams.size() - Statix.nbHOSTS()) {
                        userNumber = teams.size() - Statix.nbHOSTS();
                    }
                    for (int i = 0; i < userNumber; i++) {
                        Simulating.backward(scanner, actua, teamDrafted, teamBdOrder, teams, groupOrder);
                    }
                    break;

                case "next":
                    if (teams.size() == Statix.nbGROUPS() * 4) {
                        System.out.println("Draft already completed.");
                        break;
                    }
                    String teamPrefix = null;
                    try {
                        userNumber = Integer.parseInt(understood[1]);
                    } catch (NumberFormatException e) {
                        userNumber = 1;
                        teamPrefix = understood[1];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        userNumber = 1;
                    }
                    int teamIdx = -1;
                    if (teamPrefix != null) {
                        try {
                            int round = teams.size() / Statix.nbGROUPS();
                            teamIdx = RealMain.guessTeam(round, teamPrefix);
                            if (teamDrafted[round][teamIdx]) {
                                System.out.println(Statix.getTeam(round, teamIdx) + " is already drafted.");
                                break;
                            }

                        } catch (ProbaParamEx ex) {
                            System.out.println(ex.getMessage());
                            break;
                        }

                    } else if (userNumber <= 0) {
                        System.out.println(understood[1] + " is a wrong parameter for " + understood[0]);
                        break;
                    }
                    if (userNumber > NB_TEAMS - teams.size()) {
                        userNumber = NB_TEAMS - teams.size();
                    }
                    for (int i = 0; i < userNumber; i++) {
                        Simulating.forward(scanner, actua, teamIdx, teamDrafted, teamBdOrder, teams, groupOrder);
                        teamIdx = -1;//reset team index fot next iter
                    }
                    break;
                case "current":
                    Simulating.printCurrentDraft(teams, groupOrder);
                    break;

                case RealMain.SIMUL_AVG:
                    //stats = StatFida.createAndGet(MegaMain.SIMUL_AVG);
                    stats = new StatByConts();
                    int sampleSize;
                    try {
                        sampleSize = Integer.parseInt(understood[1]);
                    } catch (NumberFormatException e) {
                        System.out.println("stize" + "can't be without extra argument (size of sample).");
                        break;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("extra argument must be a number (size of sample).");
                        break;
                    }
                    expandBySample(stats, sampleSize, actua, teamDrafted, teamBdOrder, teams, groupOrder);
                    return stats;
                case RealMain.SIMUL_HARD:
                    //stats = StatFida.createAndGet(MegaMain.SIMUL_HARD);
                    stats = new StatByTeams();
                    try {
                        sampleSize = Integer.parseInt(understood[1]);
                    } catch (NumberFormatException e) {
                        System.out.println("stize" + "can't be without extra argument (size of sample).");
                        break;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("extra argument must be a number (size of sample).");
                        break;
                    }
                    expandBySample(stats, sampleSize, actua, teamDrafted, teamBdOrder, teams, groupOrder);
                    return stats;
                case "exactFromHere":
                    //stats = StatFida.createAndGet(MegaMain.EXACT_FROM_SOME);
                    stats = new StatByConts();
                    stats.setForcedTeams(teamBdOrder, groupOrder);
                    CalculusMain.buildExactStats(stats);
                    return stats;
                //case "terminate":
                //    return false;
            }
        }

    }

    private static void expandBySample(StatByConts stats, int sampleSize, Node actua, boolean[][] teamDrafted,
            ArrayList<Byte> teamBdOrder, ArrayList<Team> teams, ArrayList<Byte> groupOrder) {

        if (stats == null) {
            throw new UnsupportedOperationException();
            //Nothing here cuz it'll never happen, just to remove warning
        }

        for (int i = 0; i < sampleSize; i++) {
            PresetScanner pss = new PresetScanner();//just to avoid printings, useless otherwise

            int depth = 4 * Statix.nbGROUPS() - teams.size();
            for (int j = 0; j < depth; j++) {
                Simulating.forward(pss, actua, -1, teamDrafted, teamBdOrder, teams, groupOrder);
            }
            Simulating.printCurrentDraft(teams, groupOrder);
            System.out.println("");
            Misc.print2D(actua.copyPotsState());

            if (!(stats instanceof StatByTeams)) {
                stats.feed(actua.copyPotsState(), 1);
            } else {
                stats.feed(teamBdOrder, groupOrder);
            }
            for (int j = 0; j < depth; j++) {
                Simulating.backward(pss, actua, teamDrafted, teamBdOrder, teams, groupOrder);
            }
        }
        System.out.println("StatFida.simulStats().getGlobalCount(): " + stats.getGlobalCount());
    }

    private static void printCurrentDraft(ArrayList<Team> teams, ArrayList<Byte> groupOrder) {
        int width = 6;
        String[][] names = new String[4][Statix.nbGROUPS()];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < names[0].length; j++) {
                names[i][j] = "-";
            }
        }
        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(i);
            byte group = groupOrder.get(i);
            int round = i / Statix.nbGROUPS();
            String name = team.name();
            names[round][group] = name;
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < names[0].length; j++) {
                names[i][j] = Misc.pad(names[i][j], width, ' ');
            }
        }
        for (int i = 0; i < 4; i++) {
            System.out.println(String.join(" | ", names[i]));
        }
    }

    private static void backward(Flower scanner, Node node, boolean[][] teamDrafted,
            ArrayList<Byte> teamBdOrder, ArrayList<Team> teams, ArrayList<Byte> groupOrder) {
        byte group = Misc.removeLast(groupOrder);
        Team team = Misc.removeLast(teams);
        char groupLetter = Statix.groupLetter(group);
        byte teamIdx = Misc.removeLast(teamBdOrder);
        node.backward(team.cont(), group);
        int round = node.getRound();
        teamDrafted[round][teamIdx] = false;
        ScanMeths.printlnIfScan(scanner, "Draft of " + team.name() + " in group " + groupLetter + " was cancelled.");

    }

    private static void forward(Flower scanner, Node node, int teamIdx, boolean[][] teamDrafted,
            ArrayList<Byte> teamBdOrder, ArrayList<Team> teams, ArrayList<Byte> groupOrder) {
        int level = node.getLevel();
        int round = node.getRound();

        if (teamIdx < 0) {
            if (level < Statix.nbHOSTS()) {
                teamIdx = level;
            } else {
                //int nbPoss =  Statix.nbGROUPS() - (previous.getLevel() - 4 * round);
                teamIdx = randomTeamIdx(teamDrafted[round]);
            }
        }

        AscendStorer asSt = null;
        byte[][] asp;
        byte[] perm = null;
        if (round > 0) {
            asp = AscendStorer.getAscendStorerPerm(node);
            asSt = AscendStorer.getAscendStorer(asp);
            perm = asp[asp.length - 1];
        }

        Team team = Statix.getTeam(round, teamIdx);
        byte cont = team.cont();
        byte group = node.leftMostPlace(cont, asSt, perm);
        node.forward(cont, group);
        char groupLetter = Statix.groupLetter(group);

        groupOrder.add(group);
        teams.add(team);
        teamBdOrder.add((byte) teamIdx);
        teamDrafted[round][teamIdx] = true;

        ScanMeths.printlnIfScan(scanner, team.name() + " was drafted to group " + groupLetter + ".");
    }

    public static int[][] getRandomDrawOrder() {
        int ngr = Statix.nbGROUPS();
        int nbh = Statix.nbHOSTS();
        int[] firstPermExtra = GeneralMeths.randomPermArray(ngr - nbh);
        int[] firstPerm = new int[ngr];
        for (int i = 0; i < nbh; i++) {
            firstPerm[i] = i;
        }
        for (int i = nbh; i < ngr; i++) {
            firstPerm[i] = firstPermExtra[i - nbh] + nbh;
        }
        int[][] randomDraw = new int[][]{
            firstPerm,
            GeneralMeths.randomPermArray(ngr),
            GeneralMeths.randomPermArray(ngr),
            GeneralMeths.randomPermArray(ngr)
        };
        return randomDraw;

    }

    public static int randomTeamIdx(boolean[] taken) {

        int nbChoices = 0;
        for (boolean bool : taken) {
            if (!bool) {
                nbChoices++;
            }
        }
        int remaining = (int) (Math.random() * nbChoices);
        for (int idx = 0; idx < taken.length; idx++) {
            if (!taken[idx]) {
                if (remaining == 0) {
                    return idx;
                }
                remaining--;
            }
        }
        //Should not be reached
        return -1;
    }
}
