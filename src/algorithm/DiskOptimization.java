package algorithm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Collections;
import java.lang.Integer;

public class DiskOptimization {
    Properties p = new Properties();
    DiskParameter dp = null;

    public static void main (String args[]) {
        new DiskOptimization("diskq1.properties");
    }

    public DiskOptimization(String filename) {
        try
        {
            p.load(new BufferedReader(new FileReader(filename)));
            dp = new DiskParameter(p);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        generateAnalysis();
    }

    public void generateAnalysis()
    {
        generateFCFS();
        generateSSTF();
        //generateSCAN();
    }

    public void printSequence(String name, int location[])
    {
        String sequence = "";
        String working1 = "";
        String working2 = "";
        int total = 0;
        sequence += dp.getCurrent();
        int previous = dp.getCurrent();
        for (int i = 0; i < location.length; i++)
        {
            int current = location[i];
            sequence += "," + current;
            int d = Math.abs(previous-current);

            working1 += "|" + previous + "-" + current + "|+";
            working2 += d + " + ";
            total += d;
            previous = current;
        }

        System.out.println(name+'\n'+"====");
        System.out.println("Order of Access: " +sequence);
        System.out.println("Total Distance = " +
                working1.substring(0, working1.length()-1));
        System.out.println("                = " +
                working2.substring(0, working2.length()-2));
        System.out.println("                = " +
                total + '\n');


    }

    public void generateFCFS()
    {
        int location[] = dp.getSequence();
        printSequence("FCFS", location);
    }


    public void generateSSTF()
    {
        int location[] = arrangeBySSTF(dp.getCurrent(), dp.getSequence());
        printSequence("SSTF", location);
    }

    //generate SCAN
    //public void generateSCAN(){
    //    int location[] = arrangeBySCAN(dp.getPrevious(), dp.getCurrent(), dp.getSequence(), dp.getCylinders());
    //    printSequence("SCAN", location);
    //}

    //arange bt sstf
    private int[] arrangeBySSTF(int current, int sequence[])
    {
        int n = sequence.length;
        int sstf[] = new int[n];
        for (int i = 0; i < n; i++)
        {
            sstf[i] = sequence[i];

        }
        int ii = -1;
        for (int i = 0; i < n; i++)
        {
            int minimum = Integer.MAX_VALUE;
            ii = i;
            for (int j = i; j < n; j++)
            {
                int distance = Math.abs(current - sstf[j]);
                if (distance < minimum)
                {
                    ii = j;
                    minimum = distance;
                }
            }
            int tmp = sstf[i];
            sstf[i] = sstf[ii];
            sstf[ii] = tmp;
            current = sstf[i];

        }
        return sstf;
    }


}


