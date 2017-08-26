/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid1.rendering;

/**
 * Liquid Rendering options.
 *
 * @author Barry Becker
 */
public final class RenderingOptions {

    private boolean showVelocities_ = true;
    private boolean showPressures_ = true;
    private boolean showCellStatus_ = true;
    private boolean showGrid_ = true;

    public RenderingOptions() {
    }

    public void setShowVelocities(boolean show) {
        showVelocities_ = show;
    }

    public boolean getShowVelocities() {
        return showVelocities_;
    }

    public void setShowPressures(boolean show) {
        showPressures_ = show;
    }

    public boolean getShowPressures() {
        return showPressures_;
    }

    public void setShowCellStatus(boolean show) {
        showCellStatus_ = show;
    }

    public boolean getShowCellStatus() {
        return showCellStatus_;
    }

    public void setShowGrid(boolean show) {
        showGrid_ = show;
    }

    public boolean getShowGrid() {
        return showGrid_;
    }
}
