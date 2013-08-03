/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.habitat.creatures;

import com.barrybecker4.common.math.function.CountFunction;
import com.barrybecker4.common.math.function.Function;
import com.barrybecker4.simulation.habitat.model.Cell;
import com.barrybecker4.simulation.habitat.model.HabitatGrid;
import com.barrybecker4.ui.renderers.MultipleFunctionRenderer;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Create populations for all our creatures.
 *
 * @author Barry Becker
 */
public abstract class Populations extends ArrayList<Population> {

    private long dayCount = 0;

    /** associate population with function*/
    private Map<Population, CountFunction> functionMap;

    private HabitatGrid grid;

    /**
     * Constructor
     */
    public Populations() {

        initialize();
    }

    public void initialize() {
        functionMap = new HashMap<Population, CountFunction>();
        grid = new HabitatGrid(20, 15);

        this.clear();
        addPopulations();

        updateGridCellCounts();
    }

    protected abstract void addPopulations();

    public void nextDay() {
        for (Population pop : this) {
            pop.nextDay(grid);
        }
        // remove any creatures that might have died by starvation or being eaten.
        for (Population pop : this) {
            pop.removeDead(grid);
        }
        updateFunctions(dayCount);
        dayCount++;
    }

    public MultipleFunctionRenderer createFunctionRenderer() {

        List<Function> functions = new ArrayList<Function>();
        List<Color> lineColors = new LinkedList<Color>();

        for (Population pop : this) {
            CountFunction func = new CountFunction(pop.getSize());
            functions.add(func);
            lineColors.add(pop.getType().getColor());

            functionMap.put(pop, func);
        }

        return new MultipleFunctionRenderer(functions, lineColors);
    }

    private void updateFunctions(long iteration) {

        for (Population pop : this) {
            functionMap.get(pop).addValue(iteration, pop.getSize());
        }
    }

    private void updateGridCellCounts() {
        for (Population pop : this) {
            for (Creature c : pop.getCreatures()) {
                Cell cell = grid.getCellForPosition(c.getLocation());
                cell.addCreature(c);
            }
        }
    }

}
