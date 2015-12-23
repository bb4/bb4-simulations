// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave;

import com.barrybecker4.simulation.cave.model.CaveProcessor;
import com.barrybecker4.simulation.cave.model.CaveModel;
import com.barrybecker4.ui.sliders.SliderGroup;
import com.barrybecker4.ui.sliders.SliderGroupChangeListener;
import com.barrybecker4.ui.sliders.SliderProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Dynamic controls for the RD simulation that will show on the right.
 * They change the behavior of the simulation while it is running.
 * @author Barry Becker
 */
class DynamicOptions extends JPanel
                     implements SliderGroupChangeListener, ItemListener {

    private CaveModel caveModel;

    private Choice kernelChoice;

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
        new SliderProperties(DENSITY_SLIDER,   0,    1.0,    CaveProcessor.DEFAULT_DENSITY, 100),
        new SliderProperties(BIRTH_THRESHOLD_SLIDER,   0,    9,   CaveProcessor.DEFAULT_BIRTH_THRESHOLD),
        new SliderProperties(STARVATION_LIMIT_SLIDER,  0,    9,   CaveProcessor.DEFAULT_STARVATION_LIMIT),
        new SliderProperties(SCALE_SLIDER,             1,    20,  CaveModel.DEFAULT_SCALE, 40),
    };


    /**
     * Constructor
     */
    DynamicOptions(CaveModel algorithm, CaveExplorer simulator) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEtchedBorder());
        setPreferredSize(new Dimension(300, 300));

        caveModel = algorithm;

        sliderGroup_ = new SliderGroup(SLIDER_PROPS);
        sliderGroup_.addSliderChangeListener(this);

        add(sliderGroup_);
        add(createKernalDropdown());

        JPanel fill = new JPanel();
        fill.setPreferredSize(new Dimension(1, 1000));
        add(fill);
    }

    /**
     * The dropdown menu at the top for selecting a kernel type.
     * @return a dropdown/down component.
     */
    private JPanel createKernalDropdown() {

        JPanel kernelChoicePanel = new JPanel();
        JLabel label = new JLabel("Kernal type: ");

        kernelChoice = new Choice();
        for (Enum kernelType: CaveProcessor.KernelType.values()) {
            kernelChoice.add(kernelType.name());
        }
        kernelChoice.select(CaveProcessor.DEFAULT_KERNEL_TYPE.ordinal());
        kernelChoice.addItemListener(this);

        kernelChoicePanel.add(label);
        kernelChoicePanel.add(kernelChoice);
        return kernelChoicePanel;
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


    @Override
    public void itemStateChanged(ItemEvent e) {
        CaveProcessor.KernelType type = CaveProcessor.KernelType.valueOf(kernelChoice.getSelectedItem());
        caveModel.setKernelType(type);
    }
}
