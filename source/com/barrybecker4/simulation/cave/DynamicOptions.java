// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave;

import com.barrybecker4.simulation.cave.model.CaveProcessor;
import com.barrybecker4.simulation.cave.model.CaveModel;
import com.barrybecker4.ui.sliders.SliderGroup;
import com.barrybecker4.ui.sliders.SliderGroupChangeListener;
import com.barrybecker4.ui.sliders.SliderProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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
    private static final String SCALE_SLIDER = "Scale";

    private SliderGroup sliderGroup_;
    //private JCheckBox useBumpmapRendering_;

    private static final SliderProperties[] SLIDER_PROPS = {

        new SliderProperties(FLOOR_SLIDER,   0,    1.0,    CaveProcessor.DEFAULT_FLOOR_THRESH, 100),
        new SliderProperties(CEILING_SLIDER,   0,    1.0,   CaveProcessor.DEFAULT_CEIL_THRESH, 100),
        new SliderProperties(LOSS_FACTOR_SLIDER,  0,   1.0,  CaveProcessor.DEFAULT_LOSS_FACTOR, 100),
        new SliderProperties(EFFECT_FACTOR_SLIDER,  0,   1.0,  CaveProcessor.DEFAULT_EFFECT_FACTOR, 100),
        new SliderProperties(BUMP_HEIGHT_SLIDER,  0.0,   10.0,  CaveModel.DEFAULT_BUMP_HEIGHT, 100),
        new SliderProperties(SPECULAR_PCT_SLIDER,  0.0,   1.0,  CaveModel.DEFAULT_SPECULAR_PCT, 100),
        new SliderProperties(SCALE_SLIDER,           1,   20,  CaveModel.DEFAULT_SCALE_FACTOR, 40),
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
        add(createCheckBoxes());
        add(createButtons());

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
    private JPanel createCheckBoxes() {
        JPanel checkboxPanel = new JPanel();

        JLabel label = new JLabel("Use Bump map Rendering: ");
        //useBumpmapRendering_ = new JCheckBox();
        //useBumpmapRendering_.setSelected(CaveModel.DEFAULT_USE_BUMP_MAPPING);
        //useBumpmapRendering_.addActionListener(this);

        checkboxPanel.add(label);
        //checkboxPanel.add(useBumpmapRendering_);
        return checkboxPanel;
    }

    /**
     * The dropdown menu at the top for selecting a kernel type.
     * @return a dropdown/down component.
     */
    private JPanel createButtons() {

        JPanel buttonsPanel = new JPanel();

        nextButton = new JButton("Next");
        resetButton = new JButton("Reset");
        nextButton.addActionListener(this);
        resetButton.addActionListener(this);

        buttonsPanel.add(nextButton);
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
                break;
            case SPECULAR_PCT_SLIDER:
                caveModel.setSpecularPercent(value);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(nextButton)) {
            caveModel.requestNextStep();
        }
        else if (e.getSource().equals(resetButton)) {
            caveModel.requestRestart();
        }
        //else if (e.getSource().equals(useBumpmapRendering_)) {
        //    caveModel.setUseBumpmapping(useBumpmapRendering_.isSelected());
        //}
        else throw new IllegalStateException("Unexpected button " + e.getSource());
    }
}
