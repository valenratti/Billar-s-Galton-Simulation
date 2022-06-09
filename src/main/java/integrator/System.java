package integrator;

import utils.Pair;

public interface System {
    Pair getForce();
    Pair getForceD1();
    Pair getForceD2();
    Pair getForceD3();
    Pair getForceD4();
}
