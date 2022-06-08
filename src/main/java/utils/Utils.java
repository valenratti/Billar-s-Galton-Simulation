package utils;

import entity.Particle;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {
    public static double rand(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static double calculateSystemKineticEnergy(List<Particle> particleList) {
        return particleList.stream().mapToDouble(Particle::getKineticEnergy).sum();
    }
}
