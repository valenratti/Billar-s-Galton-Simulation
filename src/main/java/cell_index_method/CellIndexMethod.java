package cell_index_method;

import entity.Entity;
import entity.Obstacle;
import entity.Particle;
import lombok.Data;
import utils.Utils;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class CellIndexMethod {

    private Area area;
    private final Map<CellCoordinates, Cell> cellMap;
    private Set<CellCoordinates> currentOccupiedCells;
    private boolean periodicBorder;
    private final int cellsPerRow;
    private final int cellsPerColumn;
    private final double cellSideLength;
    private final CIMConfig config;

    public CellIndexMethod(CIMConfig config) {
        this.config = config;
        this.cellSideLength = config.getMaxParticleRadius() * 2;
        this.cellsPerRow = calculateCellsPerRow();
        this.cellsPerColumn = calculateCellsPerColumn();
        this.cellMap = new HashMap<>();

        for(int i = cellsPerColumn - 1; i >= 0; i--)
            for(int j = 0; j < cellsPerRow; j++)
                cellMap.put(new CellCoordinates(i, j), new Cell(i, j, new ArrayList<>()));

        List<Particle> particleList = spawnParticles(config);
    }

    private List<Particle> spawnParticles(CIMConfig config) {
        //TODO
        return new ArrayList<>();
    }

    private List<Obstacle> spawnObstacles(){
        double d = 5.45;
        return new ArrayList<>();
    }

    private Particle generateParticle(double i, double j) {
        double radius = Utils.rand(config.getMinParticleRadius(), config.getMaxParticleRadius());
        // centered in the cell
        double x = (j * config.getAreaWidth() / cellsPerRow) + radius;
        double y = (i * config.getAreaHeight() / cellsPerColumn) + radius;

        return new Particle(x, y, 0, 0, config.getParticleMass(), radius, false);   // TODO check: v0 = 0?
    }

    private void addParticleToCell(Particle particle, int row, int column) {
        Cell cell = cellMap.getOrDefault(new CellCoordinates(row, column), new Cell(row, column, new ArrayList<>()));
        particle.setCell(cell);
        cell.addParticle(particle);
        cellMap.put(new CellCoordinates(row, column), cell);
    }

    public Map<Particle, NeighbourWrapper> calculateNeighbours() {
        Map<Particle, NeighbourWrapper> neighboursMap = new HashMap<>();

        for(Cell cell : cellMap.values()){
            List<Cell> neighbourCells = calculateNeighbourCells(cell.getRow(), cell.getColumn())
                    .stream().map(cellMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            List<Particle> onlyParticleEntities = getParticleEntities(cell.getEntityList());

            for (Particle particle : onlyParticleEntities) {
                NeighbourWrapper currentParticleNeighbours = neighboursMap.getOrDefault(particle, new NeighbourWrapper());
                for(Cell neighbourCell : neighbourCells) {

                    //Identify all neighbours, they can be walls, obstacles or particles
                    List<Entity> neighbours = neighbourCell.getEntityList()
                            .stream()
                            .filter((current) -> !current.isFixed())
                            .filter(current -> !current.equals(particle))
                            .filter((current) -> Entity.distance(particle, current) <= area.getRc())
                            .collect(Collectors.toList())
                            .stream().distinct().collect(Collectors.toList());

                    neighbours.forEach((neighbour) -> {
                        if(neighbour.getType().equals(Entity.EntityType.PARTICLE)) {
                            NeighbourWrapper neighbourNeighbours = neighboursMap.getOrDefault((Particle) neighbour, new NeighbourWrapper());
                            neighbourNeighbours.add(particle);
                            neighboursMap.put((Particle) neighbour, neighbourNeighbours);
                        }
                    });
                    currentParticleNeighbours.addAll(neighbours);
                }
                neighboursMap.put(particle, currentParticleNeighbours);
            }
        }
        return neighboursMap;
    }

    private List<Particle> getParticleEntities(List<Entity> entityList){
        return entityList.stream().map((entity) -> {
            if(entity.getType().equals(Entity.EntityType.PARTICLE))
                return (Particle) entity;
            else return null;
        }).collect(Collectors.toList());
    }

    private int calculateCellsPerRow(){
        double L = config.getAreaWidth();
        int possibleM = (int) Math.floor(L / this.cellSideLength);
        return possibleM == 0 ? 1 : possibleM;
    }

    private int calculateCellsPerColumn(){
        double L = config.getAreaHeight();
        int possibleM = (int) Math.floor(L / this.cellSideLength);
        return possibleM == 0 ? 1 : possibleM;
    }

    private List<CellCoordinates> calculateNeighbourCells(int i, int j){
        int M = this.cellsPerColumn;
        List<CellCoordinates> cellCoordinates = new ArrayList<>();
        //Add itself as neighbour
        cellCoordinates.add(new CellCoordinates(i,j));
        boolean lastCol = j + 1 == M, firstRow = i == 0, lastRow = i + 1 == M;

        if(!lastCol) {
            cellCoordinates.add(new CellCoordinates(i, j + 1)); //Derecha
            if(!lastRow)
                cellCoordinates.add(new CellCoordinates(i + 1, j + 1)); // Diagonal abajo
            if(!firstRow)
                cellCoordinates.add(new CellCoordinates(i - 1, j + 1)); // Diagonal arriba
        }
        if(!firstRow) {
            cellCoordinates.add(new CellCoordinates(i - 1, j)); //  Arriba
        }
        return cellCoordinates;
    }

    public void updateParticles(List<Particle> particles){
        Set<CellCoordinates> currentOccupiedCells = new HashSet<>();
        for(Particle particle : particles.stream().filter(particle -> !particle.isFixed()).collect(Collectors.toList())) {
            int row = (int) Math.floor(particle.getY() / this.cellSideLength);
            int column = (int) Math.floor(particle.getX() / this.cellSideLength);
            try {
                if (row >= 0 && row < cellsPerColumn && column >= 0 && column < cellsPerRow) {
                    Cell cell = cellMap.get(new CellCoordinates(row, column));
                    particle.setCell(cell);
                    cell.addParticle(particle);
                    currentOccupiedCells.add(new CellCoordinates(row, column));
                }
            } catch (Exception e){
                //                    FileWriter.printPositions(0,particles);
                System.out.println("Wrong dt. entity.Particle with id:" + particle.getId());
                System.out.println(row + " " + column);
                System.out.println(particle.getY() + " " + particle.getX());
                System.exit(0);
            }
        }
        this.currentOccupiedCells = currentOccupiedCells;
    }

    public void clear(){
        cellMap.values().forEach(Cell::clearParticles);
    }

}
