/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package init;

import central.Misc;
import central.Statix;
import exception.InitParseException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import tools.GeneralMeths;

/**
 *
 * @author desharnc27
 */
public class Loading {

    static final String COMMENT_PREFIX = "//";

    public static String humanRank(int i) {
        String wrd;
        int humanVal = i + 1;
        switch (humanVal) {
            case 1:
                wrd = "st";
                break;
            case 2:
                wrd = "nd";
                break;
            case 3:
                wrd = "rd";
                break;
            default:
                wrd = "th";
                break;
        }
        return humanVal + wrd;
    }

    public static String hybridValidation(int nbMonoConts, ArrayList<ArrayList<Byte>> hybrids) {

        for (int i = 0; i < hybrids.size(); i++) {
            ArrayList<Byte> hybrid = hybrids.get(i);
            int val = -1;
            if (val >= hybrid.get(0)) {
                return humanRank(i) + " hybrid refers to negative continent id.";
            }
            for (int j = 0; j < hybrid.size() - 1; j++) {
                if (hybrid.get(j) >= hybrid.get(j + 1)) {
                    return humanRank(i) + " hybrid does not have a strictly ascending order.";
                }
            }
            int top = hybrid.get(hybrid.size() - 1);
            if (top >= nbMonoConts) {
                return humanRank(i) + " hybrid contains number " + top + ",  out of range of mono-continents: <" + nbMonoConts;
            }
        }
        return null;

    }

