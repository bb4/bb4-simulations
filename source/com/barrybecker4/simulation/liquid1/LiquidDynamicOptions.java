/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid1;

import com.barrybecker4.simulation.liquid1.compute.GridUpdater;
import com.barrybecker4.ui.sliders.SliderGroup;
import com.barrybecker4.ui.sliders.SliderGroupChangeListener;
import com.barrybecker4.ui.sliders.SliderProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dynamic controls for the liquid simulation that will show on the right.
 * They change the behavior of the simulation while it is running.
 * @author Barry Becker
 */
class LiquidDynamicOptions extends JPanel
                          implements SliderGroupChangeListener, ActionListener {

    private LiquidSimulator liquidSim;

    private static final String VISCOSITY_SLIDER = "Viscosity";
    private static final String B0_SLIDER = "Relaxation Coefficient";
    private static final String TIMESTEP_SLIDER = "Time Step Size";

    private SliderGroup sliderGroup;

    private JCheckBox advectionOnlyCheckBox;

    /**
     * Constructor
     */
    LiquidDynamicOptions(LiquidSimulator liquid) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEtchedBorder());
        setPreferredSize(new Dimension(300, 300));

        liquidSim = liquid;

        sliderGroup = new SliderGroup(createSliderProperties());
        sliderGroup.addSliderChangeListener(this);

        add(sliderGroup);

        advectionOnlyCheckBox =
                createCheckBox("Do advection only",
                    "If checked we will not apply the Navier Stokes solver",
                    liquidSim.getAdvectionOnly() );
        add(advectionOnlyCheckBox);

        JPanel fill = new JPanel();
        fill.setPreferredSize(new Dimension(10, 1000));
        add(fill);
    }


    protected JCheckBox createCheckBox(String label, String ttip, boolean initialValue) {
        JCheckBox cb = new JCheckBox(label, initialValue);
        cb.setToolTipText(ttip);
        cb.addActionListener(this);
        return cb;
    }


    private SliderProperties[] createSliderProperties() {

        SliderProperties[] sliderProps;
        sliderProps = new SliderProperties[] {
                //                                      MIN   MAX    INITIAL    SCALE
                new SliderProperties(VISCOSITY_SLIDER, 0.0, 0.1, GridUpdater.DEFAULT_VISCOSITY, 100),
                new SliderProperties(B0_SLIDER, 1.0, 2.0, GridUpdater.DEFAULT_B0, 100),
                new SliderProperties(TIMESTEP_SLIDER, 0.001, 0.4, 0.01, 1000)};
         return sliderProps;
    }


    public void reset() {
        sliderGroup.reset();
    }

    /**
     * One of the sliders was moved.
     */
    public void sliderChanged(int sliderIndex, String sliderName, double value) {

        if (sliderName.equals(VISCOSITY_SLIDER)) {
            liquidSim.getEnvironment().setViscosity(value);
        }
        else if (sliderName.equals(B0_SLIDER)) {
            liquidSim.getEnvironment().setB0(value);
        }
        else if (sliderName.equals(TIMESTEP_SLIDER)) {
            liquidSim.setTimeStep(value);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == advectionOnlyCheckBox) {
            liquidSim.setAdvectionOnly(advectionOnlyCheckBox.isSelected());
        }
    }
}
