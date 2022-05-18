/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scanlol;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author desharnc27
 */
public class PresetScanner implements Flower {

    Iterator<String> iter;

    public PresetScanner(List<String> list) {
        iter = list.iterator();
    }

    public PresetScanner() {
        iter = null;
    }

    @Override
    public boolean hasNext() {
        return iter.hasNext();
    }

    @Override
    public String next() {
        return iter.next();
    }

    @Override
    public String questionStr(String questionStr) {
        return iter.next();
    }

    @Override
    public String questionStr(String questionStr, String regex) {
        while (iter.hasNext()) {
            String res = iter.next();
            if (res.matches(regex)) {
                return res;
            }
        }
        return null;
    }

}
