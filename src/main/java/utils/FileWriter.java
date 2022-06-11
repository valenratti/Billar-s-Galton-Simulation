package utils;

import cell_index_method.NeighbourWrapper;
import entity.Obstacle;
import entity.Particle;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class FileWriter {

    private static BufferedWriter simulationBufferedWriter;

    public static void generateXYZFile(){
        try{
            java.io.FileWriter fileWriter = new java.io.FileWriter("positions-" + LocalDateTime.now()  + ".xyz");
            simulationBufferedWriter = new BufferedWriter(fileWriter);
        }catch(IOException e){
            System.out.println(e);
        }
    }

    public static void printPositions(NeighbourWrapper neighbourWrapper) throws IOException {
        if(simulationBufferedWriter == null){
            generateXYZFile();
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

}
