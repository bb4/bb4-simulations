/** Copyright by Barry G. Becker, 2000-2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.habitat;

import com.barrybecker4.common.concurrency.ThreadUtil;
import com.barrybecker4.common.math.MathUtil;
import com.barrybecker4.simulation.common.ui.Simulator;
import com.barrybecker4.simulation.common.ui.SimulatorApplet;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;
import com.barrybecker4.simulation.habitat.creatures.Populations;
import com.barrybecker4.simulation.habitat.creatures.SerengetiPopulations;
import com.barrybecker4.simulation.habitat.options.DynamicOptions;
import com.barrybecker4.simulation.habitat.options.HabitatOptionsDialog;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Simulates foxes (predators) and rabbits (prey) in the wild.
 *
 * @author Barry Becker
 */
public class HabitatSimulator extends Simulator {

    private Populations populations;
    private DynamicOptions options;
    private JSplitPane splitPane;


    /** Constructor */
    private HabitatSimulator() {

        super("Habitat Simulation");
        setBackground(Color.WHITE);
        populations = new SerengetiPopulations();

        PopulationGraphPanel graphPanel = new PopulationGraphPanel(populations);
        HabitatPanel habitatPanel = new HabitatPanel(populations);

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                           habitatPanel, graphPanel);


        //Provide minimum sizes for the two components in the split pane
        Dimension minimumSize = new Dimension(100, 50);
        habitatPanel.setMinimumSize(minimumSize);
        graphPanel.setMinimumSize(minimumSize);

        splitPane.setDividerLocation(350);

        this.add(splitPane);
    }

    @Override
    protected void reset() {

        this.setPaused(true);
        // wait till actually paused. Not clean, but oh well.
        ThreadUtil.sleep(500);
        MathUtil.RANDOM.setSeed(1);
        populations.reset();
        options.reset();
        this.setPaused(false);
    }

    @Override
    protected double getInitialTimeStep() {
        return 1.0;
    }

    @Override
    public double timeStep() {

        options.update();
        populations.nextDay();
        return timeStep_;
    }

    public Populations getPopulations() {
        return populations;
    }

    /**
     * Draw the population graph under the hab.
     * @param g java graphics context
     */
    @Override
    public void paint( Graphics g ) {
        splitPane.setSize(getSize());
        splitPane.paint(g);
    }

    @Override
    public JPanel createDynamicControls() {
        options = new DynamicOptions(this);
        return options;
    }

    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
         return new HabitatOptionsDialog(frame, this);
    }

    public static void main( String[] args ) {
        final HabitatSimulator sim = new HabitatSimulator();

        sim.setPaused(true);
        GUIUtil.showApplet(new SimulatorApplet(sim));
    }
}