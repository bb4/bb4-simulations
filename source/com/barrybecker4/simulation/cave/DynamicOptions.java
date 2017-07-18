// Copyright by Barry G. Becker, 2016. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave;

import com.barrybecker4.common.concurrency.ThreadUtil;
import com.barrybecker4.simulation.cave.model.CaveProcessor;
import com.barrybecker4.simulation.cave.model.CaveModel;
import com.barrybecker4.ui.legend.ContinuousColorLegend;
import com.barrybecker4.ui.sliders.SliderGroup;
import com.barrybecker4.ui.sliders.SliderGroupChangeListener;
import com.barrybecker4.ui.sliders.SliderProperties;

import javax.swing.*;
import javax.swing.border.Border;
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
    private static final String BRUSH_RADIUS_SLIDER = "Brush radius";
    private static final String BRUSH_STRENGTH_SLIDER = "Brush strength";
    private static final String EFFECT_FACTOR_SLIDER = "Effect Factor";
    private static final String BUMP_HEIGHT_SLIDER = "Height (for bumps)";
    private static final String SPECULAR_PCT_SLIDER = "Specular Highlight (for bumps)";
    private static final String LIGHT_SOURCE_ELEVATION_SLIDER = "Light source elevation angle (for bumps)";
    private static final String LIGHT_SOURCE_AZYMUTH_SLIDER = "Light azymuthal angle (for bumps)";
    private static final String NUM_STEPS_PER_FRAME_SLIDER = "Mum steps per frame";
    private static final String SCALE_SLIDER = "Scale";
    private static final double PI_D2 = Math.PI / 2.0;
    private static final int PREFERRED_WIDTH = 300;
    private static final int SPACING = 14;

    private SliderGroup generalSliderGroup;
    private SliderGroup bumpSliderGroup;
    private SliderGroup brushSliderGroup;

    private JCheckBox useContinuousIteration;
    private JCheckBox useParallelComputation;
    private CaveExplorer simulator;

    private static final SliderProperties[] GENERAL_SLIDER_PROPS = {

        new SliderProperties(FLOOR_SLIDER,   0,    1.0,    CaveProcessor.DEFAULT_FLOOR_THRESH, 100),
        new SliderProperties(CEILING_SLIDER,   0,    1.0,   CaveProcessor.DEFAULT_CEIL_THRESH, 100),
        new SliderProperties(LOSS_FACTOR_SLIDER,  0,   1.0,  CaveProcessor.DEFAULT_LOSS_FACTOR, 100),
        new SliderProperties(EFFECT_FACTOR_SLIDER,  0,   1.0,  CaveProcessor.DEFAULT_EFFECT_FACTOR, 100),
        new SliderProperties(NUM_STEPS_PER_FRAME_SLIDER,   1,   20,  CaveModel.DEFAULT_NUM_STEPS_PER_FRAME),
        new SliderProperties(SCALE_SLIDER,           1,   20,  CaveModel.DEFAULT_SCALE_FACTOR),
    };

    private static final SliderProperties[] BUMP_SLIDER_PROPS = {
        new SliderProperties(BUMP_HEIGHT_SLIDER,  0.0,   10.0,  CaveModel.DEFAULT_BUMP_HEIGHT, 100),
        new SliderProperties(SPECULAR_PCT_SLIDER,  0.0,   1.0,  CaveModel.DEFAULT_SPECULAR_PCT, 100),
        new SliderProperties(LIGHT_SOURCE_ELEVATION_SLIDER, 0.0, Math.PI/2.0,  CaveModel.DEFAULT_LIGHT_SOURCE_ELEVATION, 100),
        new SliderProperties(LIGHT_SOURCE_AZYMUTH_SLIDER, 0.0, Math.PI,  CaveModel.DEFAULT_LIGHT_SOURCE_AZYMUTH, 100),
    };

    private static final SliderProperties[] BRUSH_SLIDER_PROPS = {

        new SliderProperties(BRUSH_RADIUS_SLIDER,  1,   50,  CaveModel.DEFAULT_BRUSH_RADIUS),
        new SliderProperties(BRUSH_STRENGTH_SLIDER, 0.1,   2.0,  CaveModel.DEFAULT_BRUSH_STRENGTH, 100),
    };

    /**
     * Constructor
     */
    DynamicOptions(CaveModel algorithm, CaveExplorer simulator) {

        this.simulator = simulator;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEtchedBorder());
        setPreferredSize(new Dimension(PREFERRED_WIDTH, 900));

        caveModel = algorithm;

        JPanel generalPanel = createGeneralControls();
        JPanel bumpPanel = createBumpControls();
        JPanel brushPanel = createBrushControls();

        ContinuousColorLegend legend = new ContinuousColorLegend(null, algorithm.getColormap(), true);
        add(createKernalDropdown());
        add(createIncrementPanel());
        add(createButtons());
        add(legend);

        add(Box.createVerticalStrut(SPACING));
        add(generalPanel);
        add(Box.createVerticalStrut(SPACING));
        add(bumpPanel);
        add(Box.createVerticalStrut(SPACING));
        add(brushPanel);

        JPanel fill = new JPanel();
        fill.setPreferredSize(new Dimension(1, 1000));
        add(fill);
    }

    private JPanel createGeneralControls() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(createTitledBorder("General parameters"));

        generalSliderGroup = new SliderGroup(GENERAL_SLIDER_PROPS);
        generalSliderGroup.addSliderChangeListener(this);

        panel.add(generalSliderGroup, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBumpControls() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(createTitledBorder("Bump parameters"));

        bumpSliderGroup = new SliderGroup(BUMP_SLIDER_PROPS);
        bumpSliderGroup.addSliderChangeListener(this);

        panel.add(bumpSliderGroup, BorderLayout.CENTER);
        return panel;
    }

    private Border createTitledBorder(String title) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title),
                BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    private JPanel createBrushControls() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(createTitledBorder("Brush Parameters (left: raise; right: lower)"));

        brushSliderGroup = new SliderGroup(BRUSH_SLIDER_PROPS);
        brushSliderGroup.addSliderChangeListener(this);

        panel.add(brushSliderGroup, BorderLayout.CENTER);
        return panel;
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
        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel("Continuous iteration: ");
        useContinuousIteration = new JCheckBox();
        useContinuousIteration.setSelected(CaveModel.DEFAULT_USE_CONTINUOUS_ITERATION);
        useContinuousIteration.addActionListener(this);

        nextButton = new JButton("Next");
        nextButton.addActionListener(this);
        nextButton.setEnabled(!useContinuousIteration.isSelected());

        panel.add(label, BorderLayout.WEST);
        panel.add(useContinuousIteration, BorderLayout.CENTER);
        panel.add(nextButton, BorderLayout.EAST);
        panel.add(createCheckboxPanel(), BorderLayout.SOUTH);

        return panel;
    }

    /**
     * @return checkbox options.
     */
    private JPanel createCheckboxPanel() {
        JPanel panel = new JPanel();

        JLabel label = new JLabel("Parallel computation: ");
        useParallelComputation = new JCheckBox();
        useParallelComputation.setSelected(CaveProcessor.DEFAULT_USE_PARALLEL);
        useParallelComputation.addActionListener(this);

        panel.add(label);
        panel.add(useParallelComputation);
        panel.add(Box.createHorizontalGlue());

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
        generalSliderGroup.reset();
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
                bumpSliderGroup.setEnabled(SPECULAR_PCT_SLIDER, value > 0);
                bumpSliderGroup.setEnabled(LIGHT_SOURCE_ELEVATION_SLIDER, value > 0);
                break;
            case SPECULAR_PCT_SLIDER:
                caveModel.setSpecularPercent(value);
                break;
            case LIGHT_SOURCE_ELEVATION_SLIDER:
                caveModel.setLightSourceDescensionAngle(PI_D2 - value);
                break;
            case LIGHT_SOURCE_AZYMUTH_SLIDER:
                caveModel.setLightSourceAzymuthAngle(value);
                break;
            case NUM_STEPS_PER_FRAME_SLIDER:
                caveModel.setNumStepsPerFrame((int)value);
                break;
            case SCALE_SLIDER:
                caveModel.setScale(value);
                simulator.getInteractionHandler().setScale(value);
                break;

            case BRUSH_RADIUS_SLIDER:
                simulator.getInteractionHandler().setBrushRadius((int) value);
                break;
            case BRUSH_STRENGTH_SLIDER:
                simulator.getInteractionHandler().setBrushStrength(value);
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
        else if (e.getSource().equals(useContinuousIteration)) {
            boolean useCont = useContinuousIteration.isSelected();
            caveModel.setDefaultUseContinuousIteration(useCont);
            nextButton.setEnabled(!useCont);
            if (!useCont) {
                // do one last step in case the rendering was interrupted.
                ThreadUtil.sleep(100);
                caveModel.requestNextStep();
            }
        }
        else if (e.getSource().equals((useParallelComputation))) {
            caveModel.setUseParallelComputation(useParallelComputation.isSelected());
        }
        else throw new IllegalStateException("Unexpected button " + e.getSource());
    }
}
