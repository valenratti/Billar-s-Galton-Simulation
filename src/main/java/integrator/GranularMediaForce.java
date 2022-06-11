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
    private double gamma;

    public GranularMediaForce(Particle particle, NeighbourWrapper neighbourWrapper) {
        this.particle = particle;
        this.neighbourWrapper = neighbourWrapper;
        this.kn = 1e+4;//N/m
        this.kt = 2 * kn; //N/m
        this.gamma = 100; //kg/s
    }

    @Override
    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    @Override
    public Pair getForce() {
        Pair forceFromParticles = forceFromParticles(neighbourWrapper.getParticles());
        Pair forceFromWalls = forceFromWalls(neighbourWrapper.getWalls());
        Pair forceFromObstacles = forceFromObstacles(neighbourWrapper.getObstacles());
        Pair gravityForce = new Pair(0.0,-9.8 * particle.getMass());
        return forceFromParticles.add(forceFromWalls).add(forceFromObstacles).add(gravityForce);
    }

    Pair forceFromParticles(List<Particle> particles){
        Pair force = new Pair(0.0, 0.0);
        for(Particle neighbour : particles) {
            double overlapSize = Entity.overlap(particle, neighbour);
            if(overlapSize > 0){
                double tangencialRelativeVelocity = particle.getTangencialRelativeVelocity(neighbour);
                double normalForce = -kn * overlapSize - gamma * Entity.overlapD1(particle, neighbour); //TODO: Agregar nueva resta de la ecuacion
                double tangencialForce = -kt * overlapSize * tangencialRelativeVelocity;
                double distance = Entity.distance(particle, neighbour);
                double normalizedXDistance = (neighbour.getX() - particle.getX()) / distance;
                double normalizedYDistance = (neighbour.getY() - particle.getY()) / distance;
                force.add(new Pair(normalForce * normalizedXDistance + tangencialForce * -1 * normalizedYDistance,
                        normalForce * normalizedYDistance + tangencialForce * normalizedXDistance));
            }
        }
        return force;
    }

    Pair forceFromWalls(List<Wall> walls){
        Pair force = new Pair(0.0, 0.0);
        for(Wall wall : walls) {
            double overlap = Entity.overlap(particle, wall);
            double ovelapD1 = Entity.overlapD1(particle,wall);
            double relativeVelocity = particle.getTangencialRelativeVelocity(wall);
            return force.add(new Pair(-kn *overlap- gamma *ovelapD1, -kt*overlap*relativeVelocity));
        }
        return force;
    }

    Pair forceFromObstacles(List<Obstacle> obstacles){
        Pair force = new Pair(0.0, 0.0);
        for(Obstacle neighbour : obstacles) {
            double overlapSize = Entity.overlap(particle, neighbour);
            if(overlapSize > 0){
                double tangencialRelativeVelocity = particle.getTangencialRelativeVelocity(neighbour);
                double overlapD1 = Entity.overlapD1(particle, neighbour);
                double normalForce = -kn * overlapSize - gamma * overlapD1; //TODO: Agregar nueva resta de la ecuacion
                double tangencialForce = -kt * overlapSize * tangencialRelativeVelocity;
                double distance = Entity.distance(particle, neighbour);
                double normalizedXDistance = (neighbour.getX() - particle.getX()) / distance;
                double normalizedYDistance = (neighbour.getY() - particle.getY()) / distance;
                Pair toAdd = new Pair(normalForce * normalizedXDistance + tangencialForce * -1 * normalizedYDistance,
                        normalForce * normalizedYDistance + tangencialForce * normalizedXDistance);
                force.add(toAdd);
            }
        }
        return force;
    }
}
