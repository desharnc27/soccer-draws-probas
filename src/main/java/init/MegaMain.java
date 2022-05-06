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
import tools.GeneralMeths;
import tools.RootFinder;

/**
 *
 * @author desharnc27
 */
public class MegaMain {

    public static final String ROOT_STR = RootFinder.getRootPath();

    private final static String DATA_FOLDER_NAME = "data";
    private final static String DEFAULT_SCENARIO_NAME = "default";
    private final static String DRAFT_CONFIG = "draft.txt";
    private final static String STAT_STORAGE_NAME = "algoData";    

    public static final String EXIT = "exit";
    public static final String HELP = "help";
    public static final String DISPLAY_TEAMS = "teams";
    public static final String MODEL = "example";
    public static final String HELP_CONFIG = DRAFT_CONFIG;
    public static final String HELP_PARAMS = "input.txt";
    public static final String DEMO_WARNING = "demoWarning.txt";

    public static String dataRoot() {
        return ROOT_STR + File.separator + DATA_FOLDER_NAME + File.separator;
    }

    public static String dataPath(String scenario) {
        return dataRoot() + scenario + File.separator;
    }

    public static String dataPath() {
        return dataPath(DEFAULT_SCENARIO_NAME);
    }

    public static String paramFile(String scenario) {
        return dataRoot() + scenario + File.separator + DRAFT_CONFIG;
    }

    public static String calculFile(String scenario) {
        return dataRoot() + scenario + File.separator + STAT_STORAGE_NAME;
    }

    public static String paramFile() {
        return paramFile(DEFAULT_SCENARIO_NAME);
    }

    public static String calculFile() {
        return calculFile(DEFAULT_SCENARIO_NAME);
    }

    public static String helpFolder() {
        return ROOT_STR + File.separator + HELP + File.separator;
    }

    public static String helpParamFile() {
        return helpFolder() + HELP_PARAMS;
    }

    public static String helpConfigFile() {
        return helpFolder() + HELP_CONFIG;
    }

    public static final String SYMB_NO = "-";
    public static final String GUIL_NO = Misc.guillemet(SYMB_NO);
    public static final String SYMB_WILD = "*";
    public static final String GUIL_WILD = Misc.guillemet(SYMB_WILD);
    public static final String SYMB_OR = "/";
    public static final String GUIL_OR = Misc.guillemet(SYMB_OR);

    public static String fullName(String filename) {
        return ROOT_STR + File.separator + filename;
    }

    private static void printGlobalOptions() {
        System.out.println("By the way, you can type " + Misc.guillemet(EXIT) + " to close app.");
        System.out.println("You can type " + Misc.guillemet(DISPLAY_TEAMS) + " to show the name of all teams.");
        System.out.println("You can type " + Misc.guillemet(HELP) + " to get some general indications and examples");
    }

    public static void checkExit(String input) {
        if (input.equals(EXIT)) {
            System.out.println("User asked to close app. Will exit.");
            if (DebugData.printDebugSums) {
                DebugData.printCounters();
            }
            System.exit(0);
        }
    }

    private static boolean checkHelp(String input, boolean helpOnConfigFile) {
        if (input.equals(HELP)) {
            if (helpOnConfigFile) {
                String message = "Open file " + paramFile(MODEL)
                        + "for an example on how to fill " + DRAFT_CONFIG + " files";
                System.out.println(message);
                return true;
            }
            String helpFile = helpParamFile();
            Misc.displayFile(helpFile);
            return true;
        }
        return false;
    }

