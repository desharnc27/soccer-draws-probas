/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package central;

import exception.InitParseException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 *
 * @author desharnc27
 */
public class Stats {

    static long[][] pairSameGroup = new long[Statix.NB_CONTS * 4][Statix.NB_CONTS * 4];
    static long[][][][][] detailedRoundStats
            = new long[Statix.NB_GROUPS][Statix.NB_CONTS][Statix.NB_CONTS][Statix.NB_CONTS][Statix.NB_CONTS];

    static int analyzedRounds = 4;
    static int noRefRounds = 1;//Sould always be 1 unless Node.iscompletable() code gets positively changed.

    //deprecated
    public static double countryProbability(byte round0, byte cont0, byte round1, byte cont1) {
        int idx0 = cont0 + round0 * Statix.NB_CONTS;
        int idx1 = cont1 + round1 * Statix.NB_CONTS;
        long fact = Misc.fact(Statix.NB_GROUPS);
        long denom = Misc.power(fact, analyzedRounds) / Statix.NB_GROUPS;
        return (Stats.pairSameGroup[idx0][idx1] + 0.0) / denom / Statix.pots[round0][cont0] / Statix.pots[round1][cont1];
    }

    public static long getSpecificProbLong(int gr, byte[] conts) {
        return detailedRoundStats[gr][conts[0]][conts[1]][conts[2]][conts[3]];
    }

    public static void print() {
        for (int i = 0; i < pairSameGroup.length; i++) {
            System.out.println(Arrays.toString(pairSameGroup[i]));
        }
    }

    /*public static fWritePairs(){
        String [][] strVals = new String[pairSameGroup.length][pairSameGroup.length];
        for (int i=0;i<strVals.length;i++)
            for (int j=0;j<strVals.length;j++){
                strVals[i][j] = String.valueOf(pairSameGroup[i][j]);
            }
        
        String[] lines = new String[pairSameGroup.length];
        String total = "";
        for (int i = 0; i < lines.length; i++) {
            lines[i] = "";
            String line = "";
            for (int j = 0; j < pairSameGroup.length; j++) {
                String add = String.valueOf(pairSameGroup[i][j]);
                if (j != 0) {
                    add = "," + add;
                }
                line += add;
            }
            String add = String.valueOf(line);
            if (i != 0) {
                add = "\n" + add;
            }
            total += add;
        }
        Misc.write(total, total);

    }*/
    public static void printPairs() {
        System.out.println();
        int len = pairSameGroup.length;
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                if (j != 0) {
                    System.out.print(",");
                }
                System.out.print(pairSameGroup[i][j]);
            }
            System.out.println();
        }
        System.out.println();
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) {
                if (j != 0) {
                    System.out.print(",");
                }
                int u = Statix.NB_CONTS;
                System.out.print(countryProbability((byte) (i / u), (byte) (i % u), (byte) (j / u), (byte) (j % u)));
            }
            System.out.println();
        }
    }

    static void feed(byte[][] potsState, long num) {
        for (int gr = 0; gr < Statix.NB_GROUPS; gr++) {
            int t0 = potsState[0][gr];
            int t1 = potsState[1][gr];
            int t2 = potsState[2][gr];
            int t3 = potsState[3][gr];
            detailedRoundStats[gr][t0][t1][t2][t3] += num;

        }
        for (int gr = 0; gr < Statix.NB_GROUPS; gr++) {
            for (int i = 0; i < Stats.analyzedRounds; i++) {
                for (int j = 0; j < Stats.analyzedRounds; j++) {
                    byte cont0 = potsState[i][gr];
                    byte cont1 = potsState[j][gr];
                    int idx0 = cont0 + i * Statix.NB_CONTS;
                    int idx1 = cont1 + j * Statix.NB_CONTS;
                    Stats.pairSameGroup[idx0][idx1] += num;
                }
            }
        }
    }

    public static void StoreInfile(String filename) {
        int chainLen = (int) (2 + Statix.NB_GROUPS * Misc.power(Statix.NB_CONTS, 4) * Long.BYTES);
        byte[] byteChain = new byte[chainLen];
        int idx = 0;
        byteChain[idx++] = Statix.NB_GROUPS;
        byteChain[idx++] = Statix.NB_CONTS;
        for (int gr = 0; gr < Statix.NB_GROUPS; gr++) {
            for (int p0 = 0; p0 < Statix.NB_CONTS; p0++) {
                for (int p1 = 0; p1 < Statix.NB_CONTS; p1++) {
                    for (int p2 = 0; p2 < Statix.NB_CONTS; p2++) {
                        for (int p3 = 0; p3 < Statix.NB_CONTS; p3++) {
                            byte[] bytes = Misc.longToBytes(detailedRoundStats[gr][p0][p1][p2][p3]);
                            for (byte by : bytes) {
                                byteChain[idx++] = by;
                            }
                        }
                    }
                }
            }
        }

        try {
            Files.write(Paths.get(filename), byteChain);
            System.out.println("Stats were written in " + filename);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.out.println("No file was written");
        }

    }

    public static void loadFromFile(String filename) throws InitParseException, IOException {
        byte[] byteChain = Files.readAllBytes(Paths.get(filename));
        /*try {
            byteChain = Files.readAllBytes(Paths.get(filename));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.out.println("No file was read");
            return;
        }*/

        int idx = 0;
        int tempNbGroups = byteChain[idx++];
        int tempNbConts = byteChain[idx++];

        if (Statix.NB_GROUPS != tempNbGroups || Statix.NB_CONTS != tempNbConts) {
            String mismatchParameter = Statix.NB_GROUPS == tempNbGroups
                    ? "Number of mono-continents or Number of hybrids" : "number of groups";
            throw InitParseException.makeDataMismatch(mismatchParameter);
        }
        //Statix.NB_GROUPS = byteChain[idx++];
        //Statix.NB_CONTS = byteChain[idx++];
        for (int gr = 0; gr < Statix.NB_GROUPS; gr++) {
            for (int p0 = 0; p0 < Statix.NB_CONTS; p0++) {
                for (int p1 = 0; p1 < Statix.NB_CONTS; p1++) {
                    for (int p2 = 0; p2 < Statix.NB_CONTS; p2++) {
                        for (int p3 = 0; p3 < Statix.NB_CONTS; p3++) {
                            byte[] bytes = Arrays.copyOfRange(byteChain, idx, idx + Long.BYTES);
                            long val = Misc.bytesToLong(bytes);
                            detailedRoundStats[gr][p0][p1][p2][p3] = val;
                            idx += Long.BYTES;
                        }
                    }
                }
            }
        }

    }
}
