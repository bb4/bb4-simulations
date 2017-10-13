/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.parameter1;

import com.barrybecker4.common.math.MathUtil;
import com.barrybecker4.common.math.function.InvertibleFunction;
import com.barrybecker4.common.math.function.LinearFunction;
import com.barrybecker4.optimization.parameter.types.Parameter;
import com.barrybecker4.simulation.common1.ui.DistributionSimulator;
import com.barrybecker4.simulation.common1.ui.SimulatorOptionsDialog;
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
    private Parameter parameter = ParameterDistributionType.values()[0].getParameter();
    private boolean showRedistribution = true;

    public ParameterSimulator() {
        super("Parameter Histogram");
        initHistogram();
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
        initHistogram();
    }

    void setShowRedistribution(boolean show) {
        showRedistribution = show;
    }

    boolean isShowRedistribution() {
        return showRedistribution;
    }

    @Override
    protected void initHistogram() {

        if (parameter.isIntegerOnly()) {
            data = new int[(int) parameter.getRange() + 1];
            histogram = new HistogramRenderer(data);
        }
        else {
            data = new int[NUM_DOUBLE_BINS];

            double scale = NUM_DOUBLE_BINS / parameter.getRange();
            double offset = -parameter.getMinValue();
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

        if (showRedistribution) {
            parameter.randomizeValue(MathUtil.RANDOM);
        }
        else {
            //System.out.println("parameter.getRange()="+parameter.getRange());
            //double scale = parameter.isIntegerOnly()?  parameter.getRange() +1.0 : parameter.getRange();
            double scale = parameter.getRange();
            double v = parameter.getMinValue() + MathUtil.RANDOM.nextDouble() * scale;
            parameter.setValue(v);
        }

        return parameter.getValue();
    }

    public static void main( String[] args ) {
        final ParameterSimulator sim = new ParameterSimulator();
        runSimulation(sim);
    }
}