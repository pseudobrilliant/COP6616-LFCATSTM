package com.cop6616.testDriver;

import com.cop6616.lfcat.LFCAT;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Random;

public class LFCATTestThread implements Runnable
{
    private LFCAT lfcat;
    private int threadops = 0;
    private int heartbeat;
    private Random random;
    private final int maxRange = 1000;
    private ArrayDeque<Integer> actions;
    private ArrayDeque<Integer> toLookup;
    private ArrayDeque<Integer> toInsert;
    private ArrayDeque<Integer> toRemove;
    private ArrayDeque<Pair> toQuery;
    public static boolean start = false;

    public static class Pair
    {
        public final Integer a;
        public final Integer b;

        public Pair(Integer _a, Integer _b)
        {
            a = _a;
            b = _b;
        }
    }


    LFCATTestThread(int _threadops, LFCAT _lfcat, int range, Ratio targetRatios)
    {
        threadops = _threadops;
        heartbeat = threadops/4;
        lfcat = _lfcat;
        Ratio currentRatios = new Ratio();
        random = new Random();
        actions = new ArrayDeque<Integer>();
        toLookup = new ArrayDeque<Integer>();
        toInsert = new ArrayDeque<Integer>();
        toRemove = new ArrayDeque<Integer>();
        toQuery = new ArrayDeque<Pair>();

        ArrayList<Integer> possibleActions = new ArrayList<>();
        possibleActions.add(0);
        possibleActions.add(1);
        possibleActions.add(2);
        possibleActions.add(3);

        for(int i =0; i < threadops; i ++)
        {
            int rnd = possibleActions.get(random.nextInt(possibleActions.size()));


            if(rnd == 0)
            {
                actions.add(0);

                toLookup.push(random.nextInt(maxRange));

                currentRatios.UpdateContainsCount(1);
            }
            else
            if(rnd == 1)
            {
                actions.add(1);

                toInsert.push(random.nextInt(maxRange));

                currentRatios.UpdateInsertCount(1);
            }
            else
            if(rnd == 2)
            {
                actions.add(2);

                toRemove.push(random.nextInt(maxRange));

                currentRatios.UpdateRemoveCount(1);

            }
            else
            if(rnd == 3)
            {
                actions.add(3);

                Integer lowIndex = random.nextInt(maxRange / 2);
                Integer highIndex = lowIndex + random.nextInt( range);

                toQuery.push(new Pair(lowIndex,highIndex));

                currentRatios.UpdateRangeCount(1);
            }

            possibleActions.clear();

            if(currentRatios.containsOperations <= targetRatios.containsOperations)
            {
                possibleActions.add(0);
            }

            if(currentRatios.insertOperations <= targetRatios.insertOperations)
            {
                possibleActions.add(1);
            }

            if(currentRatios.removeOperations <= targetRatios.removeOperations)
            {
                possibleActions.add(2);
            }

            if(currentRatios.rangeOperations <= targetRatios.rangeOperations)
            {
                possibleActions.add(3);
            }
        }

    }

    public void run()
    {
        while(!start)
        {
            // Wait for all threads to be created before starting.
        }

        try {
            for (int i = 0; i < threadops; i++) {
                int action = actions.pop();

                if (action == 0) {
                    Integer val = toLookup.pop();
                    lfcat.contains(val);
                } else if (action == 1) {
                    Integer val = toInsert.pop();
                    lfcat.insert(val);
                } else if (action == 2) {
                    Integer val = toRemove.pop();
                    lfcat.remove(val);
                } else if (action == 3) {
                    Pair pair = toQuery.pop();
                    lfcat.range(pair.a, pair.b);
                }

            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
