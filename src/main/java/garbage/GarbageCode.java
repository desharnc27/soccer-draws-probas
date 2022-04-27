/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package garbage;

import central.AscendStorer;
import central.ByteArray;
import central.DebugData;
import central.Misc;
import central.StateComparator;
import central.Statix;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author desharnc27
 */
public class GarbageCode {

    //From Node
    /*
    //xbegin
    boolean twado(byte teamAdd, byte groupAdd) {
        //ArrayList<Byte> noException = new ArrayList<>();
        boolean[] isException = new boolean[Statix.NB_CONTS];
        boolean euroOne = false;
        int nbc = Statix.NB_CONTS;
        for (int i = 0; i < 4; i++) {
            byte byteTemp = potsState[i][groupAdd];
            if (byteTemp == 0 && !euroOne) {
                euroOne = true;
            } else {
                isException[byteTemp] = true;
                nbc--;
            }

        }

        int idx = 0;
        byte[] trad = new byte[nbc];
        for (int i = 0; i < isException.length; i++) {
            if (!isException[i]) {
                trad[idx++] = (byte) i;
            }
        }

        boolean[] iterArr = new boolean[nbc];

        do {
            int dispo = 0;
            for (int gr = 0; gr < Statix.NB_GROUPS; gr++) {
                boolean takable = false;
                for (int i = 0; i < nbc; i++) {
                    if (!iterArr[i]) {
                        continue;
                    }
                    byte cont = trad[i];
                    if (groupCanTake(gr, cont)) {
                        takable = true;
                        break;
                    }
                }
                if (takable) {
                    dispo++;
                }

            }
            for (int i = 0; i < nbc; i++) {
                if (iterArr[i]) {
                    byte cont = trad[i];
                    dispo -= trRemains[cont];
                }
            }
            if (dispo < 0) {
                return false;
            }

        } while (CombMeths.boolIter(iterArr));
        return true;
    }
    
    protected void leftRightExpand(byte groupIdx, ArrayList<ByteArray> avaBank) {
        if (groupIdx == Statix.NB_GROUPS) {
            //System.out.println(Arrays.toString(potsState[3]));
            AscendStorer.includePossibleLastPot(potsState[3],avaBank);
            return;
        }
        for (byte i = 0; i < Statix.NB_CONTS; i++) {
            if (trRemains[i] == 0) {
                continue;
            }
            if (!groupCanTake(groupIdx, i)) {
                continue;
            }
            if (!isCompletable( i,groupIdx)) {
                continue;
            }
            trRemains[i]--;
            potsState[3][groupIdx] = i;
            leftRightExpand((byte) (groupIdx + 1), avaBank);
            trRemains[i]++;
            potsState[3][groupIdx] = -1;
        }
    }

    
    protected void bigLeftRightExpandOri() {
        bigLeftRightExpandOri(null);
    }

    protected void bigLeftRightExpandOri(AscendStorer asSt) {
        byte groupIdx = (byte) (level % Statix.NB_GROUPS);
        byte round = (byte) (level / Statix.NB_GROUPS);
        //AscendStorer newAscendStorer;
        boolean virginLineAdd = false;

        boolean newDygon = false;
        if (new StateComparator().compare(this, DebugData.node) == 0) {
            int debug = 1;
        }
        if (groupIdx == 0) {
            //System.out.println(Arrays.toString(potsState[3]));
            if (round == 4) {
                asSt.injectParts(potsState[3]);
                return;
            }
            if (!hasAscendingColumns()) {
                return;
            }
            if (round == 3) {
                newDygon = true;
                asSt = AscendStorer.buildWithPots(potsState);

            }
            virginLineAdd = true;
            potsState = Misc.addVirginLine(potsState);
            trRemains = Statix.getCopyOfPot(round);

        }
        for (byte i = 0; i < Statix.NB_CONTS; i++) {
            if (trRemains[i] == 0) {
                continue;
            }
            if (!groupCanTake(groupIdx, i)) {
                continue;
            }

            trRemains[i]--;
            potsState[round][groupIdx] = i;
            level++;
            bigLeftRightExpandOri(asSt);
            level--;
            trRemains[i]++;
            potsState[round][groupIdx] = -1;
        }
        if (virginLineAdd) {
            potsState = Misc.removeVirginLine(potsState);
            trRemains = trRemains = new int[Statix.NB_CONTS];
        }
        if (newDygon) {
            int ascendStorerIdx = Collections.binarySearch(AscendStorer.ascendStorerBank, asSt);
            ascendStorerIdx = -ascendStorerIdx - 1;
            AscendStorer.ascendStorerBank.add(ascendStorerIdx, asSt);
        }

    }
    protected void bigLeftRightExpand() {
        bigLeftRightExpand(new AscendStorer[3]);
    }
    
    protected void bigLeftRightExpand(AscendStorer  [] asArray) {
        byte groupIdx = (byte) (level % Statix.NB_GROUPS);
        byte round = (byte) (level / Statix.NB_GROUPS);
        //AscendStorer newAscendStorer;
        boolean virginLineAdd = false;
        
        byte newDygon = -1;
        if (new StateComparator().compare(this, DebugData.node) == 0) {
            int debug = 1;
        }
        if (groupIdx == 0) {
            //System.out.println(Arrays.toString(potsState[3]));
            if (round >= 2) {
                asArray[round-2].injectParts(potsState[round-1]);
            }
            if (round == 4)
                return;
            if (round == 1){
                    int debug = 1;
            }
            if (!hasAscendingColumns()) {
                return;
            }
            if (round >0 && round <4) {
                if (round == 1){
                    int debug = 1;
                }
                    
                asArray[round-1] =  AscendStorer.buildWithPots(potsState);
                newDygon = round;

            }
            virginLineAdd = true;
            potsState = Misc.addVirginLine(potsState);
            trRemains = Statix.getCopyOfPot(round);

        }
        for (byte i = 0; i < Statix.NB_CONTS; i++) {
            if (trRemains[i] == 0) {
                continue;
            }
            if (!groupCanTake(groupIdx, i)) {
                continue;
            }

            trRemains[i]--;
            potsState[round][groupIdx] = i;
            level++;
            bigLeftRightExpand(asArray);
            level--;
            trRemains[i]++;
            potsState[round][groupIdx] = -1;
        }
        if (virginLineAdd) {
            potsState = Misc.removeVirginLine(potsState);
            trRemains = trRemains = new int[Statix.NB_CONTS];
        }
        if (newDygon>=0) {
            //????
            int arrIdx = newDygon -1;
            

            int ascendStorerIdx = Collections.binarySearch(AscendStorer.ascendStorerBank, asArray[arrIdx]);
            ascendStorerIdx = -ascendStorerIdx - 1;
            AscendStorer.ascendStorerBank.add(ascendStorerIdx, asArray[arrIdx]);
            asArray[arrIdx] = null;
        }
    
    }
    
    boolean isCompletable() {

        if (new StateComparator().compare(this, DebugData.node) == 0) {
            int debug = 1;
        }

        int[] pseudoRemain = Arrays.copyOf(trRemains, Statix.NB_MONOCONTS);
        for (byte cont : getMonoConts((byte) (Statix.NB_MONOCONTS))) {
            try {
                pseudoRemain[cont] += trRemains[Statix.NB_MONOCONTS];
            } catch (Exception e) {
                int a = 1 / 0;
            }
        }

        for (byte cont = 0; cont < Statix.NB_MONOCONTS; cont++) {
            int dispo = 0;
            for (int gr = 0; gr < Statix.NB_GROUPS; gr++) {
                if (groupCanTake(gr, cont)) {
                    dispo++;
                }
            }
            if (dispo < pseudoRemain[cont]) {
                return false;
            }

        }
        //extreme euro case: even without immediate rule break, completing round 2 (pot 3)
        //leads to undoable round 3 if two groups don't have a euro team before last round. 
        int round = potsState.length - 1;
        if (round == 2) {
            int noEuro = 0;
            int doubleEuro = 0;
            for (int i = 0; i < Statix.NB_GROUPS; i++) {
                //if (potsState[round][i]==-1)
                //    continue;
                int euroCount = 0;
                for (int j = 0; j < 3; j++) {
                    if (potsState[j][i] == 0) {
                        euroCount++;
                    }
                }
                if (euroCount == 0 && potsState[round][i] != -1) {
                    noEuro++;
                } else if (euroCount == 2) {
                    doubleEuro++;
                }
            }
            if (noEuro >= 2) {
                return false;
            }
            if (doubleEuro >= 6) {
                return false;
            }

        }

        //for hybird group, TODO generalize
        if (trRemains[Statix.NB_MONOCONTS] == 0) {
            return true;
        }
        for (int gr = 0; gr < Statix.NB_GROUPS; gr++) {
            if (groupCanTake(gr, (byte) 1) && groupCanTake(gr, (byte) 4)) {
                return true;
            }
        }

        return false;
    }
    //xend
     */
}
