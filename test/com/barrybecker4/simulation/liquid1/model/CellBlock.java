/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid1.model;


/**
 * A 3x3 block of cells for testing purposes.
 * There is a layer of obstacle cells surrounding the 9 in the middle.
 *
 *                      ^  positive y direction.
 *                       |
 *    c(-1, -1)  |    c(0, -1) | c(0, -1)
 *   -------------------------------------
 *    c(-1, 0)  |    c(0, 0)   | c(0, 0)   --->  Positive x
 *   -------------------------------------
 *    c(-1, 1)  |    c(0, 1)   |  c(0, 1)
 *
 * @author Barry Becker
 */
public class CellBlock {

    private static final int DIM = 5;
    private final Cell[][] block_;

    public CellBlock() {
        block_ = new Cell[DIM][DIM];

        for (int i = 0; i<DIM; i++)  {
           for (int j = 0; j<DIM; j++) {
               block_[i][j] = new Cell();
           }
        }
        updateCellStatuses();
    }

    /**
     * Gets a cell relative to the center cell position.
     * e.g. get(0,0) returns the center cell.
     * @return cell relative to center of the block
     */
    public Cell get(int offsetX, int offsetY) {
        return block_[offsetX + 2][offsetY + 2];
    }

    /**
     * @return the cell at the specified position in the array
     * (excluding the outer obstacle cells).
     */
    public Cell getAbsolute(int x, int y) {
        return block_[x+1][y+1];
    }

    public void setPressures(double p) {
        for (int i = 0; i<DIM; i++)
           for (int j = 0; j<DIM; j++)
               block_[i][j].setPressure(p);
    }

    public void setVelocities(double u, double v) {
        for (int i = 0; i<DIM; i++)
           for (int j = 0; j<DIM; j++)
               block_[i][j].initializeVelocity(u, v);
    }

    /**
     * @param numParticles number of particles to add to each cell in the block.
     */
    public void setCenterCellParticles(int numParticles) {

       setSingleCellParticles(1, 1, numParticles);
       updateCellStatuses();
    }

    /**
     * Set all the cell particles with numParticlesPerCell
     * @param numParticlesPerCell number of particles to add to each cell in the block.
     */
    public void setAllCellParticles(int numParticlesPerCell) {
        setCellBlockParticles(1, 1, DIM-1, DIM-1, numParticlesPerCell);
    }

    /** set left 6 cells */
    public void setLeftCellParticles(int numParticlesPerCell) {
        setCellBlockParticles(1, 1, DIM-2, DIM-1, numParticlesPerCell);
    }

    /** set right 6 cells */
    public void setRightCellParticles(int numParticlesPerCell) {
        setCellBlockParticles(2, 1, DIM-1, DIM-1, numParticlesPerCell);
    }

    /** set top 6 cells */
    public void setTopCellParticles(int numParticlesPerCell) {
        setCellBlockParticles(1, 1, DIM-1, DIM-2, numParticlesPerCell);
    }

    /** set bottom 6 cells */
    public void setBottomCellParticles(int numParticlesPerCell) {
        setCellBlockParticles(1, 2, DIM-1, DIM-1, numParticlesPerCell);
    }

    /**
     * @param numParticlesPerCell number of particles to add to each cell in the block.
     */
    private void setCellBlockParticles(int minX, int minY, int maxX, int maxY, int numParticlesPerCell) {
        for (int i = minX; i<maxX; i++){
           for (int j = minY; j<maxY; j++) {
               setSingleCellParticles(i, j, numParticlesPerCell);
           }
        }
        updateCellStatuses();
    }

    private void setSingleCellParticles(int xpos, int ypos, int numParticles) {
        Cell cell = getAbsolute(xpos, ypos);
        int n = cell.getNumParticles();
        for (int k = 0; k<n; k++) {
           cell.decParticles();
        }
        for (int k = 0; k<numParticles; k++) {
           cell.incParticles();
        }
    }

    public void updateCellStatuses() {
        for (int i = 1; i<DIM-2; i++)
           for (int j = 1; j<DIM-2; j++)
               block_[i][j].updateStatus(findNeighbors(i, j));
    }

    public CellNeighbors getCenterNeighbors() {
        return findNeighbors(2, 2);
    }

    public CellNeighbors findNeighbors(int i, int j) {
        return new CellNeighbors(block_[i+1][j], block_[i-1][j], block_[i][j+1], block_[i][j-1]);
    }
}

