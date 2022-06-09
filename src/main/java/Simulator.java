import cell_index_method.CIMConfig;
import cell_index_method.CellIndexMethod;
import cell_index_method.NeighbourWrapper;
import entity.Particle;
import integrator.GearPredictorCorrector;
import integrator.GranularMediaForce;

import java.util.Map;
import java.util.stream.Collectors;

public class Simulator {


    public static void simulate(){
        double dt = 5e-5;
        boolean finishedSimulation = false;
        double time = 0;
        CIMConfig config = new CIMConfig(80.0, 120.0, 10, 0.3, 0.3, 10.0);
        CellIndexMethod cellIndexMethod = new CellIndexMethod(config);

        while(!finishedSimulation){
            Map<Particle, NeighbourWrapper> neighbourWrapperMap = cellIndexMethod.calculateNeighbours();
            for(Particle particle : cellIndexMethod.getParticles().stream().filter((particle) -> !particle.isFixed()).collect(Collectors.toList())){
                GranularMediaForce granularMediaForce = new GranularMediaForce(particle, neighbourWrapperMap.getOrDefault(particle, new NeighbourWrapper()));
                GearPredictorCorrector gearPredictorCorrector = new GearPredictorCorrector(dt, granularMediaForce);
                gearPredictorCorrector.nextStep(particle);
                if(particle.getY() <= -10.0){
                    finishedSimulation = true;
                }
            }
        }
    }



}
