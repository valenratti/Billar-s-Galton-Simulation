package utils;

import entity.Particle;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {
    public static double rand(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static long factorial(int n) {
        long fact = 1;

        for (int i = 2; i <= n; i++)
            fact = fact * i;

        return fact;
    }
}
