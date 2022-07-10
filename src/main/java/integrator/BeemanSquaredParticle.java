package integrator;

import entity.SquaredParticle;
import utils.Pair;

public class BeemanSquaredParticle /*implements Integrator*/ {
    private double dt;
    private boolean isForceVelocityDependent;
    private static Pair fPrev; // initially null
    private GranularMediaForceSquaredParticle forceCalculator;

    public BeemanSquaredParticle(double dt, GranularMediaForceSquaredParticle forceCalculator) {
        this.dt = dt;
        this.isForceVelocityDependent = true;
        this.forceCalculator = forceCalculator;
    }

    // receives the force value of the previous state of the particle (fPrev) and updates it
    // since this integrator needs the acceleration of the previous state
    public void nextStep(final SquaredParticle particle) {
        final Pair f = forceCalculator.getForce();
        final double m = particle.getMass();
        final double ax = f.getX() / m, ay = f.getY() / m;
        final double vx = particle.getVx(), vy = particle.getVy();
        final double rx = particle.getX(), ry = particle.getY();

        if(fPrev == null) {  // first step
            // estimamos las posiciones y velocidades anteriores con Euler evaluado en -dt
            SquaredParticle eulerParticle = euler(particle, -dt);
            fPrev = new Pair(eulerParticle.getX(), eulerParticle.getY());
        }

        final double axPrev = fPrev.getX() / m, ayPrev = fPrev.getY() / m;

        final double rxNext = nextPosition(rx, vx, ax, axPrev);
        final double ryNext = nextPosition(ry, vy, ay, ayPrev);

        double auxVx = vx, auxVy = vy;

        if(isForceVelocityDependent) {
            auxVx = predictVelocity(vx, ax, axPrev);
            auxVy = predictVelocity(vy, ay, ayPrev);
        }

        final SquaredParticle auxParticle = new SquaredParticle(rxNext, ryNext, auxVx, auxVy, m, particle.getSideLength(), true, false);
        forceCalculator.setParticle(auxParticle);
        final Pair fNext = forceCalculator.getForce();

        double vxNext = nextVelocity(vx, ax, axPrev, fNext.getX() / m);
        double vyNext = nextVelocity(vy, ay, ayPrev, fNext.getY() / m);
        if(rxNext<0 || rxNext > 0.3){
            java.lang.System.out.println("here");
        }

        particle.updateState(rxNext, ryNext, vxNext, vyNext);
        fPrev = f;

        // TODO: Torque
    }

    private double nextPosition(double r, double v, double a, double aPrev) {
        final double dtSquared = dt * dt;

        return r + v * dt + (double) 2/3 * a * dtSquared - (double) 1/6 * aPrev * dtSquared;
    }

    private double predictVelocity(double v, double a, double aPrev) {
        return v + (double) 3/2 * a * dt - (double) 1/2 * aPrev * dt;
    }

    private double nextVelocity(double v, double a, double aPrev, double aNext) {
        return v + (double) 1/3 * aNext * dt + (double) 5/6 * a * dt - (double) 1/6 * aPrev * dt;
    }

    private SquaredParticle euler(SquaredParticle particle, double dt){
        final Pair f0 = forceCalculator.getForce();
        final double m = particle.getMass();
        final double ax0 = f0.getX() / m, ay0 = f0.getY();
        final double vx0 = particle.getVx(), vy0 = particle.getVy();

        final double rx = particle.getX() + dt * vx0 + dt * dt / 2 * ax0;
        final double ry = particle.getY() + dt * vy0 + dt * dt / 2 * ay0;

        final double vx = vx0 + dt * ax0;
        final double vy = vy0 + dt * ay0;

        return new SquaredParticle(rx, ry, vx, vy, m, particle.getSideLength(), true, false);
    }
}
