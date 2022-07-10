package integrator;

import cell_index_method.NeighbourWrapperSquaredParticle;
import entity.Entity;
import entity.Particle;
import entity.SquaredParticle;
import entity.Wall;
import utils.Pair;

import java.util.List;

public class GranularMediaForceSquaredParticle /*implements System*/ {

    private SquaredParticle particle;
    private NeighbourWrapperSquaredParticle neighbourWrapper;
    private final double kn;
    private final double kt;
    private final double gamma;

    public GranularMediaForceSquaredParticle(SquaredParticle particle, NeighbourWrapperSquaredParticle neighbourWrapper) {
        this.particle = particle;
        this.neighbourWrapper = neighbourWrapper;
        this.kn = 1e+4;//N/m
        this.kt = 2 * kn; //N/m
        this.gamma = 10; //kg/s
    }

    public void setParticle(SquaredParticle particle) {
        this.particle = particle;
    }

    Pair getForce() {
        Pair forceFromParticles = forceFromParticles(neighbourWrapper.getParticles());
        Pair forceFromWalls = forceFromWalls(neighbourWrapper.getWalls());
        Pair gravityForce = new Pair(0.0,-9.8 * particle.getMass());
        return forceFromParticles.add(forceFromWalls).add(gravityForce);
    }

    private Pair forceFromParticles(List<SquaredParticle> particles){
        // TODO: CHECK
        Pair force = new Pair(0.0, 0.0);
        for(SquaredParticle neighbour : particles) {
            double overlapSize = Entity.overlap(particle, neighbour);
            if(overlapSize > 0) {
                double tangencialRelativeVelocity = particle.getTangencialRelativeVelocity(neighbour);
                double normalForce = -kn * overlapSize;
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

    private Pair forceFromWalls(List<Wall> walls){
        Pair force = new Pair(0.0, 0.0);
        for(Wall wall : walls) {
            double overlap = Entity.overlap(particle, wall);
            double overlapD1 = Entity.overlapD1(particle,wall);
            double relativeVelocity = particle.getTangencialRelativeVelocity(wall);
            double normal = -kn * overlap - gamma * overlapD1;
            double tangencial = -kt*overlap*relativeVelocity;
            switch (wall.getWallType()) {
                case TOP_WALL:
                    force.add(new Pair(tangencial, normal));
                    break;
                case BOTTOM_WALL:
                    force.add(new Pair(-tangencial, -normal));
                    break;
                case RIGHT_AREA_WALL:
                    force.add(new Pair(normal, -tangencial));
                    break;
                case LEFT_AREA_WALL:
                    force.add(new Pair(-normal, tangencial));
                    break;
            }

        }
        return force;
    }

}
