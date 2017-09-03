/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.habitat1.model;

import javax.vecmath.Point2d;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Barry Becker
 */
public class HabitatGrid {

    private int xDim;
    private int yDim;

    private Cell[][] cells;


    public HabitatGrid(int xDim, int yDim)  {
        this.xDim = xDim;
        this.yDim = yDim;

        cells = new Cell[xDim + 1][yDim + 1];
        for (int i=0; i<=xDim; i++) {
            for (int j=0; j<=yDim; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
    }

    public Cell getCellForPosition(Point2d position) {

        int x = (int) (position.x * xDim);
        int y = (int) (position.y * yDim);

        return cells[x][y];
    }

    public List<Cell> getNeighborCells(Cell cell) {
        List<Cell> nbrCells = new ArrayList<Cell>(8);

        int xm1 = getSafeX(cell.xIndex - 1);
        int xp1 = getSafeX(cell.xIndex + 1);
        int ym1 = getSafeY(cell.yIndex - 1);
        int yp1 = getSafeY(cell.yIndex + 1);
        nbrCells.add(cells[xm1][ym1]);
        nbrCells.add(cells[xm1][cell.yIndex]);
        nbrCells.add(cells[xm1][yp1]);
        nbrCells.add(cells[cell.xIndex][ym1]);
        nbrCells.add(cells[cell.xIndex][yp1]);
        nbrCells.add(cells[xp1][ym1]);
        nbrCells.add(cells[xp1][cell.yIndex]);
        nbrCells.add(cells[xp1][yp1]);

        return nbrCells;
    }

    private int getSafeX(int xIndex) {
        return Math.abs(xIndex % xDim);
    }

    private int getSafeY(int yIndex) {
        return Math.abs(yIndex % yDim);
    }
}
