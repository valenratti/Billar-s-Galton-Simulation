import cell_index_method.CellIndexMethod;
import cell_index_method.NeighbourWrapper;
import entity.Obstacle;
import entity.Particle;
import utils.FileWriter;

import java.io.IOException;
import java.util.List;

public class Main {


    public static void main(String[] args) throws IOException {
        Simulator.simulate();
//        List<Obstacle> obstacleList = CellIndexMethod.spawnObstacles();
//        List<Particle> particleList = CellIndexMethod.spawnParticles();
//        System.out.println(obstacleList);
//        FileWriter.printPositions(new NeighbourWrapper(particleList, null, obstacleList));
    }
}
