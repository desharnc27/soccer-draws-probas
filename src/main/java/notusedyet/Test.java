/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package notusedyet;

import central.Misc;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

/**
 *
 * @author desharnc27
 */
public class Test {

    public static void main0() {
        /*int nbGroups = 8;
        boolean [] used = new boolean[nbGroups];
        used[0] =true;
        used[2] = true;
        used[5] = true;
        int nbChoicies = */
    }

    public static void main(String[] args) {
        main0();
        Scanner scanner = new Scanner(System.in);
        System.out.println(scanner.delimiter());
        System.out.println(System.in.getClass());
        ArrayList<String> list = new ArrayList<>();
        list.add("Alice");
        list.add("Bob");
        list.add("Chuck");
        Iterator<String> iter = list.iterator();
        System.out.println(list.iterator().getClass());
        System.out.println(iter.getClass());
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }

        long[][] test = new long[][]{
            {0, 1, 2},
            {2, 4, 7},
            {33, 55, 88},};
        int[] indexes = new int[]{1, 2};
        long res = (long) Misc.getValAt(test, indexes);
        System.out.println(res);

    }
}
