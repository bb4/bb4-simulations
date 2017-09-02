/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.predprey;

import com.barrybecker4.common.math.function.CountFunction;
import com.barrybecker4.common.math.function.Function;
import com.barrybecker4.simulation.common.ui.Simulator;
import com.barrybecker4.simulation.common.ui.SimulatorApplet;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;
import com.barrybecker4.simulation.graphing1.GraphOptionsDialog;
import com.barrybecker4.simulation.predprey.creatures.Foxes;
import com.barrybecker4.simulation.predprey.creatures.Population;
import com.barrybecker4.simulation.predprey.creatures.Rabbits;
import com.barrybecker4.simulation.predprey.options.DynamicOptions;
import com.barrybecker4.ui.renderers.MultipleFunctionRenderer;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Simulates foxes (predators) and rabbits (prey) in the wild.
 *
 * @author Barry Becker
 */
public class PredPreySimulator extends Simulator {

    private MultipleFunctionRenderer graph;
    private long iteration;

    private Rabbits rabbits;
    private Foxes foxes;

    private CountFunction rabbitFunction;
    private CountFunction foxFunction;

    private DynamicOptions options_;

    /** Constructor */
    private PredPreySimulator() {
        super("Predator Prey Simulation");

        foxes = new Foxes();
        rabbits = new Rabbits();

        initGraph();
    }

    public List<Population> getCreatures() {
        List<Population> creatures = new ArrayList<Population>();
        creatures.add(foxes);
        creatures.add(rabbits);
        return creatures;
    }

    @Override
    protected void reset() {

        initGraph();
        options_.reset();
    }

    @Override
    protected double getInitialTimeStep() {
        return 1.0;
    }

    @Override
    public double timeStep() {
        iteration++;

        foxes.setPopulation( foxes.getPopulation() * foxes.birthRate
                           - foxes.getPopulation() * foxes.deathRate / rabbits.getPopulation());
        //                   - lions.getPopulation() * foxes.deathRate * foxes.getPopulation());
        rabbits.setPopulation( rabbits.getPopulation() * rabbits.birthRate
                             - foxes.getPopulation() * rabbits.deathRate * rabbits.getPopulation());
        //                    - lions.getPopulation() * rabbits.deathRate * rabbits.getPopulation());

        rabbitFunction.addValue(iteration, rabbits.getPopulation());
        foxFunction.addValue(iteration, foxes.getPopulation());

        options_.update();

        return timeStep_;
    }

    private  void initGraph() {
        iteration = 0;

        rabbits.reset();
        foxes.reset();

        rabbitFunction = new CountFunction(Rabbits.INITIAL_NUM_RABBITS);
        foxFunction = new CountFunction(Foxes.INITIAL_NUM_FOXES);

        List<Function> functions = new LinkedList<Function>();
        functions.add(rabbitFunction);
        functions.add(foxFunction);
        //functions.add(lionFunction);

        List<Color> lineColors = new LinkedList<Color>();
        lineColors.add(Rabbits.COLOR);
        lineColors.add(Foxes.COLOR);
        //lineColors.add(Lions.COLOR);

        graph = new MultipleFunctionRenderer(functions, lineColors);
    }


    @Override
    public JPanel createDynamicControls() {
        options_ = new DynamicOptions(this);
        return options_;
    }


    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
         return new GraphOptionsDialog(frame, this );
    }

    @Override
    public void paint( Graphics g ) {
        graph.setSize(getWidth(), getHeight());
        graph.paint(g);
    }

    public static void main( String[] args ) {
        final PredPreySimulator sim = new PredPreySimulator();

        sim.setPaused(true);
        GUIUtil.showApplet(new SimulatorApplet(sim));
    }
}