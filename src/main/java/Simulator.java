import cell_index_method.CIMConfig;
import cell_index_method.CellIndexMethod;

public class Simulator {


    public static void simulate(){
        double dt = 5e-5;
        boolean finishedSimulation = false;
        double time = 0;
        CIMConfig config = new CIMConfig(80.0, 120.0, 10, 0.06, 0.06, 10.0);
        CellIndexMethod cellIndexMethod = new CellIndexMethod(config);

        while(!finishedSimulation){
            //  for each particle:
            // get neighbours
            // granular media force uses walls, particles and obstacles
            // call gpc integrator and evolve
        }
    }



}
