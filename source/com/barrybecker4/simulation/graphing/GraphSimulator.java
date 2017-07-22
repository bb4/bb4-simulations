/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.graphing;

import com.barrybecker4.common.math.function.Function;
import com.barrybecker4.simulation.common.ui.Simulator;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;
import com.barrybecker4.ui.animation.AnimationFrame;
import com.barrybecker4.ui.renderers.SingleFunctionRenderer;

import java.awt.*;


/**
 * Simluates graphing a function
 *
 * @author Barry Becker
 */
public class GraphSimulator extends Simulator {

    SingleFunctionRenderer graph_;
    Function function_;


    public GraphSimulator() {
        super("Graph");
        initGraph();
    }

    public void setFunction(Function function) {
        function_ = function;
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

    protected void initGraph() {
        if (function_ == null) {

            function_ = FunctionType.DIAGONAL.function;
        }
        graph_ = new SingleFunctionRenderer(function_);
    }

    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
         return new GraphOptionsDialog(frame, this );
    }

    @Override
    public void paint( Graphics g ) {
        graph_.setSize(getWidth(), getHeight());
        graph_.paint(g);
    }

    public static void main( String[] args ) {
        final GraphSimulator sim = new GraphSimulator();

        sim.setPaused(true);
         new AnimationFrame( sim );
    }
}