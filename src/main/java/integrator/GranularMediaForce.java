package integrator;

import cell_index_method.NeighbourWrapper;
import entity.Particle;
import utils.Pair;

public class GranularMediaForce implements System {

    private Particle particle;
    private NeighbourWrapper neighbourWrapper;

    @Override
    public Pair getForce() {
        //given a particle and its neighbours,
        //we can calculate the force
        return null;
    }

    @Override
    public Pair getForceD1(){

    }

    @Override
    public Pair getForceD2() {
        return null;
    }

    @Override
    public Pair getForceD3() {
        return null;
    }

    @Override
    public Pair getForceD4() {
        return null;
    }
}
