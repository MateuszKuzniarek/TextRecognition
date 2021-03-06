package logic.metrics;

import java.util.List;

public class MinMaxSimilarity extends Similiarity
{
    @Override
    public double getSimilarity(List<Double> point1, List<Double> point2)
    {
        double sumOfMins = 0;
        double sumOfMaxs = 0;
        for(int i=0; i<point1.size(); i++)
        {
            sumOfMins += Math.min(point1.get(i), point2.get(i));
            sumOfMaxs += Math.max(point1.get(i), point2.get(i));
        }
        return sumOfMins/sumOfMaxs;
    }

    @Override
    public String toString() {
        return "Miara minimum-maximum";
    }
}
