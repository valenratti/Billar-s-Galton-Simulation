import cell_index_method.CIMConfig;
import cell_index_method.CellIndexMethod;
import cell_index_method.NeighbourWrapper;
import entity.Particle;
import integrator.Beeman;
import integrator.GranularMediaForce;
import i_o.FileWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Simulator {

    public static void ej() throws IOException {

        List<Integer> NList = List.of(1001);
        for(Integer n : NList) {
            for(int i=4; i<5; i++) {
                simulate(n, i);
                FileWriter.reset();
            }
        }
    }


    public static void simulate(Integer particlesN, int run) throws IOException {
        int aux = 0;
        double dt = 5e-5;
        boolean finishedSimulation = false;
        double time = 0;
        CIMConfig config = new CIMConfig(0.8, 1.2, particlesN, 0.00, 0.006, 10.0);
        CellIndexMethod cellIndexMethod = new CellIndexMethod(config);

        while(!finishedSimulation){
            if(time!= 0d){
                cellIndexMethod.updateParticles();
            }
            Map<Particle, NeighbourWrapper> neighbourWrapperMap = cellIndexMethod.calculateNeighbours();
            for(Particle particle : cellIndexMethod.getParticles().stream().filter((particle) -> !particle.isFixed()).collect(Collectors.toList())){
                GranularMediaForce granularMediaForce = new GranularMediaForce(particle, neighbourWrapperMap.getOrDefault(particle, new NeighbourWrapper()));
                Beeman beeman = new Beeman(dt, granularMediaForce);
                beeman.nextStep(particle);
                if(cellIndexMethod.getParticles().stream().filter((current) -> !current.isFixed()).allMatch(Particle::isReachedBin)){
                    finishedSimulation = true;
                }
            }
            time+=dt;
//            System.out.println(time);
            cellIndexMethod.clear();
            aux++;
            if(aux == 50 && run == 4){
                FileWriter.printPositions(new NeighbourWrapper(cellIndexMethod.getParticles(), cellIndexMethod.getWalls(), cellIndexMethod.getObstacles()), config.getTotalParticles());
                aux = 0;
            }
        }
        FileWriter.finalCsv(cellIndexMethod.getParticles(), config.getTotalParticles(), run);
    }



}
