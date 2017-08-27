/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid1.compute;

import com.barrybecker4.simulation.liquid1.model.Cell;
import com.barrybecker4.simulation.liquid1.model.Grid;
import com.barrybecker4.simulation.liquid1.model.Particle;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

/**
 *  Used to interpolate velocities in the grid.
 *
 *  @author Barry Becker
 */
public class VelocityInterpolator {

    private Grid grid;

    /**
     * constructor
     */
    public VelocityInterpolator(Grid grid)  {

       this.grid = grid;
    }

    /**
     * Find the velocity of a particle somewhere on the grid.
     * @param particle particle to find velocity for
     * @return the interpolated (weighted) velocity vector for the particle
     */
    public Vector2d findVelocity(Particle particle) {
        int i = (int) particle.x;
        int j = (int) particle.y;
        int ii = ((particle.x - i) > 0.5) ? (i + 1) : (i - 1);
        int jj = ((particle.y - j) > 0.5) ? (j + 1) : (j - 1);

        return interpolateVelocity( particle, grid.getCell(i, j),
                                    grid.getCell(ii, j),    grid.getCell(i, jj),
                                    grid.getCell(i - 1, j), grid.getCell(i - 1, jj), // u
                                    grid.getCell(i, j - 1), grid.getCell(ii, j - 1)  // v
                                  );
    }

    /**
     * linearly interpolate the velocity of the particle based on its position
     * relative to 4 neighboring velocity vectors.
     * There are 4 cases: The numbers indicate the parameter
     *  case 1: particle in the upper right corner: 6 distinct cells are passed in this pattern
     *          4  2  X
     *          3  c  1
     *          X  5  6
     *  case 2: particle in upper left
     *         4   2   X
     *        1/3  c   X
     *         6   5   X
     *  case 3: lower right.
     *         X   X   X
     *         3   c   1
     *         4  2/5  6
     *  case 4: particle in lower left.
     *         X    X   X
     *        1/3   c   X
     *        4/6 2/5   X
     *
     * RISK: 1
     * @param particle the particle to find the velocity of.
     * @param cell the central cell that the particle is in.
     * @param cX either one forward or one back in the x direction
     *      depending on the position of the particle.   [1]
     * @param cY either one forward or one back in the y direction
     *       depending on the position of the particle.    [2]
     * @param cXm1  x - 1  (always the cell to the left)      [3]
     * @param cXm1y x - 1 and either one forward or one back in the y
     *       direction depending on the position of the particle. [4]
     * @param cYm1  y - 1  (always the cell to the bottom)       [5]
     * @param cYm1x  y - 1 and either one forward or one back in the x
     *       direction depending on the position of the particle.  [6]
     * @return the interpolated velocity vector.
     */
    Vector2d interpolateVelocity( Point2d particle, Cell cell,
                                         Cell cX, Cell cY,
                                         Cell cXm1, Cell cXm1y, // u
                                         Cell cYm1, Cell cYm1x // v
                                         ) {
        assert ( !(cell.isObstacle() || cell.isEmpty())) :
             "Error: interpVelocity cell status=" + cell.getStatus()
               + " num particles = " + cell.getNumParticles();

        double x = particle.x - Math.floor(particle.x);
        double y = particle.y - Math.floor(particle.y);

        double xx = (x > 0.5) ? (1.5 - x) : (0.5 + x);
        double yy = (y > 0.5) ? (1.5 - y) : (0.5 + y);
        double x1 = (1.0 - x) * cXm1.getU() + x * cell.getU();
        double x2 = (1.0 - x) * cXm1y.getU() + x * cY.getU();
        double pu = x1 * yy + x2 * (1.0 - yy);
        double y1 = (1.0 - y) * cYm1.getV() + y * cell.getV();
        double y2 = (1.0 - y) * cYm1x.getV() + y * cX.getV();
        double pv = y1 * xx + y2 * (1.0 - xx);

        return new Vector2d(pu, pv);
    }

}