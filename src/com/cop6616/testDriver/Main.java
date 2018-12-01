package com.cop6616.testDriver;

import com.cop6616.lfcat.LFCAT;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/***
 * Test driver for Deuce STM version comparision of LFCAT trees. Runs 1000 operations over different scenario distributions.
 */
public class Main {

    static final int NUMOPS = 1000;

    public static void main(String[] args) {
        List<XYChart> charts = new ArrayList<XYChart>();

        Vector<Integer> x = new Vector<Integer>();
        Vector<Double> rw = new Vector<Double>();
        Vector<Double> hcnt = new Vector<Double>();
        Vector<Double> lcnt = new Vector<Double>();
        Vector<Double> hcnf = new Vector<Double>();

        //Runs different ratio scenartios through a change in threads
        for (int i = 0; i <= 5; i++) {
            try {
                x.add((int) Math.pow(2, i));

                Ratio realWorld = new Ratio(0.02f, 0.09f, 0.09f, 0.80f);
                rw.add((float) NUMOPS / LFCATTest("Real World Test", (int) Math.pow(2, i), 10, 100, realWorld));

                Ratio highContention = new Ratio(0.001f, 0.20f, 0.20f, 0.599f);
                hcnt.add((float) NUMOPS / LFCATTest("High Contention Test", (int) Math.pow(2, i), 10, 100, highContention));

                Ratio lowContention = new Ratio(0.20f, 0.05f, 0.05f, 0.70f);
                lcnt.add((float) NUMOPS / LFCATTest("Low Contention Test", (int) Math.pow(2, i), 10, 100, lowContention));

                Ratio highConflict = new Ratio(0.20f, 0.40f, 0.40f, 0.00f);
                hcnf.add((float) NUMOPS / LFCATTest("High Conflict Test", (int) Math.pow(2, i), 10, 100, highConflict));

                System.out.println("");


            } catch (Exception e) {
                System.out.println("ERROR: Unable to run test!!!");
            }
        }

        //Displays result of threads test
        XYChart chart = new XYChartBuilder().xAxisTitle("Threads").yAxisTitle("Throughput (ops/us)").width(600).height(400).build();
        chart.getStyler().setXAxisMin(1.0);
        chart.getStyler().setXAxisMax(40.0);
        chart.getStyler().setXAxisTickMarkSpacingHint(200);
        XYSeries series = chart.addSeries("Real World", x, rw);
        XYSeries series2 = chart.addSeries("High Contention", x, hcnt);
        XYSeries series3 = chart.addSeries("Low Contention", x, lcnt);
        XYSeries series4 = chart.addSeries("High Conflict", x, hcnf);
        chart.getStyler().setXAxisTickMarkSpacingHint(200);
        charts.add(chart);
        new SwingWrapper<XYChart>(charts).displayChartMatrix();

    }

    /***
     * Pregenerates a tree and splits the range values evenly since there is no contention adaptation.
     * @return
     */
    static LFCAT pregenerate()
    {
        LFCAT lfcat = new LFCAT();

        for(int i = 0 ;i < 1000; i++)
        {
            lfcat.insert(i);

            if ( i != 0 && i % 100 == 0)
            {
                lfcat.split(i);
            }
        }

        return lfcat;
    }


    /***
     * Runs the actual test with the given paramters
     * @param test Name of test
     * @param numThreads number of threads to run test with
     * @param treeSize max treeSize threshold for contention statistic
     * @param range Max range query size
     * @param ratios Ratio of operations to perform.
     * @return
     * @throws Exception
     */
    static Double LFCATTest(String test, int numThreads, int treeSize, int range,  Ratio ratios) throws Exception
    {

        //pre-populates tree
        LFCAT lfcat = pregenerate();

        Vector<Thread> threads = new Vector<Thread>();

        //goes through the given number of threads and instantiates test threads
        for(int i=0; i < numThreads; i++)
        {
            int threadops = NUMOPS / numThreads;

            if(i == numThreads - 1)
            {
                threadops += NUMOPS % numThreads;
            }

            LFCATTestThread r = new LFCATTestThread(threadops, lfcat, range, ratios);

            Thread t = new Thread(r);

            t.start();

            threads.add(t);
        }


        //Times total of all thread completions
        long startTime = System.nanoTime();

        //Starting gun that all threads are waiting on.
        LFCATTestThread.start = true;

        for(int i=0; i < numThreads; i++)
        {
            threads.elementAt(i).join();
        }

        long endTime = System.nanoTime();

        //Figure out how long all threads took
        double elapsedTime = ((double)(endTime - startTime) / (double)1000.0f);

        System.out.println(test + " - " + numThreads + " Threads : " +
                elapsedTime/(double) 1000000.0f +" seconds");
        System.out.flush();

        return elapsedTime;

    }


}