/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package garbage;

/**
 *
 * @author desharnc27
 */
class Placement {
    byte contId;
    byte group;
    Placement(byte contId,byte group){
        this.contId=contId;
        this.group=group;
    }
    @Override
    public String toString(){
        return "("+contId+","+group+")";
    }
}
