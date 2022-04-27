/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package central;

import java.util.ArrayList;
import java.util.Comparator;

/**
 *
 * @author desharnc27
 */
public class NodeList {

    ArrayList<Node> list;

    /*void orderedInsert(Node n) {
        int index = Collections.
    }*/
    public NodeList() {
        list = new ArrayList<>();
    }

    void sort() {
        list.sort(new StateComparator());
    }

    void sort(Comparator<Node> comp) {
        list.sort(comp);
    }

}
