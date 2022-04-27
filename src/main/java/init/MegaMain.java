/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package init;

import central.CalculusMain;
import central.DebugData;
import central.Misc;
import central.Statix;
import central.Stats;
import exception.InitParseException;
import exception.ProbaParamEx;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import tools.GeneralMeths;
import tools.RootFinder;

/**
 *
 * @author desharnc27
 */
public class MegaMain {

    public static final String ROOT_STR = RootFinder.getRootPath();
    public static boolean useExistentCalculations = false;

    private static String teamsFileName = "initConfig.txt";
    private static String statsFileStr = "stats";
    private static String dataFolderName = "data";
    private static String defaultScenarioName = "default";
    private static String initParametersFileName = "parameters.txt";
    private static String calculFileName = "algoData";

    public static String dataPath() {
        return ROOT_STR + File.separator + dataFolderName + File.separator;
    }

    public static String dataPath(String scenario) {
        return dataPath() + scenario + File.separator;
    }

    public static String defaultDataPath() {
        return dataPath(defaultScenarioName);
    }

    public static String paramFile(String scenario) {
        return dataPath() + scenario + File.separator + initParametersFileName;
    }

    public static String calculFile(String scenario) {
        return dataPath() + scenario + File.separator + calculFileName;
    }

    public static String paramFile() {
        return paramFile(defaultScenarioName);
    }

    public static String calculFile() {
        return calculFile(defaultScenarioName);
    }

    public static String symbNo = "-";
    public static String guilNo = "\"" + symbNo + "\"";
    public static String symbWild = "*";
    public static String guilWild = "\"" + symbWild + "\"";
    public static String symbOr = "/";
    public static String guilOr = "\"" + symbOr + "\"";

    public static String fullName(String filename) {
        return ROOT_STR + File.separator + filename;
    }

    public static void checkExit(String input) {
        if (input.equals("exit")) {
            System.out.println("User asked to close app. Will exit.");
            System.exit(0);
        }
    }

    private static int chooseOption(Scanner scanner, String[] options) {
        while (true) {
            System.out.println("Type one of the following options: " + String.join(",", options));
            String input = scanner.next();
            checkExit(input);
            for (int i = 0; i < options.length; i++) {
                if (input.equals(options[i])) {
                    return i;
                }
            }
        }

    }

    private static double calculateProba(int[][] reqs) {
        double sumOfAllCases = 0;
        for (int i0 = 0; i0 < reqs[0].length; i0++) {
            for (int i1 = 0; i1 < reqs[1].length; i1++) {
                for (int i2 = 0; i2 < reqs[2].length; i2++) {
                    for (int i3 = 0; i3 < reqs[3].length; i3++) {
                        for (int i4 = 0; i4 < reqs[4].length; i4++) {
                            int[] teamQuatuor = new int[]{reqs[1][i1], reqs[2][i2], reqs[3][i3], reqs[4][i4]};
                            sumOfAllCases += Statix.getProba(reqs[0][i0], teamQuatuor);
                            DebugData.cmdCounter++;
                        }
                    }
                }
            }
        }

        return sumOfAllCases / Statix.commonDenominator();
    }

    public static int guessGroup(String input) {
        if (input.length() != 1) {
            return -2;
        }
        if (input.equals(symbWild)) {
            return -1;
        }
        char group = Misc.decapitalize(input).charAt(0);
        if (group < 'a' || group >= 'a' + Statix.nbGroups()) {
            return -2;
        }
        return group - 'a';

    }

    public static int guessTeam(int round, String input) throws ProbaParamEx {
        if (input.equals(symbWild)) {
            return -1;
        }
        int gr = Statix.indexValidateByPrefix(round, input);
        return gr;
    }

    public static String askWithDefault(Scanner scanner, String quest, String defaultVal) {
        System.out.print(quest);
        System.out.println(" [enter " + guilNo + " to choose" + defaultVal + "]");
        String res = scanner.next();
        if (res.equals(symbNo)) {
            return defaultVal;
        }
        return res;
    }

