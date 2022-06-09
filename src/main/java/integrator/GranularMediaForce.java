package integrator;

import cell_index_method.NeighbourWrapper;
import entity.Obstacle;
import entity.Particle;
import entity.Wall;
import utils.Pair;

import java.util.List;

public class GranularMediaForce implements System {

    private Particle particle;
    private NeighbourWrapper neighbourWrapper;

    public GranularMediaForce(Particle particle, NeighbourWrapper neighbourWrapper) {
        this.particle = particle;
        this.neighbourWrapper = neighbourWrapper;
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
        return new Pair(0.0, 0.0);
    }

    Pair forceFromWalls(List<Wall> walls){
        return new Pair(0.0, 0.0);
    }

    Pair forceFromObstacles(List<Obstacle> obstacles){
        return new Pair(0.0, 0.0);
    }
}
