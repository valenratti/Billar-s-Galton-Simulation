import cell_index_method.*;
import entity.Particle;
import entity.SquaredParticle;
import integrator.Beeman;
import integrator.BeemanSquaredParticle;
import integrator.GranularMediaForce;
import i_o.FileWriter;
import integrator.GranularMediaForceSquaredParticle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Simulator {

    public static void simulateSquared(Integer particlesN, int run, double openWidth) throws IOException {
        int aux = 0;
        double dt = 5e-5;
        double time = 0, finishTime = 10;
        CIMConfig config = new CIMConfig(1.0, 0.3, particlesN, 7.07 * 10e-3, 0.0106, 10.0, openWidth);
        CellIndexMethodSquaredParticles cellIndexMethod = new CellIndexMethodSquaredParticles(config);
        while(time < finishTime) {  // TODO: Ver criterio de corte
            if(time!= 0d)
                cellIndexMethod.updateParticles();

            Map<SquaredParticle, NeighbourWrapperSquaredParticle> neighbourWrapperMap = cellIndexMethod.calculateNeighbours();
            for(SquaredParticle particle : cellIndexMethod.getParticles().stream().filter((particle) -> !particle.isFixed()).collect(Collectors.toList())){
                GranularMediaForceSquaredParticle granularMediaForce = new GranularMediaForceSquaredParticle(particle, neighbourWrapperMap.getOrDefault(particle, new NeighbourWrapperSquaredParticle()));
                BeemanSquaredParticle beeman = new BeemanSquaredParticle(dt, granularMediaForce);
                beeman.nextStep(particle);
            }
            time += dt;
//            System.out.println(time);
            cellIndexMethod.clear();
            aux++;
            if(aux == 50 && run == 4) {
                FileWriter.printPositionsSquared(new NeighbourWrapperSquaredParticle(cellIndexMethod.getParticles(), cellIndexMethod.getWalls()), config.getTotalParticles());
                aux = 0;
            }
        }
    }


    public static void simulate(Integer particlesN, int run, double openWidth) throws IOException {
        int aux = 0;
        double dt = 5e-5;
        double time = 0, finishTime = 10;
        CIMConfig config = new CIMConfig(1.0, 0.3, particlesN, 7.07 * 10e-3, 0.0106, 10.0, openWidth);
        CellIndexMethod cellIndexMethod = new CellIndexMethod(config);

        while(time < finishTime) {  // TODO: Ver criterio de corte
            if(time!= 0d)
                cellIndexMethod.updateParticles();

            Map<Particle, NeighbourWrapper> neighbourWrapperMap = cellIndexMethod.calculateNeighbours();
            for(Particle particle : cellIndexMethod.getParticles().stream().filter((particle) -> !particle.isFixed()).collect(Collectors.toList())){
                GranularMediaForce granularMediaForce = new GranularMediaForce(particle, neighbourWrapperMap.getOrDefault(particle, new NeighbourWrapper()));
                Beeman beeman = new Beeman(dt, granularMediaForce);
                beeman.nextStep(particle);
            }
            time += dt;
//            System.out.println(time);
            cellIndexMethod.clear();
            aux++;
            if(aux == 50 && run == 4) {
                FileWriter.printPositions(new NeighbourWrapper(cellIndexMethod.getParticles(), cellIndexMethod.getWalls()), config.getTotalParticles());
                aux = 0;
            }
        }
        FileWriter.finalCsv(cellIndexMethod.getParticles(), config.getTotalParticles(), run);
    }



}
