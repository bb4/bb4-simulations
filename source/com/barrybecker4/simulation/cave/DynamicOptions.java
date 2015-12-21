// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave;

import com.barrybecker4.simulation.cave.model.CaveMap;
import com.barrybecker4.simulation.cave.model.CaveModel;
import com.barrybecker4.ui.sliders.SliderGroup;
import com.barrybecker4.ui.sliders.SliderGroupChangeListener;
import com.barrybecker4.ui.sliders.SliderProperties;

import javax.swing.*;
import java.awt.*;

/**
 * Dynamic controls for the RD simulation that will show on the right.
 * They change the behavior of the simulation while it is running.
 * @author Barry Becker
 */
class DynamicOptions extends JPanel
                     implements SliderGroupChangeListener {

    private CaveModel caveModel;
    //private CaveExplorer simulator_;
    private double density;


    private static final String NUM_ITERATIONS_SLIDER = "Num Iterations";
    private static final String DENSITY_SLIDER = "Density";
    private static final String BIRTH_THRESHOLD_SLIDER = "Birth Threshold";
    private static final String STARVATION_LIMIT_SLIDER = "Starvation Limit";
    private static final String SCALE_SLIDER = "Scale";
    //private static final String HEIGHT_SLIDER = "Height";

    private SliderGroup sliderGroup_;
    private JTextArea formulaText_;

    private static final SliderProperties[] SLIDER_PROPS = {

        new SliderProperties(NUM_ITERATIONS_SLIDER,   0,    10,    CaveModel.DEFAULT_MAX_ITERATIONS),
        new SliderProperties(DENSITY_SLIDER,   0,    1.0,    CaveMap.DEFAULT_DENSITY, 100),
        new SliderProperties(BIRTH_THRESHOLD_SLIDER,   0,    9,   CaveMap.DEFAULT_BIRTH_THRESHOLD),
        new SliderProperties(STARVATION_LIMIT_SLIDER,  0,    9,   CaveMap.DEFAULT_STARVATION_LIMIT),
        new SliderProperties(SCALE_SLIDER,             1,    20,   CaveModel.DEFAULT_SCALE, 100),
    };


    /**
     * Constructor
     */
    DynamicOptions(CaveModel algorithm, CaveExplorer simulator) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEtchedBorder());
        setPreferredSize(new Dimension(300, 300));

        caveModel = algorithm;
        //simulator_ = simulator;

        sliderGroup_ = new SliderGroup(SLIDER_PROPS);
        sliderGroup_.addSliderChangeListener(this);

        add(sliderGroup_);
        JPanel fill = new JPanel();
        fill.setPreferredSize(new Dimension(1, 1000));
        add(fill);
    }


    public void reset() {
        sliderGroup_.reset();
    }

    /**
     * One of the sliders was moved.
     */
    public void sliderChanged(int sliderIndex, String sliderName, double value) {

        switch (sliderName) {
            case NUM_ITERATIONS_SLIDER:
                caveModel.setMaxIterations((int) value);
                break;
            case DENSITY_SLIDER:
                caveModel.setDensity(value);
                break;
            case BIRTH_THRESHOLD_SLIDER:
                caveModel.setBirthThreshold((int) value);
                break;
            case STARVATION_LIMIT_SLIDER:
                caveModel.setStarvationLimit((int) value);
                break;
            case SCALE_SLIDER:
                caveModel.setScale(value);
                break;
            default: throw new IllegalArgumentException("Unexpected slider: " + sliderName);
        }
    }


}
