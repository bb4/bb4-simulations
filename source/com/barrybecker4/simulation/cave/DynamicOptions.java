// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave;

import com.barrybecker4.simulation.cave.model.CaveProcessor;
import com.barrybecker4.simulation.cave.model.CaveModel;
import com.barrybecker4.ui.legend.ContinuousColorLegend;
import com.barrybecker4.ui.sliders.SliderGroup;
import com.barrybecker4.ui.sliders.SliderGroupChangeListener;
import com.barrybecker4.ui.sliders.SliderProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Dynamic controls for the RD simulation that will show on the right.
 * They change the behavior of the simulation while it is running.
 * @author Barry Becker
 */
class DynamicOptions extends JPanel
                     implements SliderGroupChangeListener, ItemListener, ActionListener {

    private CaveModel caveModel;

    private Choice kernelChoice;
    private JButton nextButton;
    private JButton resetButton;

    private static final String FLOOR_SLIDER = "Floor";
    private static final String CEILING_SLIDER = "Ceiling";
    private static final String LOSS_FACTOR_SLIDER = "Loss Factor";
    private static final String EFFECT_FACTOR_SLIDER = "Effect Factor";
    private static final String BUMP_HEIGHT_SLIDER = "Height (for bumps)";
    private static final String SPECULAR_PCT_SLIDER = "Specular Highlight (for bumps)";
    private static final String LIGHT_SOURCE_ELEVATION_SLIDER = "Light source elevation angle (for bumps)";
    private static final String SCALE_SLIDER = "Scale";
    private static final double PI_D2 = Math.PI / 2.0;

    private SliderGroup sliderGroup_;
    private JCheckBox useContinuousIteration_;
    private CaveExplorer simulator_;

    private static final SliderProperties[] SLIDER_PROPS = {

        new SliderProperties(FLOOR_SLIDER,   0,    1.0,    CaveProcessor.DEFAULT_FLOOR_THRESH, 100),
        new SliderProperties(CEILING_SLIDER,   0,    1.0,   CaveProcessor.DEFAULT_CEIL_THRESH, 100),
        new SliderProperties(LOSS_FACTOR_SLIDER,  0,   1.0,  CaveProcessor.DEFAULT_LOSS_FACTOR, 100),
        new SliderProperties(EFFECT_FACTOR_SLIDER,  0,   1.0,  CaveProcessor.DEFAULT_EFFECT_FACTOR, 100),
        new SliderProperties(BUMP_HEIGHT_SLIDER,  0.0,   10.0,  CaveModel.DEFAULT_BUMP_HEIGHT, 100),
        new SliderProperties(SPECULAR_PCT_SLIDER,  0.0,   1.0,  CaveModel.DEFAULT_SPECULAR_PCT, 100),
        new SliderProperties(LIGHT_SOURCE_ELEVATION_SLIDER, 0.0, Math.PI/2.0,  CaveModel.DEFAULT_LIGHT_SOURCE_ELEVATION, 100),
        new SliderProperties(SCALE_SLIDER,           1,   20,  CaveModel.DEFAULT_SCALE_FACTOR),
    };

    /**
     * Constructor
     */
    DynamicOptions(CaveModel algorithm, CaveExplorer simulator) {

        simulator_ = simulator;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEtchedBorder());
        setPreferredSize(new Dimension(300, 300));

        caveModel = algorithm;

        sliderGroup_ = new SliderGroup(SLIDER_PROPS);
        sliderGroup_.addSliderChangeListener(this);

        ContinuousColorLegend legend = new ContinuousColorLegend(null, algorithm.getColormap(), true);

        add(sliderGroup_);
        add(createKernalDropdown());
        add(createIncrementPanel());
        add(createButtons());
        add(legend);

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

    /**
     * The dropdown menu at the top for selecting a kernel type.
     * @return a dropdown/down component.
     */
    private JPanel createIncrementPanel() {
        JPanel panel = new JPanel();

        JLabel label = new JLabel("Continuous iteration: ");
        useContinuousIteration_ = new JCheckBox();
        useContinuousIteration_.setSelected(CaveModel.DEFAULT_USE_CONTINUOUS_ITERATION);
        useContinuousIteration_.addActionListener(this);

        nextButton = new JButton("Next");
        nextButton.addActionListener(this);
        nextButton.setEnabled(!useContinuousIteration_.isSelected());

        panel.add(label);
        panel.add(useContinuousIteration_);
        panel.add(Box.createHorizontalGlue());
        panel.add(nextButton);

        return panel;
    }

    /**
     * The dropdown menu at the top for selecting a kernel type.
     * @return a dropdown/down component.
     */
    private JPanel createButtons() {
        JPanel buttonsPanel = new JPanel();
        resetButton = new JButton("Reset");
        resetButton.addActionListener(this);
        buttonsPanel.add(resetButton);
        return buttonsPanel;
    }

    public void reset() {
        sliderGroup_.reset();
    }

    /**
     * One of the sliders was moved.
     */
    public void sliderChanged(int sliderIndex, String sliderName, double value) {

        switch (sliderName) {
            case FLOOR_SLIDER:
                caveModel.setFloorThresh(value);
                break;
            case CEILING_SLIDER:
                caveModel.setCeilThresh(value);
                break;
            case LOSS_FACTOR_SLIDER:
                caveModel.setLossFactor(value);
                break;
            case EFFECT_FACTOR_SLIDER:
                caveModel.setEffectFactor(value);
                break;
            case BUMP_HEIGHT_SLIDER:
                caveModel.setBumpHeight(value);
                // specular highlight does not apply if no bumps
                sliderGroup_.setEnabled(SPECULAR_PCT_SLIDER, value > 0);
                sliderGroup_.setEnabled(LIGHT_SOURCE_ELEVATION_SLIDER, value > 0);
                break;
            case SPECULAR_PCT_SLIDER:
                caveModel.setSpecularPercent(value);
                break;
            case LIGHT_SOURCE_ELEVATION_SLIDER:
                caveModel.setLightSourceDescensionAngle(PI_D2 - value);
                break;
            case SCALE_SLIDER:
                caveModel.setScale(value);
                simulator_.getInteractionHandler().setScale(value);
                break;
            default: throw new IllegalArgumentException("Unexpected slider: " + sliderName);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        CaveProcessor.KernelType type = CaveProcessor.KernelType.valueOf(kernelChoice.getSelectedItem());
        caveModel.setKernelType(type);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(nextButton)) {
            caveModel.requestNextStep();
        }
        else if (e.getSource().equals(resetButton)) {
            caveModel.requestRestart();
        }
        else if (e.getSource().equals(useContinuousIteration_)) {
            caveModel.setDefaultUseContinuousIteration(useContinuousIteration_.isSelected());
            nextButton.setEnabled(!useContinuousIteration_.isSelected());
        }
        else throw new IllegalStateException("Unexpected button " + e.getSource());
    }
}
