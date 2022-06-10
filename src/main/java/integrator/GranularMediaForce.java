package integrator;

import cell_index_method.NeighbourWrapper;
import entity.Entity;
import entity.Obstacle;
import entity.Particle;
import entity.Wall;
import utils.Pair;

import java.util.List;

public class GranularMediaForce implements System {

    private Particle particle;
    private NeighbourWrapper neighbourWrapper;
    private double kn;
    private double kt;
    private double lambda;

    public GranularMediaForce(Particle particle, NeighbourWrapper neighbourWrapper) {
        this.particle = particle;
        this.neighbourWrapper = neighbourWrapper;
        this.kn = 1e+5;//N/m
        this.kt = 2 * kn; //N/m
        this.lambda = 100.0; //kg/s
    }

    @Override
    public Pair getForce() {
        Pair forceFromParticles = forceFromParticles(neighbourWrapper.getParticles());
        Pair forceFromWalls = forceFromWalls(neighbourWrapper.getWalls());
        Pair forceFromObstacles = forceFromObstacles(neighbourWrapper.getObstacles());
        Pair gravityForce = new Pair(0.0,-9.8 * particle.getMass());
        return forceFromParticles.add(forceFromWalls).add(forceFromObstacles).add(gravityForce);
    }

    @Override
    public Pair getForceD1(){
        return null;
    }

    @Override
    public Pair getForceD2() {
        return null;
    }

    @Override
    public Pair getForceD3() {
        return null;
    }

    Pair forceFromParticles(List<Particle> particles){
        Pair force = new Pair(0.0, 0.0);
        for(Particle neighbour : particles) {
            double overlapSize = Entity.overlap(particle, neighbour);
            if(overlapSize > 0){
                double tangencialRelativeVelocity = particle.getTangencialRelativeVelocity(neighbour);
                double normalForce = -kn * overlapSize - lambda * Entity.overlapD1(particle, neighbour); //TODO: Agregar nueva resta de la ecuacion
                double tangencialForce = -kt * overlapSize * tangencialRelativeVelocity;
                double distance = Entity.distance(particle, neighbour);
                double normalizedXDistance = (neighbour.getX() - particle.getX()) / distance;
                double normalizedYDistance = (neighbour.getY() - particle.getY()) / distance;
                force.add(new Pair(normalForce * normalizedXDistance + tangencialForce * -1 * normalizedYDistance,
                        normalForce * normalizedYDistance + tangencialForce * normalizedXDistance));
            }
        }
        return new Pair(0.0, 0.0);
    }

    Pair forceFromParticlesD1(List<Particle> particles) {
        Pair force = new Pair(0.0, 0.0);
        for(Particle neighbour : particles) {
//            double overlapSize = Entity.overlap(particle, neighbour);
            double overlapD1 = Entity.overlapD1(particle, neighbour);
            double relativeAccelerationTangencial = 0.0;
            double normalForce = -kn * overlapD1 - lambda * Entity.overlapD2(particle, neighbour);
            double tangencialForce = -kt * overlapD1 * relativeAccelerationTangencial;
            double distance = Entity.distance(particle, neighbour);
            double relativeVelocityModule = particle.getRelativeVelocityModule(neighbour);
            double normalizedXDistance = - (neighbour.getX() - particle.getX()) * relativeVelocityModule / Math.pow(distance,2);
            double normalizedYDistance = (neighbour.getY() - particle.getY()) * relativeVelocityModule / Math.pow(distance,2);
            force.add(new Pair(normalForce * normalizedXDistance + tangencialForce * -1 * normalizedYDistance,
                    normalForce * normalizedYDistance + tangencialForce * normalizedXDistance));
        }
        return force;
    }

    Pair forceFromWalls(List<Wall> walls){
        return new Pair(0.0, 0.0);
    }

    Pair forceFromObstacles(List<Obstacle> obstacles){
        return new Pair(0.0, 0.0);
    }
}
