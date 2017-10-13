/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.graphing1;

import com.barrybecker4.common.math.function.Function;
import com.barrybecker4.simulation.common1.ui.Simulator;
import com.barrybecker4.simulation.common1.ui.SimulatorOptionsDialog;
import com.barrybecker4.ui.animation.AnimationFrame;
import com.barrybecker4.ui.renderers.SingleFunctionRenderer;

import java.awt.*;


/**
 * Simulates graphing a function
 *
 * @author Barry Becker
 */
public class GraphSimulator extends Simulator {

    private SingleFunctionRenderer graph;
    private Function function;

    public GraphSimulator() {
        super("Graph");
        initGraph();
    }

    public void setFunction(Function function) {
        this.function = function;
        initGraph();
    }

    @Override
    protected void reset() {
        initGraph();
    }

    @Override
    protected double getInitialTimeStep() {
        return 1.0;
    }

    @Override
    public double timeStep() {
        return timeStep_;
    }

    private void initGraph() {
        if (function == null) {

            function = FunctionType.DIAGONAL.function;
        }
        graph = new SingleFunctionRenderer(function);
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
        final GraphSimulator sim = new GraphSimulator();

        sim.setPaused(true);
         new AnimationFrame( sim );
    }
}