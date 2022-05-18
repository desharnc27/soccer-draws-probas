/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package init;

import java.util.LinkedList;
import scanlol.Flower;
import scanlol.PresetScanner;

/**
 *
 * @author desharnc27
 */
public class DebugMain {

    public static void main0() {
        LinkedList<String> cmds = new LinkedList<>();
        cmds.add("q");
        cmds.add("s");
        cmds.add("n,32");
        cmds.add("exit");
        Flower flower = new PresetScanner(cmds);
        MegaMain.mainRun(flower);
    }

    public static void main(String[] args) {
        main0();
    }
}
