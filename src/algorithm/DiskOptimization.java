package algorithm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.Arrays;
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
        //call the method
        generateFCFS();
        generateSSTF();
        generateSCAN();
        generateCSCAN();
        generateLOOK();
    }

    public void printSequence(String name, int location[])
    {
        //output for the disk optimization
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
    public void generateSCAN(){
        int location[] = arrangeBySCAN(dp.getPrevious(), dp.getCurrent(), dp.getSequence(), dp.getCylinders());
        printSequence("SCAN", location);
    }
    //generate CSCAN
    public void generateCSCAN(){
        int location[] = arrangeByCSCAN(dp.getPrevious(), dp.getCurrent(), dp.getSequence(), dp.getCylinders());
        printSequence("CSCAN", location);
    }

    //generate LOOK
    public void generateLOOK(){
        int location[] = arrangeByLOOK(dp.getPrevious(), dp.getCurrent(), dp.getSequence(), dp.getCylinders());
        printSequence("LOOK", location);
    }

    //arange bt sstf
    private int[] arrangeBySSTF(int current, int sequence[])
    {
        int n = sequence.length;

        //create array
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

    private int[] arrangeBySCAN(int previous, int current, int sequence[], int cylinders) {
        int s = sequence.length;

        //create 2 arrayList
        ArrayList<Integer> accessOrder = new ArrayList<>();
        ArrayList<Integer> tmpSequence = new ArrayList<>();

        //define the max and min
        int max = cylinders -1;
        int min = 0;
        //add sequence values to tmpSequence
        for (int i = 0; i < s; i++) {
            tmpSequence.add(sequence[i]);
        }
        //add current to the tmpSequence end ArrayList
        tmpSequence.add(current);
        //Sort tmpSequence (ascending order)
        Collections.sort(tmpSequence);

        //if previous > current , it will moves towards the min
        if (previous > current) {
            //goes back by 1 index and inserts values into accessOrder
            for (int i = tmpSequence.indexOf(current) - 1; i >= 0; i--) {
                accessOrder.add(tmpSequence.get(i));
            }
            //add min in accessOrder
            accessOrder.add(min);

            //add the remaining values into the accessOrder
            for (int i = tmpSequence.indexOf(current) + 1; i < tmpSequence.size(); i++) {
               accessOrder.add(tmpSequence.get(i));
           }

        }
        //if previous < current, it will moves towards the max
        else {
            //add values to accessOrder starting from current + 1 (second index) to the end of the arrayList
            for (int i = tmpSequence.indexOf(current) + 1; i < tmpSequence.size(); i++) {
                accessOrder.add(tmpSequence.get(i));
            }
            //add max in the accessOrder
            accessOrder.add(max);

            //then add the remaining values in the accessOrder
            for (int i = tmpSequence.indexOf(current) - 1; i >= 0; i--) {
                accessOrder.add(tmpSequence.get(i));
            }
        }
        //put accessOrder into an array as output is set to an array
        int[] scan = new int[accessOrder.size()];
        for (int i = 0; i < accessOrder.size(); i++) {
            scan[i] = accessOrder.get(i);
        }
        return scan;
    }


    private int[] arrangeByCSCAN(int previous, int current, int sequence[], int cylinders) {
        int s = sequence.length;
        ArrayList<Integer> accessOrder = new ArrayList<>();
        ArrayList<Integer> tmpSequence = new ArrayList<>();
        int max = cylinders -1;
        int min = 0;

        for (int i = 0; i < s; i++) {
            tmpSequence.add(sequence[i]);
        }

        tmpSequence.add(current);

        Collections.sort(tmpSequence);


        if (previous > current) {

            for (int i = tmpSequence.indexOf(current) -1; i >= 0; i--) {
                accessOrder.add(tmpSequence.get(i));
            }

            accessOrder.add(min);
            accessOrder.add(max);

            for (int i = tmpSequence.indexOf(current) + 1; i < tmpSequence.size(); i++) {
                accessOrder.add(tmpSequence.get(i));
            }
        }
        //if previous < current - movement will be towards left(towards max)
        else {
            //add values to accessOrder starting from current (will be in the first index) to the end of arrayList
            for (int i = tmpSequence.indexOf(current) + 1; i < tmpSequence.size(); i++) {
                accessOrder.add(tmpSequence.get(i));
            }
            //add max as it is not inside the arrayList yet
            accessOrder.add(max);
            accessOrder.add(min);
            //add the remaining values to arrayList
            //basically goes back by 1 index
            //for loop ends when i reaches 0 and has been inserted to accessOrder
            for (int i = tmpSequence.indexOf(current) - 1; i >= 0; i--) {
                accessOrder.add(tmpSequence.get(i));
            }
        }
        //put accessOrder into an array bc output is set to be an array
        int[] cScan = new int[accessOrder.size()];
        for (int i = 0; i < accessOrder.size(); i++) {
            cScan[i] = accessOrder.get(i);
        }
        return cScan;
    }

    //LOOK
    private int[] arrangeByLOOK(int previous, int current, int sequence[], int cylinders) {
        int s = sequence.length;
        ArrayList<Integer> accessOrder = new ArrayList<>();
        ArrayList<Integer> tmpSequence = new ArrayList<>();
        int max = cylinders -1;
        int min = 0;

        for (int i = 0; i < s; i++) {
            tmpSequence.add(sequence[i]);
        }

        tmpSequence.add(current);

        Collections.sort(tmpSequence);


        if (previous > current) {

            //add values to accessOrder starting from current - 1
            for (int i = tmpSequence.indexOf(current) - 1; i >= 0; i--) {
                accessOrder.add(tmpSequence.get(i));
            }

            //add the remaining values in the arrayList
            for (int i = tmpSequence.indexOf(current) + 1; i < tmpSequence.size(); i++) {
                accessOrder.add(tmpSequence.get(i));
            }
        }
        //if previous < current - movement will be towards left(towards max)
        else {
            //add values to accessOrder starting from current (will be in the first index) to the end of arrayList
            for (int i = tmpSequence.indexOf(current) +1; i < tmpSequence.size(); i++) {
                accessOrder.add(tmpSequence.get(i));
            }
            //add max as it is not inside the arrayList yet
            //add the remaining values to arrayList
            //basically goes back by 1 index
            //for loop ends when i reaches 0 and has been inserted to accessOrder
            for (int i = tmpSequence.indexOf(current) - 1; i >= 0; i--) {
                accessOrder.add(tmpSequence.get(i));
            }

        }
        //put accessOrder into an array bc output is set to be an array
        int[] look = new int[accessOrder.size()];
        for (int i = 0; i < accessOrder.size(); i++) {
            look[i] = accessOrder.get(i);
        }
        return look;
    }
}


