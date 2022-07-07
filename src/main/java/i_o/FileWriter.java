package i_o;

import cell_index_method.NeighbourWrapper;
import entity.Particle;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class FileWriter {

    private static BufferedWriter simulationBufferedWriter;

    public static void generateXYZFile(Integer n){
        try{
            java.io.FileWriter fileWriter = new java.io.FileWriter("positions-" + n + ".xyz");
            simulationBufferedWriter = new BufferedWriter(fileWriter);
        }catch(IOException e){
            System.out.println(e);
        }
    }

    public static void printPositions(NeighbourWrapper neighbourWrapper, Integer n) throws IOException {
        if(simulationBufferedWriter == null){
            generateXYZFile(n);
        }

        simulationBufferedWriter.newLine();

        for(Particle particle : neighbourWrapper.getParticles()){
            simulationBufferedWriter.write(particle.getId() + " " + particle.getX() + " " + particle.getY() + " " + particle.getVx() + " " + particle.getVy()  + " " +  particle.getRadius());
            simulationBufferedWriter.newLine();
        }
        simulationBufferedWriter.flush();
    }

//o



    public static void finalCsv(List<Particle> particleList, Integer n, Integer run) throws IOException {
        java.io.FileWriter fileWriter = new java.io.FileWriter("end_positions" + "-" + n + "-" + run + ".csv");
        BufferedWriter other = new BufferedWriter(fileWriter);
        other.write("particle_id, particle_x_position");
        other.newLine();

        for(Particle particle : particleList.stream().filter((particle) -> !particle.isFixed()).collect(Collectors.toList())){
            other.write(String.format("%s,%s", particle.getId(), particle.getX()));
            other.newLine();
        }
        other.flush();
    }

    public static void reset(){
        simulationBufferedWriter = null;
    }

}