    public static void load(String dataName, String filename) {

        Path path = Paths.get(filename);
        List<String> lines = null;
        try {
            lines = Files.readAllLines(path);
        } catch (IOException ex) {
            System.out.println("Fatal error: initial data file not found: " + filename);
            System.exit(2);
        }
        int[][] potsByCont = null;
        Team[][] teams = null;
        int nbGroups = -1;
        int nbRounds = -1;
        int nbMonoConts = -1;
        int nbConts = -1;
        int[] minima = null;
        int[] maxima = null;

        final String ngrStr = "Number of groups";
        final String nrdStr = "Number of pots";
        final String nscStr = "Number of mono-continents";
        final String nhStr = "Number of hosts";
        final String hcStr = "Hybrid continents";
        final String minStr = "Mono-continents minima";
        final String maxStr = "Mono-continents maxima";

        int teamIdx = 0;

        ArrayList<ArrayList<Byte>> hybrids = new ArrayList<>();
        int nbHosts = -1;
        boolean earlyStepsValidated = false;
        boolean middleStepsValidated = false;
        String exceptionMessage = null;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            if (line.startsWith(COMMENT_PREFIX)) {
                continue;
            }
            if (line.length() < 3) {
                continue;
            }
            String[] args = line.split(",");
            try {
                if (!earlyStepsValidated) {
                    switch (args[0]) {
                        case nrdStr:
                            nbRounds = Integer.parseInt(args[1]);
                            continue;
                        case nhStr:
                            nbHosts = Integer.parseInt(args[1]);
                            continue;
                        case ngrStr:
                            nbGroups = Byte.parseByte(args[1]);
                            continue;
                        case nscStr:
                            nbMonoConts = Byte.parseByte(args[1]);
                            continue;
                        case hcStr:
                            for (int j = 1; j < args.length; j++) {
                                ArrayList<Byte> monoConts = new ArrayList<>();
                                String[] monoContsStr = args[j].split("-");
                                for (String monoContStr : monoContsStr) {
                                    monoConts.add(Byte.parseByte(monoContStr));
                                }
                                hybrids.add(monoConts);
                            }
                            continue;

                    }

                    //At that point, next commands can only define a football team
                    //so some early validations must be satisfied
                    if (nbGroups < 0) {
                        throw InitParseException.makeMiss(filename, line, ngrStr);
                    }
                    if (nbGroups > 8) {
                        String tempMessage = "No implementation yet for more than 8 groups";
                        throw InitParseException.makeVague(filename, ngrStr, tempMessage);
                    }
                    if (nbRounds < 0) {
                        throw InitParseException.makeMiss(filename, line, nrdStr);
                    }
                    if (nbRounds != 4) {
                        String tempMessage = "No implementation yet for a number of pots other than 4";
                        throw InitParseException.makeVague(filename, nrdStr, tempMessage);
                    }
                    if (nbMonoConts < 0) {
                        throw InitParseException.makeMiss(filename, line, nscStr);
                    }
                    if (nbHosts < 0) {
                        throw InitParseException.makeMiss(filename, line, nhStr);
                    }
                    if (nbGroups < nbHosts) {
                        throw InitParseException.makeVague(filename, nhStr, "There must not be more hosts than groups ");
                    }
                    String tempMessage = hybridValidation(nbMonoConts, hybrids);

                    if (tempMessage != null) {
                        throw InitParseException.makeVague(filename, hcStr, tempMessage);
                    }
                    earlyStepsValidated = true;
                    nbConts = nbMonoConts + hybrids.size();
                    potsByCont = new int[nbRounds][nbConts];
                    teams = new Team[nbRounds][nbGroups];
                }

                //middle steps
                if (!middleStepsValidated) {
                    //Middle steps parsing
                    switch (args[0]) {
                        case minStr, maxStr:
                            if (args.length != nbMonoConts + 1) {
                                String message = args[0] + " should be followed be as much numbers as there are mono-continents.";
                                message += "\n but " + (args.length - 1) + " were found while " + nbMonoConts + " were required";
                                throw InitParseException.make(filename, line, message);
                            }
                            int[] tempArray = new int[nbMonoConts];
                            for (int j = 0; j < nbMonoConts; j++) {
                                tempArray[j] = Integer.parseInt(args[j + 1]);
                            }
                            if (args[0].equals(minStr)) {
                                minima = tempArray;
                            } else {
                                maxima = tempArray;
                            }
                            continue;
                    }
                    //Validate middle steps
                    if (minima == null) {
                        throw InitParseException.makeMiss(filename, line, minStr);
                    }
                    if (maxima == null) {
                        throw InitParseException.makeMiss(filename, line, maxStr);
                    }
                    for (int j = 0; j < nbMonoConts; j++) {
                        if (minima[j] > maxima[j]) {
                            String message = "minimum is larger than maximum for mono-continent " + j;
                            throw InitParseException.makeVague(filename, minStr, message);
                        }
                    }
                    //Apply middle steps
                    middleStepsValidated = true;

                    Statix.setContinentBounds(minima, maxima);
                }

                //Late steps. At that point, remainings command can only define football teams
                if (teamIdx == nbGroups * nbRounds) {
                    String tempMessage = "Too much teams defined. " + "It cannot be over ";
                    tempMessage += nbRounds + " x " + nbGroups + " = " + (nbGroups * nbRounds);
                    tempMessage += ", according to the paramters you defined before.";
                    throw InitParseException.make(filename, line, tempMessage);
                }

                String teamName = args[0];
                byte teamCont = Byte.parseByte(args[1]);
                byte teamPot = (byte) (teamIdx / nbGroups);
                byte teamTempIdx = (byte) (teamIdx % nbGroups);
                if (teamCont < 0 || teamCont >= nbConts) {
                    String tempMessage = teamName + " 's continent id should be inclusively between 0 and " + (nbConts - 1);
                    tempMessage += ", according to the paramters you defined before.";
                    throw InitParseException.make(filename, line, tempMessage);
                }
                teams[teamPot][teamTempIdx] = new Team(teamName, teamCont, teamPot);
                potsByCont[teamPot][teamCont]++;
                teamIdx++;

            } catch (NumberFormatException e) {
                exceptionMessage = InitParseException.make(filename, line, "unexpected non-integer parameter").getMessage();
            } catch (ArrayIndexOutOfBoundsException e) {
                exceptionMessage = InitParseException.make(filename, line, "Not enough parameters").getMessage();
            } catch (InitParseException ex) {
                exceptionMessage = ex.getMessage();
            }
            if (exceptionMessage != null) {
                break;
            }

        }
        if (exceptionMessage == null && teamIdx != nbGroups * nbRounds) {
            String tempMessage = "Not enough teams defined. " + "It should be exactly ";
            tempMessage += nbRounds + " x " + nbGroups + " = " + (nbGroups * nbRounds);
            tempMessage += ", according to the paramters you defined before.";
            exceptionMessage = InitParseException.make(filename, "[End of file]", tempMessage).getMessage();
        }
        Statix.setTeams(teams);
        if (exceptionMessage == null) {
            exceptionMessage = Team.validateTeamsNoAmbiguousPrefixes(teams);

        }

        if (exceptionMessage != null) {
            System.out.println(exceptionMessage);
            System.exit(0);
        }

        ArrayList<Byte> hostList = new ArrayList<>();
        for (int i = 0; i < nbHosts; i++) {
            hostList.add(teams[0][i].cont);
        }

        Statix.setStatix(dataName, GeneralMeths.getCopy(potsByCont), nbGroups, nbMonoConts, hostList, hybrids);

        System.out.println("Initial data parsing was succesful!");
        System.out.println(Statix.allTeamsToString(true));

    }

    /*public static void main(String[] args) {
        load(MegaMain.paramFile);
    }*/
}
