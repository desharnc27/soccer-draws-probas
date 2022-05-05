/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package central;

import algocore.Node;
import java.util.Comparator;

/**
 *
 * @author desharnc27
 */
public class StateComparator implements Comparator<Node> {

    //boolean descendRound = true;
    //byte minRound = 0;
    //public StateComparator(){
    //}
    @Override
    public int compare(Node n0, Node n1) {
        return n0.potLevCompare(n1);
    }

}