    private static int chooseOption(Scanner scanner, String[] options) {

        while (true) {

            System.out.println("Type one of the following options: " + String.join(",", options));
            String input = scanner.next();
            checkExit(input);
            int chosenIdx = -1;

            //boolean exactMatchFound = false;
            for (int i = 0; i < options.length; i++) {
                if (options[i].startsWith(input)) {
                    if (input.equals(options[i])) {
                        return i;
                    }
                    if (chosenIdx > 0) {
                        System.out.print(Misc.ambiguousStr(input, options[chosenIdx], options[i]));
                        chosenIdx = -2;
                        break;
                    }
                    chosenIdx = i;
                }
            }
            if (chosenIdx >= 0) {
                System.out.println(Misc.inputUnderstoodAs(input, options[chosenIdx]));
                return chosenIdx;
            }
            if (chosenIdx == -1) {
                System.out.print(Misc.inputUnsolved(input));
            }
            System.out.println(" Enter something else.");
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

    public static int guessGroup(String input) throws ProbaParamEx {
        if (input.length() != 1) {
            throw ProbaParamEx.makeBadArg(input);
        }
        if (input.equals(SYMB_WILD)) {
            return -1;
        }
        char group = Misc.decapitalize(input).charAt(0);
        if (group < 'a' || group >= 'a' + Statix.nbGROUPS()) {
            throw ProbaParamEx.makeBadArg(input);
        }
        return group - 'a';

    }

    public static int guessTeam(int round, String input) throws ProbaParamEx {
        if (input.equals(SYMB_WILD)) {
            return -1;
        }
        int gr = Statix.indexValidateByPrefix(round, input);
        return gr;
    }

    public static String askWithDefault(Scanner scanner, String quest, String defaultVal) {
        System.out.print(quest);
        System.out.println(" [enter " + GUIL_NO + " to choose" + defaultVal + "]");
        String res = scanner.next();
        if (res.equals(SYMB_NO)) {
            return defaultVal;
        }
        return res;
    }

    private static void exactApp(Scanner scanner, String dataName) {

        //data management
        boolean recalculate = true;
        File calculFile = new File(calculFile(dataName));
        if (calculFile.exists()) {
            System.out.print(calculFile.getPath() + " already contains calculation data. ");
            System.out.println("Load it or restart calculations from scratch?");
            String[] options = new String[]{"load", "restart"};
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
        boolean still = true;
        byte consecutiveMistakes = 0;
        while (still) {
            DebugData.cmdCounter = 0;
            try {
                still = manageProbaParamCmd(scanner);
                consecutiveMistakes = 0;
            } catch (ProbaParamEx ex) {
                System.out.println(ex.getMessage());
                consecutiveMistakes++;
                if (consecutiveMistakes > 2) {
                    printGlobalOptions();
                }
            }
            //System.out.println("DebugData.cmdCounter: " + DebugData.cmdCounter);
        }
    }

    private static void simulApp(Scanner scanner, String dataName) {
        CalculusMain.buildAscendStorage();
        Simulating.simulateOne((byte) 2);
    }

    private static boolean manageProbaParamCmd(Scanner scanner) throws ProbaParamEx {
        System.out.println("What probability would you like to calculate?");
        String input = scanner.next();
        MegaMain.checkExit(input);
        if (MegaMain.checkHelp(input, false)) {
            return true;
        }
        if (MegaMain.checkTeamDisplay(input)) {
            return true;
        }
        String[] args = input.split(",");
        if (args.length != 5) {
            System.out.println("Your command should have 5 parameters separated by 4 commas: ");
            System.out.println("group,pot1team,pot2team,pot3team,pot4team");
            System.out.print("If desired, use " + GUIL_WILD + " as wild cards");
            System.out.println(" and " + GUIL_OR + " to enumerate groups letters or teams");
            throw ProbaParamEx.makeAnonymus();
        }
        int nbOfSingletons = 0;

        int nbGroups = Statix.nbGROUPS();

        int[][] reqs = new int[5][];
        for (int i = 0; i < 5; i++) {
            if (args[i].length() == 0) {
                throw ProbaParamEx.makeEmptyArg(input);
            }
            if (args[i].equals(SYMB_WILD)) {
                reqs[i] = GeneralMeths.idPermArray(nbGroups);
                continue;
            }
            String[] enumer = args[i].split(MegaMain.SYMB_OR);
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

    private static boolean checkTeamDisplay(String input) {
        if (input.equals(MegaMain.DISPLAY_TEAMS)) {
            displayTeams();
            return true;
        }
        return false;
    }

    private static void displayTeams() {
        for (int i = 0; i < 4; i++) {
            System.out.print("Pot " + (i + 1) + ": ");
            String[] enu = new String[Statix.nbGROUPS()];
            for (int j = 0; j < enu.length; j++) {
                enu[j] = Statix.getTeam(i, j).name();
            }
            System.out.println(String.join(",", enu));
        }
    }

    private static void mainRun() {

        //Welcome
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome!");
        //String dataName = askWithDefault(scanner, "What folder should data be taken from?", DEFAULT_SCENARIO_NAME);
        System.out.println("What folder should data be taken from?");
        String[] options = new File(dataRoot()).list();
        int chosenIdx = chooseOption(scanner, options);
        String dataName = new File(options[chosenIdx]).getName();
        Loading.load(paramFile(dataName));
        if (!MegaMain.RESTRICTED_VERSION)            
            DebugData.initialize();
        //
        System.out.println("You want to explore exact probabilities or use a simulator?");
        options = new String[]{"exact", "simulator"};
        chosenIdx = chooseOption(scanner, options);
        if (chosenIdx == 0) {
            exactApp(scanner, dataName);
        } else {
            simulApp(scanner, dataName);
        }
    }

    public static void main(String[] args) {
        if (RESTRICTED_VERSION)
            Misc.displayFile(DEMO_WARNING);
        mainRun();
    }
    
    final static boolean RESTRICTED_VERSION = false;

}
