package com.barrybecker4.simulation.conway.model.kernal;

import com.barrybecker4.simulation.conway.model.Conway;

/**
 * Looks only at immediate neighbors
 * @author Barry Becker
 */
public abstract class AbstractKernel implements Kernel {

    protected Conway cave;

    public AbstractKernel(Conway cave) {
         this.cave = cave;
    }

    protected boolean isOffEdge(int x, int y) {
        return (x < 0 || y < 0 || x >= cave.getWidth() || y >= cave.getLength());
    }

}
