/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scanlol;

import init.ScanMeths;
import java.io.InputStream;
import java.util.Scanner;

/**
 *
 * @author desharnc27
 */
public class MyScanna implements Flower {

    Scanner scanner;

    public MyScanna(InputStream is) {
        scanner = new Scanner(is);
    }

    @Override
    public boolean hasNext() {
        return scanner.hasNext();
    }

    @Override
    public String next() {
        return scanner.next();
    }

    public Scanner getScanner() {
        return scanner;
    }

    @Override
    public String questionStr(String question) {
        return ScanMeths.questionStr(scanner, question);
    }

    @Override
    public String questionStr(String question, String regex) {
        return ScanMeths.questionStr(scanner, question, regex);
    }

}
