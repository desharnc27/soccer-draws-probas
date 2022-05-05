/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package exception;

import central.Misc;

/**
 *
 * @author desharnc27
 */
public class ProbaParamEx extends Exception {

    public static ProbaParamEx makeAnonymus() {
        return new ProbaParamEx("");
    }

    public static ProbaParamEx makeEmptyArg(String arg) {
        return new ProbaParamEx(arg + " contains empty parameters");
    }

    public ProbaParamEx(String mess) {
        super(mess);
    }

    public static ProbaParamEx makeBadArg(String arg) {
        return new ProbaParamEx("Parser couldn't guess what " + arg + " is refering to");
    }

    public static ProbaParamEx makeAmbiguousArg(String arg, String poss1, String poss2) {
        return new ProbaParamEx(Misc.ambiguousStr(arg, poss1, poss2));
    }

    public static ProbaParamEx makeRedundantArg(String arg) {
        return new ProbaParamEx("Redundant arguments within " + arg);
    }

    public static ProbaParamEx makeLackSingleton() {
        return new ProbaParamEx("At least one paramter should be a singleton (no enumeration, no wild card).");
    }

}
