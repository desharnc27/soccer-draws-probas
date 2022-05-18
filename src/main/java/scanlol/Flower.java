/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scanlol;

/**
 *
 * @author desharnc27
 *
 */
public interface Flower {

    public boolean hasNext();

    public String next();

    public String questionStr(String questionStr);

    public String questionStr(String questionStr, String regex);
}
