package com.barrybecker4.simulation.cave1.model.kernal;

import com.barrybecker4.simulation.cave1.model.Cave;

/**
 * Looks only at immediate neighbors
 * @author Barry Becker
 */
public abstract class AbstractKernel implements Kernel {

    protected Cave cave;

    public AbstractKernel(Cave cave) {
         this.cave = cave;
    }

    boolean isOffEdge(int x, int y) {
        return (x < 0 || y < 0 || x >= cave.getWidth() || y >= cave.getLength());
    }

}
