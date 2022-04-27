/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package central;

import java.util.Comparator;

/**
 *
 * @author desharnc27
 */
public class StateComparator implements Comparator<Node> {

    @Override
    public int compare(Node n0, Node n1) {
        int comp0 = n0.level - n1.level;
        if (comp0 != 0) {
            return comp0;
        }
        comp0 = n0.potsState.length - n1.potsState.length;
        if (comp0 != 0) {
            return comp0;
        }
        return Misc.compareRev(n0.potsState, n1.potsState);
    }

}
