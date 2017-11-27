package com.km.lab2;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Modelling {

    public static void main(String[] args) {
        doSteps();
    }

    final private static int[] tau = {1, 15, 1, 120, 80, 25, 150, 3};
    final private static int[][] startState = {
            {0, 1, 0},  // 1 - CPU
            {0, 0, 1},  // 2 - ОП
            {0, 1, 0},  // 3 - NB
            {0, 1, 0},  // 4 - ВП
            {0, 0, 1},  // 5 - АП
            {0, 1, 0},  // 6 - МА
            {0, 1, 0},  // 7 - КД
            {0, 0, 1}   // 8 - SB
    };

    private static double[] sysTime = new double[8];
    private static double[] countCalls = new double[8];
    private static double[] lengthQueue = new double[8];
    private static double[] generalTime = new double[8];

    public static void doSteps() {
        int tasksCount = 10_000;
        double time = 0;
        for (int i = 0; i < 8; i++) {
            if (startState[i][1] == 1) genTimeExecution(time, i); //generate time for each currently working dev
        }
        doModelling(time, tasksCount);
    }

    public static void genTimeExecution(double time, int i) {
        double temp = - tau[i] * Math.log(Math.random());
        sysTime[i] = time + temp;
        generalTime[i] += temp;
    }

    public static void doModelling(double time, int taskCount) {
        int step = 0;
        int k = 0;
        int minIndex;
        do {
            minIndex = findMin();
            time = sysTime[minIndex];
            sysTime[minIndex] = 0;
            startState[minIndex][1]--;
            startState[minIndex][2]++;
            k = next(minIndex);
            startState[k][0]++;
            if ((startState[minIndex][0] > 0) && (startState[minIndex][2] == 1)) {
                startState[minIndex][0]--;
                startState[minIndex][1]++;
                startState[minIndex][2]--;
                genTimeExecution(time, minIndex);
            }
            if ((startState[k][0] > 0) && (startState[k][2] == 1)) {
                startState[k][0]--;
                startState[k][1]++;
                startState[k][2]--;

                genTimeExecution(time, k);
            }
            step++;
        } while (step < taskCount);
        printResult(time,taskCount);
    }

    public static int findMin() {
        double minTime = Integer.MAX_VALUE;
        int min = -1;
        for (int i = 0; i < 8; i++) {
            if ((sysTime[i] > 0) && (sysTime[i] < minTime)) {
                minTime = sysTime[i];
                min = i;
            }
        }
        return min;
    }

    private static int next(int i) {
        if ((i == 0) || (i == 1)) {
            countCalls[2]++;
            lengthQueue[2] += startState[2][0];
            return 2; //NB
        }
        else {
            if (i == 2) {
                if (Math.random() > 0.9) {
                    countCalls[7]++;
                    lengthQueue[7] += startState[7][0];
                    return 7;  //SB
                }
                if (Math.random() > 0.5) {
                    countCalls[1]++;
                    lengthQueue[1] += startState[1][0];
                    return 1;  //RAM
                }
                if (Math.random() > 0) {
                    countCalls[0]++;
                    lengthQueue[0] += startState[0][0];
                    return 0;    //CPU
                }
            }
            else
                if ((i == 3) || (i == 4)) {
                    countCalls[0]++;
                    lengthQueue[0] += startState[0][0];
                    return 0; //CPU
                }
                else
                    if ((i == 5) || (i == 6)) {
                        countCalls[7]++;
                        lengthQueue[7] += startState[7][0];
                        return 7; //SB
                    }
                    else
                        if (i == 7) {
                            if (Math.random() > 0.95){
                                countCalls[4]++;
                                lengthQueue[4] += startState[4][0];
                                return 4; //AP
                            }
                            if (Math.random() > 0.8){
                                countCalls[3]++;
                                lengthQueue[3] += startState[3][0];
                                return 3;  //VP
                            }
                            if (Math.random() > 0.7){
                                countCalls[5]++;
                                lengthQueue[5] += startState[5][0];
                                return 5;  //MA
                            }
                            if (Math.random() > 0.4){
                                countCalls[6]++;
                                lengthQueue[6] += startState[6][0];
                                return 6;  //KD
                            }
                            if (Math.random() > 0){
                                countCalls[2]++;
                                lengthQueue[2] += startState[2][0];
                                return 2;  //NB
                            }
                        }
            }
        return -1;
    }

    private static void printResult(double modelTime, int lastStep) {
        double[] results = new double[8];
        for (int i = 0; i < 8; i++) {
            if (generalTime[i] > modelTime) {
                results[i] = 100.0;
            }
            else {
                results[i] = new BigDecimal(100 * generalTime[i] / modelTime).setScale(1, RoundingMode.UP).doubleValue();
            }
        for(int j = 0; j < 8; j++){
            lengthQueue[i] = lengthQueue[i]/countCalls[i];
        }
        }
        System.out.println("Device     % ");
        System.out.println("CPU  - > " + results[0] + "  " + lengthQueue[0]);
        System.out.println("RAM  - > " + results[1] + "  " + lengthQueue[1]);
        System.out.println("NB   - > " + results[2] + "  " + lengthQueue[2]);
        System.out.println("VP   - > " + results[3] + "  " + lengthQueue[3]);
        System.out.println("AP   - > " + results[4] + "  " + lengthQueue[4]);
        System.out.println("MA   - > " + results[5] + "  " + lengthQueue[5]);
        System.out.println("KD   - > " + results[6] + "  " + lengthQueue[6]);
        System.out.println("SB   - > " + results[7] + "  " + lengthQueue[7]);
        System.out.println();
        double performance = (double)lastStep/modelTime;
        System.out.println("Продуктивність системи = " + performance);

    }
}