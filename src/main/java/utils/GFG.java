package utils;

// https://www.geeksforgeeks.org/minimum-distance-from-a-point-to-the-line-segment-using-vectors/
// https://www.geeksforgeeks.org/find-points-at-a-given-distance-on-a-line-of-given-slope/

import java.util.Arrays;
import java.util.List;

// Java implementation of the approach
public class GFG
{
    public static class pair
    {
        double x, y;
        public pair(double x, double y)
        {
            this.x = x;
            this.y = y;
        }
        public pair() {
        }
    }

    // Function to return the minimum distance
// between a line segment AB and a point E
    public static double minDistance(Pair p1, Pair p2, Pair p3)
    {
        pair A = new pair(p1.getX(), p1.getY());
        pair B = new pair(p2.getX(), p2.getY());
        pair E = new pair(p3.getX(), p3.getY());

        // vector AB
        pair AB = new pair();
        AB.x = B.x - A.x;
        AB.y = B.y - A.y;

        // vector BP
        pair BE = new pair();
        BE.x = E.x - B.x;
        BE.y = E.y - B.y;

        // vector AP
        pair AE = new pair();
        AE.x = E.x - A.x;
        AE.y = E.y - A.y;

        // Variables to store dot product
        double AB_BE, AB_AE;

        // Calculating the dot product
        AB_BE = (AB.x * BE.x + AB.y * BE.y);
        AB_AE = (AB.x * AE.x + AB.y * AE.y);

        // Minimum distance from
        // point E to the line segment
        double reqAns = 0;

        // Case 1
        if (AB_BE > 0)
        {

            // Finding the magnitude
            double y = E.y - B.y;
            double x = E.x - B.x;
            reqAns = Math.sqrt(x * x + y * y);
        }

        // Case 2
        else if (AB_AE < 0)
        {
            double y = E.y - A.y;
            double x = E.x - A.x;
            reqAns = Math.sqrt(x * x + y * y);
        }

        // Case 3
        else
        {

            // Finding the perpendicular distance
            double x1 = AB.x;
            double y1 = AB.y;
            double x2 = AE.x;
            double y2 = AE.y;
            double mod = Math.sqrt(x1 * x1 + y1 * y1);
            reqAns = Math.abs(x1 * y2 - y1 * x2) / mod;
        }
        return reqAns;
    }

// Function to print pair of points at
// distance 'l' and having a slope 'm'
// from the source
    public static List<Pair> getPoints(Pair s, float l, int m) {
        // m is the slope of line, and the
        // required Point lies distance l
        // away from the source Point
        pair a = new pair();
        pair b = new pair();
        pair source = new pair(s.getX(), s.getY());

        // Slope is 0
        if (m == 0)
        {
            a.x = source.x + l;
            a.y = source.y;

            b.x = source.x - l;
            b.y = source.y;
        }

        // If slope is infinite
        else if (Double.isInfinite(m))
        {
            a.x = source.x;
            a.y = source.y + l;

            b.x = source.x;
            b.y = source.y - l;
        }
        else
        {
            float dx = (float)(l / Math.sqrt(1 + (m * m)));
            float dy = m * dx;
            a.x = source.x + dx;
            a.y = source.y + dy;
            b.x = source.x - dx;
            b.y = source.y - dy;
        }

        return Arrays.asList(new Pair(a.x, a.y), new Pair(b.x, b.y));
    }

    public static Pair getPoint(Pair p1, Pair p2, double d) {
        double L = Utils.distance(p1, p2);
        double x = p1.getX() + (p2.getX() - p1.getX()) * d/L;
        double y = p1.getY() + (p2.getY() - p1.getY()) * d/L;

        return new Pair(x, y);
    }

    // Driver code
    public static void main(String[] args)
    {
        Pair A = new Pair(0, 0);
        Pair B = new Pair(2, 0);
        Pair E = new Pair(1, 0.2);

        System.out.print(minDistance(A, B, E));
    }
}

// This code is contributed by 29AjayKumar
