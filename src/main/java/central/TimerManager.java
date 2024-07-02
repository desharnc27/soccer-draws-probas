package central;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author desharnc27
 */
public class TimerManager {
    //private static double currentCompletion;
    //private static int currentRound;
    //private static int currentSize;

    private static Timer timer = new Timer();
    private static boolean updateToDisplay = true;
    private static final ArrayList<Proportion> props = new ArrayList<>();

    private static void displayUpdate() {
        long num = 0;
        long denom = 1;

        for (int i = 0; i < props.size(); i++) {
            Proportion prop = props.get(i);
            num = num * prop.denom + prop.num;
            denom *= prop.denom;
        }
        long percentage = 100 * num / denom;
        System.out.println("Algorithm progress: " + percentage + "% completed.");
        updateToDisplay = false;

    }

    public static void notifyCompletion(int round, int size) {
        updateCurrentNumbers(round, size);
        if (!updateToDisplay) {
            return;
        }
        displayUpdate();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                updateToDisplay = true;
            }
        };
        timer.schedule(task, 5000);
    }

    private static void updateCurrentNumbers(int round, int size) {
        if (round + 1 > props.size()) {
            Proportion next = new Proportion(0, size);
            props.add(next);
        } else if (round + 1 == props.size()) {
            props.get(props.size() - 1).num++;
        } else {
            props.remove(props.size() - 1);
            props.get(props.size() - 1).num++;
        }
    }

    private static class Proportion {

        int num;
        int denom;

        public Proportion(int n, int d) {
            num = n;
            denom = d;
        }
    }

}
