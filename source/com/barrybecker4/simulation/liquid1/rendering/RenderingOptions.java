/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid1.rendering;

/**
 * Liquid Rendering options.
 *
 * @author Barry Becker
 */
public final class RenderingOptions {

    private boolean showVelocities = true;
    private boolean showPressures = true;
    private boolean showCellStatus = true;
    private boolean showGrid = true;

    public RenderingOptions() { }

    public void setShowVelocities(boolean show) {
        showVelocities = show;
    }

    public boolean getShowVelocities() {
        return showVelocities;
    }

    public void setShowPressures(boolean show) {
        showPressures = show;
    }

    public boolean getShowPressures() {
        return showPressures;
    }

    public void setShowCellStatus(boolean show) {
        showCellStatus = show;
    }

    public boolean getShowCellStatus() {
        return showCellStatus;
    }

    public void setShowGrid(boolean show) {
        showGrid = show;
    }

    public boolean getShowGrid() {
        return showGrid;
    }
}
