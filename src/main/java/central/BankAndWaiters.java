/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package central;

/**
 *
 * @author desharnc27
 */
public class BankAndWaiters {

    static NodeList[] bank = new NodeList[33];
    static NodeList[] waiters = new NodeList[33];

    static void intitializeLists() {
        for (int i = 0; i <= 32; i++) {
            BankAndWaiters.bank[i] = new NodeList();
            BankAndWaiters.waiters[i] = new NodeList();
        }
    }
}
