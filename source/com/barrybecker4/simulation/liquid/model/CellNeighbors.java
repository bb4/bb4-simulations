/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid.model;

/**
 *  The cells directly neighboring a given cell.
 *
 *  @author Barry Becker
 */
public class CellNeighbors {

    private Cell cellXp1;
    private Cell cellXm1;
    private Cell cellYp1;
    private Cell cellYm1;

    /**
     * Constructor
     * @param cXp1 cell to the right
     * @param cXm1 cell to the left
     * @param cYp1 cell above
     * @param cYm1 cell below
     */
    public CellNeighbors(Cell cXp1, Cell cXm1,Cell cYp1, Cell cYm1)  {
        cellXp1 = cXp1;
        cellXm1 = cXm1;
        cellYp1 = cYp1;
        cellYm1 = cYm1;
    }

    /** neighbor cell to the left. */
    public Cell getLeft() {
        return cellXm1;
    }

    /** neighbor cell to the right. */
    public Cell getRight() {
        return cellXp1;
    }

    /** neighbor cell to the top. */
    public Cell getBottom() {
        return cellYm1;
    }

    /** neighbor cell to the bottom. */
    public Cell getTop() {
        return cellYp1;
    }

    /**
     * @return true if all the neighbors have at least one particle
     */
    public boolean allHaveParticles() {
       return getRight().getNumParticles() > 0
            && getLeft().getNumParticles() > 0
            && getBottom().getNumParticles() > 0
            && getTop().getNumParticles() > 0;
    }

    /**
     * @return false if none of the neighbors have any particles.
     */
    public boolean noneHaveParticles() {
         return getRight().getNumParticles() == 0
            && getLeft().getNumParticles() == 0
            && getBottom().getNumParticles() == 0
            && getTop().getNumParticles() == 0;
    }
}