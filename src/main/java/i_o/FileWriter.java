package i_o;

import cell_index_method.NeighbourWrapper;
import entity.Obstacle;
import entity.Particle;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
        int totalSize = neighbourWrapper.getObstacles().size() + neighbourWrapper.getParticles().size();
        simulationBufferedWriter.write(String.valueOf(totalSize));
        simulationBufferedWriter.newLine();
        simulationBufferedWriter.newLine();

        for(Obstacle obstacle : neighbourWrapper.getObstacles()){
            simulationBufferedWriter.write(obstacle.getId() + " " + obstacle.getX() + " " + obstacle.getY() + " 0.0 0.0 " + obstacle.getRadius());
            simulationBufferedWriter.newLine();
        }
        for(Particle particle : neighbourWrapper.getParticles()){
            simulationBufferedWriter.write(particle.getId() + " " + particle.getX() + " " + particle.getY() + " " + particle.getVx() + " " + particle.getVy()  + " " +  particle.getRadius());
            simulationBufferedWriter.newLine();
        }
        simulationBufferedWriter.flush();
    }

    public static void binsCsv(List<Obstacle> obstacles) throws IOException {
        java.io.FileWriter fileWriter = new java.io.FileWriter("bins.csv");
        BufferedWriter other = new BufferedWriter(fileWriter);
        other.write("bin_start, bin_end");
        other.newLine();
        Set<Double> bins = obstacles.stream().filter((obstacle) -> obstacle.getY() == 0).map(Obstacle::getX).sorted().collect(Collectors.toSet());
        List<Double> fromSet = new ArrayList<>(bins).stream().sorted().collect(Collectors.toList());
        for(int i=0; i<bins.size()-1; i++){
            other.write(fromSet.get(i) + "," + fromSet.get(i+1));
            other.newLine();
        }
        other.flush();
    }



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
