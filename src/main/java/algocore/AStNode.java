/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algocore;

import central.ByteArray;
import central.Statix;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Tree (data structure) to enumerate all valid followings for an ascendStorer.
 * TODO explanations
 *
 * @author desharnc27
 */
public class AStNode {

    private final byte gr;
    private final AStNode[] children;

    public AStNode(byte gr) {
        this.gr = gr;
        if (leafLevel()) {
            children = null;
        } else {
            children = new AStNode[Statix.nbCONTS() + 1];
        }
    }

    public void inject(byte[] arr) {
        if (leafLevel()) {
            return;
        }
        byte pseudoCont = arr[gr];
        if (pseudoCont == -1) {
            pseudoCont = Statix.nbCONTS();
        }
        if (children[pseudoCont] == null) {
            children[pseudoCont] = new AStNode((byte) (gr + 1));
        }
        children[pseudoCont].inject(arr);
    }

    public boolean has(byte[] arr) {
        if (leafLevel()) {
            return true;
        }
        byte pseudoCont = arr[gr];
        if (pseudoCont == -1) {
            pseudoCont = Statix.nbCONTS();
        }
        if (children[pseudoCont] == null) {
            return false;
        }
        return children[pseudoCont].has(arr);
    }

    public final boolean leafLevel() {
        return gr == Statix.nbGROUPS();
    }

    public int size() {
        if (leafLevel()) {
            return 1;
        }
        int res = 0;
        for (AStNode child : children) {
            if (child != null) {
                res += child.size();
            }
        }
        return res;
    }

    public boolean isEmpty() {
        if (leafLevel()) {
            return false;
        }
        for (AStNode child : children) {
            if (child != null && !child.isEmpty()) {
                return false;
            }

        }
        return true;
    }

    public void feedForPrint(ArrayList<ByteArray> b0ss, byte[] actArr) {
        if (leafLevel()) {
            b0ss.add(new ByteArray(Arrays.copyOf(actArr, actArr.length)));
            return;
        }
        for (int i = 0; i < children.length; i++) {
            if (children[i] == null) {
                continue;
            }
            byte val = i == children.length - 1 ? (byte) -1 : (byte) i;
            actArr[gr] = val;
            children[i].feedForPrint(b0ss, actArr);
            actArr[gr] = -1;
        }
    }
}
