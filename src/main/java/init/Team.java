/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package init;

import central.Misc;
import central.Statix;
import java.util.Arrays;

/**
 *
 * @author desharnc27
 */
public class Team {

    //static int [][] potsByCont;
    //static 
    final String name;
    final byte cont;
    final byte pot;

    public Team(String name, byte cont, byte pot) {
        this.name = name;
        this.pot = pot;
        this.cont = cont;
    }

    @Override
    public String toString() {
        String res = "[" + name + "," + pot + "," + cont + "]";
        return res;

    }

    public String name() {
        return name;
    }

    public byte pot() {
        return pot;
    }

    public byte cont() {
        return cont;
    }

    public static String validateTeamsNoAmbiguousPrefixes(Team[][] teams) {
        String message = null;
        for (int i = 0; i < 4; i++) {
            String[] decaps = new String[teams[i].length];
            for (int j = 0; j < decaps.length; j++) {
                decaps[j] = Misc.decapitalize(teams[i][j].name());
            }
            Arrays.sort(decaps);
            for (int j = 0; j < decaps.length - 1; j++) {
                if (decaps[j + 1].startsWith(decaps[j])) {
                    return decaps[j + 1] + " has " + decaps[j] + " as a prefix, which will cause parsing conflicts.";
                }
            }
        }
        return message;
    }

}
