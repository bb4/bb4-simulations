/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.parameter;

import com.barrybecker4.common.math.MathUtil;
import com.barrybecker4.common.math.function.InvertibleFunction;
import com.barrybecker4.common.math.function.LinearFunction;
import com.barrybecker4.optimization.parameter.types.Parameter;
import com.barrybecker4.simulation.common.ui.DistributionSimulator;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;
import com.barrybecker4.ui.renderers.HistogramRenderer;

/**
 * To see what kind of distribution of numbers you get.
 * If showRedistribution is true, then the plot should show uniform because
 * the redistribution of a function applied to that function should be uniform.
 *
 * @author Barry Becker
 */
public class ParameterSimulator extends DistributionSimulator {

    private static final int NUM_DOUBLE_BINS = 1000;

    /** initialize with some default */
    private Parameter parameter_ = ParameterDistributionType.values()[0].getParameter();

    private boolean showRedistribution_ = true;

    private ParameterSimulator() {
        super("Parameter Histogram");
        initHistogram();
    }

    public void setParameter(Parameter parameter) {
        parameter_ = parameter;
        initHistogram();
    }

    void setShowRedistribution(boolean show) {
        showRedistribution_ = show;
    }

    boolean isShowRedistribution() {
        return showRedistribution_;
    }

    @Override
    protected void initHistogram() {

        if (parameter_.isIntegerOnly()) {
            data = new int[(int)parameter_.getRange() + 1];
            histogram = new HistogramRenderer(data);
        }
        else {
            data = new int[NUM_DOUBLE_BINS];

            double scale = NUM_DOUBLE_BINS / parameter_.getRange();
            double offset = -parameter_.getMinValue();
            //System.out.println("new Lin scale = " +scale + " off="+ offset);
            InvertibleFunction xFunc = new LinearFunction(scale, offset);

            histogram = new HistogramRenderer(data, xFunc);
        }
    }

    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
         return new ParameterOptionsDialog(frame, this );
    }

    @Override
    protected double getXPositionToIncrement() {

        if (showRedistribution_) {
            parameter_.randomizeValue(MathUtil.RANDOM);
        }
        else {
            //System.out.println("parameter_.getRange()="+parameter_.getRange());
            //double scale = parameter_.isIntegerOnly()?  parameter_.getRange() +1.0 : parameter_.getRange();
            double scale = parameter_.getRange();
            double v = parameter_.getMinValue() + MathUtil.RANDOM.nextDouble() * scale;
            parameter_.setValue(v);
        }

        return parameter_.getValue();
    }

    public static void main( String[] args ) {
        final ParameterSimulator sim = new ParameterSimulator();
        runSimulation(sim);
    }
}


