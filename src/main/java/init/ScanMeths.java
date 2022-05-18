/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package init;

import central.Misc;
import static init.MegaMain.checkExit;
import java.util.Iterator;
import java.util.Scanner;
import scanlol.Flower;
import scanlol.MyScanna;
import scanlol.PresetScanner;

/**
 *
 * @author desharnc27
 */
public class ScanMeths {

    /*
     * Returns next String from either scanner if it's not null or from iter otherwise. Exactly one of the two should be null.
     * @param scanner
     * @param iter
     * @return next String from either scanner if it's not null or from iter otherwise.
     */
 /*public static String next(Scanner scanner, Iterator<String> iter) {
        if (scanner != null) {
            return scanner.next();
        }
        if (!iter.hasNext()) {
            System.out.println("Warning: no next element for " + iter);
        }
        return iter.next();
    }*/
    public static void printIfScan(Flower scanner, String txt) {
        if (scanner instanceof PresetScanner) {
            return;
        }
        System.out.print(txt);
    }

    public static void printlnIfScan(Flower scanner, String txt) {
        if (scanner instanceof PresetScanner) {
            return;
        }
        System.out.println(txt);
    }

    public static String questionStr(Scanner scanner, String question) {
        return questionStr(scanner, question, ".*");
    }

    public static String questionStr(Scanner scanner2, String question, String acceptedRegex) {
        // System.out.println(question);
        //return scanner.next();
        while (true) {

            System.out.println(question);
            String input = scanner2.next();
            if (input.matches(acceptedRegex)) {
                return input;
            }
            System.out.println("Input " + input + " does not respect rules: it must match the following regex: " + acceptedRegex);
        }
    }

}
