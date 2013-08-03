/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.common.ui;

import com.barrybecker4.common.app.ClassLoaderSingleton;
import com.barrybecker4.ui.animation.AnimationPanel;
import com.barrybecker4.ui.application.ApplicationApplet;
import com.barrybecker4.ui.util.GUIUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Base class for all simulator applets.
 * Resizable applet for showing simulations.
 *
 * @author Barry Becker
 */
public class SimulatorApplet extends ApplicationApplet {

    private Simulator simulator_;

    private static final boolean RUN_OPTIMIZATION = false;
    private static final String DEFAULT_SIMULATOR = "com.barrybecker4.simulation.fluid.ui.FluidSimulator";

    public SimulatorApplet() {
        super(new String[] {});
    }

    /**
     * Construct the applet
     * @param simulatorClassName  name of the simulator class to show.
     */
    public SimulatorApplet(String[] args, String simulatorClassName) {

        super(args);
        System.out.println("simulatorClassName=" + simulatorClassName);
        simulator_ = createSimulationFromClassName(simulatorClassName);
    }

    @Override
    public String getName() {
        return simulator_.getName();
    }

    /**
     * create and initialize the simulation
     * The top controls define common buttons like start / reset
     * There is an optional set of dynamic options on the right for modifying the simulation as it runs.
     */
    @Override
    public JPanel createMainPanel() {

        if (simulator_ == null) {

            String className = getParameter("panel_class"); //NON-NLS
            className = className == null ? DEFAULT_SIMULATOR : className;
            simulator_ = createSimulationFromClassName(className);
        }

        JPanel animPanel = new AnimationPanel( simulator_ );
        animPanel.add( simulator_.createTopControls(), BorderLayout.NORTH );

        JPanel dynamicControls = simulator_.createDynamicControls();
        if (dynamicControls != null) {
            animPanel.add( dynamicControls, BorderLayout.EAST );
        }

        simulator_.setVisible(true);

        return animPanel;
    }

    private static Simulator createSimulationFromClassName(String className) {
        if (className == null) {
            return null;
        }
        Class simulatorClass = ClassLoaderSingleton.loadClass(className);
        Simulator simulator = null;

        try {

            simulator = (Simulator) simulatorClass.newInstance();

        } catch (InstantiationException e) {
            System.err.println("Could not create class for "  + className); //NON-NLS
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return simulator;
    }

    /**
     * the applets start method.
     */
    @Override
    public void start() {
        super.start();
        if (RUN_OPTIMIZATION)  {
            simulator_.doOptimization();
        }
        this.repaint();
    }


    //------ Main method - to allow running as an application ---------------------

    public static void main( String[] args ) {

        // create a simulator panel of the appropriate type based on the name of the class passed in.
        // if no simulator is specified as an argument, then we use the default.
        String simulatorClassName = DEFAULT_SIMULATOR;
        if (args.length == 1) {
            simulatorClassName = args[0];
        } else if (args.length > 1) {
            simulatorClassName = args[1];
        }

        SimulatorApplet applet = new SimulatorApplet(args, simulatorClassName);
        GUIUtil.showApplet( applet );
    }
}


