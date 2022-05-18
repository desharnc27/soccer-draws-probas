/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package central;

import java.util.ArrayList;

/**
 *
 * @author desharnc27
 */
public class StatByTeams extends StatFida {

    long[][][][][] detailedIndividual;

    public StatByTeams() {
        super();
        initializeIndivArray();
    }

    private final void initializeIndivArray() {
        int ngr = Statix.nbGROUPS();
        detailedIndividual = new long[ngr][ngr][ngr][ngr][ngr];
    }

    public void transfer() {
        //if (detailedIndividual==null)
        initializeIndivArray();
        int contToIndivRatio[] = new int[4];
        for (int gr = 0; gr < Statix.nbGROUPS(); gr++) {
            for (int p0 = 0; p0 < Statix.nbGROUPS(); p0++) {
                contToIndivRatio[0] = this.contToIndivNbPoss(0, gr, p0);
                for (int p1 = 0; p1 < Statix.nbGROUPS(); p1++) {
                    contToIndivRatio[1] = this.contToIndivNbPoss(1, gr, p1);
                    for (int p2 = 0; p2 < Statix.nbGROUPS(); p2++) {
                        contToIndivRatio[2] = this.contToIndivNbPoss(2, gr, p2);
                        for (int p3 = 0; p3 < Statix.nbGROUPS(); p3++) {
                            contToIndivRatio[3] = this.contToIndivNbPoss(3, gr, p3);
                            int ratio = 1;
                            for (int round = 0; round < 4; round++) {
                                ratio *= contToIndivRatio[round];
                                if (ratio < 0) {
                                    break;
                                }
                            }
                            if (ratio < 0) {
                                continue;
                            }

                            int[] teamIdxArr = new int[]{p0, p1, p2, p3};
                            int[] conts = new int[4];
                            for (int round = 0; round < 4; round++) {
                                conts[round] = Statix.getTeam(round, teamIdxArr[round]).cont();
                            }
                            //long detailedCont = this.detailedRoundStats[gr][conts[0]][conts[1]][conts[2]][conts[3]];
                            long detailedCont = (long) Misc.getValAt(detailedRoundStats[gr], conts);
                            if (detailedCont % ratio != 0) {
                                System.out.println("warining: trololo");
                            }
                            detailedIndividual[gr][p0][p1][p2][p3] = detailedCont / ratio;
                        }
                    }
                }
            }
        }
    }

    @Override
    public long getRatha(int gr, int[] teamQuatuorArr) {
        if (detailedIndividual == null) {
            return super.getRatha(gr, teamQuatuorArr);
        }
        return (long) Misc.getValAt(detailedIndividual[gr], teamQuatuorArr);
    }

    public void feedByTeamIdxs(byte[][] teamIdxs, long num) {
        for (int gr = 0; gr < Statix.nbGROUPS(); gr++) {
            int t0 = teamIdxs[0][gr];
            int t1 = teamIdxs[1][gr];
            int t2 = teamIdxs[2][gr];
            int t3 = teamIdxs[3][gr];
            this.detailedIndividual[gr][t0][t1][t2][t3] += num;
        }
        globalCount += num;
    }

    @Override
    public void feed(ArrayList<Byte> teamBdOrder, ArrayList<Byte> groupOrder) {
        int len = 4 * Statix.nbGROUPS();
        if (teamBdOrder.size() != len) {
            System.out.printf("Warining: feed aborted because teamBdOrder.size()!= len: %d != %d\n", teamBdOrder.size(), len);
            return;
        }
        if (groupOrder.size() != len) {
            System.out.printf("Warining: feed aborted because groupOrder.size()!= len: %d != %d\n", groupOrder.size(), len);
            return;
        }

        byte[][] teamIdxs = new byte[4][Statix.nbGROUPS()];
        for (int i = 0; i < len; i++) {
            int round = i / Statix.nbGROUPS();
            teamIdxs[round][groupOrder.get(i)] = teamBdOrder.get(i);
        }
        feedByTeamIdxs(teamIdxs, 1);
    }

}
