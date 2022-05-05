/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package central;

import algocore.Node;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author desharnc27
 */
public class NodeList {

    private ArrayList<Node> list;

    /*void orderedInsert(Node n) {
        int index = Collections.
    }*/
    public NodeList() {
        list = new ArrayList<>();
    }

    public void sort() {
        list.sort(new StateComparator());
    }

    public void sort(Comparator<Node> comp) {
        list.sort(comp);
    }

    public int size() {
        return list.size();
    }

    public void add(Node n) {
        list.add(n);
    }

    public Node get(int i) {
        return list.get(i);
    }

    public void clear() {
        list.clear();
    }

    public void add(int idx, Node n) {
        list.add(idx, n);
    }

    public int binarysearch(Node n) {
        return Collections.binarySearch(list, n, Statix.scmp);
    }

}