    public static void main1() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome!");
        String filename = askWithDefault(scanner, "What file should the teams be loaded from?", teamsFileName);
        Loading.load(fullName(filename));
        System.out.println("Should we calculate from scratch? If yes, enter " + guilNo);
        System.out.println("Otherwise, enter the name of the file from which data will be imported.");
        String nameToLoad = scanner.next();
        if (nameToLoad.equals(symbNo)) {
            System.out.println("Should calculations be saved in a file for next time?");
            System.out.println("Type the name of the file you wish to use, or  " + guilNo + " for no save.");
            filename = scanner.next();
            //String fullName = filename.length <0 
            if (filename.equals(symbNo)) {
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

    public static void main2() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome!");
        //String dataName = askWithDefault(scanner, "What folder should data be taken from?", defaultScenarioName);
        System.out.println("What folder should data be taken from?");
        String[] options = new File(dataPath()).list();
        int chosenIdx = chooseOption(scanner, options);
        String dataName = new File(options[chosenIdx]).getName();

        //if (!new File(dataPath(dataName)).exists())
        Loading.load(paramFile(dataName));

        boolean recalculate = true;
        File calculFile = new File(calculFile(dataName));
        if (calculFile.exists()) {
            System.out.print(calculFile.getPath() + " already contains calculation data. Load it or restart calculations from scratch?");
            System.out.println("Load it or restart calculations from scratch?");
            options = new String[]{"load", "restart"};
            int opt = chooseOption(scanner, options);
            if (opt == 0) {
                recalculate = false;
            } else {
                System.out.println(calculFile.getPath() + " will then be overwritten.");
            }
        }

        if (!recalculate) {

            try {
                Stats.loadFromFile(calculFile.getPath());
            } catch (InitParseException | IOException ex) {
                System.out.println("Error while loading " + calculFile.getPath());
                System.out.println(ex.getMessage());
                System.out.println("Recalculation will thus be necessary.");
                System.out.println("You might want to exit and get your shit together instead of continuing.");
                System.out.println("Type anything else if you just want to continue, recalculate and overwrite previous data");
                checkExit(scanner.next());
                recalculate = true;
            }
        }

        if (recalculate) {
            CalculusMain.run(calculFile.getPath());
        }

        /*System.out.println("Should we calculate from scratch? If yes, enter " + guilNo);
        System.out.println("Otherwise, enter the name of the file from which data will be imported.");
        String nameToLoad = scanner.next();
        if (nameToLoad.equals(symbNo)) {
            System.out.println("Should calculations be saved in a file for next time?");
            System.out.println("Type the name of the file you wish to use, or  " + guilNo + " for no save.");
            filename = scanner.next();
            //String fullName = filename.length <0 
            if (filename.equals(symbNo)) {
                filename = null;
            }
            CalculusMain.run(fullName(filename));

        } else {
            Stats.loadFromFile(nameToLoad);
        }*/
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

    public static boolean manageProbaParamCmd(Scanner scanner) throws ProbaParamEx {
        System.out.println("What probability would you like to calculate?");
        String input = scanner.next();
        MegaMain.checkExit(input);
        String[] args = input.split(",");
        if (args.length != 5) {
            System.out.println("Your command should have 5 parameters separated by 4 commas: ");
            System.out.println("group,pot1team,pot2team,pot3team,pot4team");
            System.out.print("If desired, use " + guilWild + " as wild cards");
            System.out.println(" and " + guilWild + " to enumerate groups letters or teams");
            return true;
        }
        int nbOfSingletons = 0;

        int nbGroups = Statix.nbGroups();

        int[][] reqs = new int[5][];
        for (int i = 0; i < 5; i++) {
            if (args[i].length() == 0) {
                throw ProbaParamEx.makeEmptyArg(input);
            }
            if (args[i].equals(symbWild)) {
                reqs[i] = GeneralMeths.idPermArray(nbGroups);
                continue;
            }
            String[] enumer = args[i].split(MegaMain.symbOr);
            reqs[i] = new int[enumer.length];
            if (reqs[i].length == 1) {
                nbOfSingletons++;
            }
            for (int j = 0; j < enumer.length; j++) {
                if (enumer[j].length() == 0) {
                    throw ProbaParamEx.makeEmptyArg(args[i]);
                }
                int readVal;
                if (i == 0) {
                    readVal = MegaMain.guessGroup(enumer[j]);
                } else {
                    readVal = MegaMain.guessTeam(i - 1, enumer[j]);
                }
                /*if (readVal == -2 ) {
                    throw ProbaParamEx.makeBadArg(enumer[j]);
                } if (readVal == -3 ) {
                    throw ProbaParamEx.makeAmbiguousArg(enumer[j]);
                }*/
                reqs[i][j] = readVal;
            }
            boolean reps = !Misc.sortAndAvoidReps(reqs[i]);
            if (reps) {
                throw ProbaParamEx.makeRedundantArg(args[i]);
            }

        }
        if (nbOfSingletons < 1) {
            throw ProbaParamEx.makeLackSingleton();
        }

        double proba = calculateProba(reqs);
        System.out.println("Probability of that scenario " + proba);
        return true;
    }

    public static void main(String[] args) {
        main2();
    }

}
