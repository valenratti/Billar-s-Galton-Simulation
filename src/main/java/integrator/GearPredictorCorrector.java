package integrator;

import entity.Particle;
import utils.Pair;
import utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*  order 5
    r = position
    r1 = velocity
    r2 = acceleration
    r3 = r2'
    r4 = r3'
    r5 = r4'
*/
public class GearPredictorCorrector implements Integrator {
    private double dt;
    private System system;

    public GearPredictorCorrector(double dt, System system) {
        this.dt = dt;
        this.system = system;
    }

    @Override
    public void nextStep(Particle particle) {
        final double m = particle.getMass();
        final Pair f = system.getForce();
        final Pair fD1 = system.getForceD1();
        final Pair fD2 = system.getForceD2();
        final Pair fD3 = system.getForceD3();

        final double rx = particle.getX(), ry = particle.getY();
        final double r1x = particle.getVx(), r1y = particle.getVy();
        final double r2x = f.getX() / m, r2y = f.getY() / m;
        final double r3x = fD1.getX() / m, r3y = fD1.getY() / m;
        final double r4x = fD2.getX() / m, r4y = fD2.getY() / m;
        final double r5x = fD3.getX() / m, r5y = fD3.getY() / m;

        final List<Double> rxList = new ArrayList<>(Arrays.asList(rx, r1x, r2x, r3x, r4x, r5x));
        final List<Double> ryList = new ArrayList<>(Arrays.asList(ry, r1y, r2y, r3y, r4y, r5y));

        // predicted position
        final double predictedRx = taylor(rxList);
        final double predictedRy = taylor(ryList);

        rxList.remove(0); // remove rx
        ryList.remove(0); // remove ry

        // predicted velocity
        final double predictedR1x = taylor(rxList);
        final double predictedR1y = taylor(ryList);

        rxList.remove(0); // remove rx1
        ryList.remove(0); // remove ry1

        // predicted acceleration
        final double predictedR2x = taylor(rxList);
        final double predictedR2y = taylor(ryList);

        final Particle auxParticle = new Particle(
                predictedRx, predictedRy, predictedR1x, predictedR1y, m, particle.getCharge(), true);
        final Pair auxF = system.getForce();

        final double ax = auxF.getX() / m, ay = auxF.getY() / m;
        final double deltaR2x = (ax - predictedR2x) * taylorAuxCalculus(2);
        final double deltaR2y = (ay - predictedR2y) * taylorAuxCalculus(2);

        final double correctedRx = predictedRx + getAlpha(0) * deltaR2x;
        final double correctedRy = predictedRy + getAlpha(0) * deltaR2y;

        final double correctedR1x = predictedR1x + getAlpha(1) * deltaR2x / dt;
        final double correctedR1y = predictedR1y + getAlpha(1) * deltaR2y / dt;

        particle.updateState(correctedRx, correctedRy, correctedR1x, correctedR1y);
    }

    private double taylor(List<Double> rList) {
        double ans = 0;
        int n = 0;

        for(double ri : rList)
            ans += ri * taylorAuxCalculus(n++);

        return ans;
    }

    private double taylorAuxCalculus(int n) {
        return Math.pow(dt, n) / Utils.factorial(n);
    }

    private double getAlpha(int i) {
        switch(i) {
            case 0:
                return system.isForceVelocityDependent() ? (double) 3 / 16 : (double) 3 / 20;
            case 1:
                return (double) 251 / 360;
            default:
                throw new RuntimeException("GPC: Illegal alpha");
        }
    }
}
