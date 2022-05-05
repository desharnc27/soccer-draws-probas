/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package central;

/**
 *
 * @author desharnc27
 *
 * Stores an array of bytes
 */
public class ByteArray implements Comparable<ByteArray> {

    public byte[] arr;

    public ByteArray(byte[] arr) {
        this.arr = arr;
    }

    @Override
    public int compareTo(ByteArray o) {
        for (int i = 0; i < arr.length; i++) {
            int comp = arr[i] - o.arr[i];
            if (comp != 0) {
                return comp;
            }
        }
        return 0;
    }

}
