/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.common1;

import com.barrybecker4.common.format.FormatUtil;


/**
 * Singleton for simulation profiling.
 * For all simulation we would like to know calculation and rendering times.
 *
 * @author Barry Becker
 */
public class Profiler extends com.barrybecker4.common.profile.Profiler {

    protected static final String RENDERING = "rendering";
    protected static final String CALCULATION = "calculation";

    private static Profiler instance;

    /**
     * @return singleton instance.
     */
    public static Profiler getInstance() {
        if (instance == null) {
            instance = new Profiler();
        }
        return instance;
    }

    /**
     * Private constructor. Use getInstance instead.
     */
    protected Profiler() {
        add(CALCULATION);
        add(RENDERING);
    }

    public void initialize() {
        resetAll();
    }

    @Override
    public void print() {

        if (!isEnabled()) {
            return;
        }
        double ratio = getCalcTime() / getRenderingTime();
        printMessage("-----------------");
        printMessage("Total time = " + (getCalcTime() + getRenderingTime()));
        printMessage("Ratio of calculation to rendering time:" + FormatUtil.formatNumber(ratio) );
        super.print();
    }

    protected double getCalcTime() {
        return getEntry(CALCULATION).getTimeInSeconds();
    }
    protected double getRenderingTime() {
        return getEntry(RENDERING).getTimeInSeconds();
    }

    public void startCalculationTime() {
        this.start(CALCULATION);
    }

    public void stopCalculationTime() {
        this.stop(CALCULATION);
    }

    public void startRenderingTime() {
        this.start(RENDERING);
    }

    public void stopRenderingTime() {
        this.stop(RENDERING);
    }

}
