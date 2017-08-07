// Copyright by Barry G. Becker, 2016. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.conway;

import com.barrybecker4.common.concurrency.ThreadUtil;
import com.barrybecker4.simulation.conway.model.ConwayModel;
import com.barrybecker4.simulation.conway.model.ConwayProcessor;
import com.barrybecker4.ui.legend.ContinuousColorLegend;
import com.barrybecker4.ui.sliders.SliderGroup;
import com.barrybecker4.ui.sliders.SliderGroupChangeListener;
import com.barrybecker4.ui.sliders.SliderProperties;

import javax.swing.*;
import javax.swing.border.Border;
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
                     implements SliderGroupChangeListener, ActionListener, ItemListener {

    private ConwayModel conwayModel;

    private JButton nextButton;
    private Choice ruleChoice;

    private static final String NUM_STEPS_PER_FRAME_SLIDER = "Mum steps per frame";
    private static final String SCALE_SLIDER = "Scale";
    private static final double PI_D2 = Math.PI / 2.0;
    private static final int PREFERRED_WIDTH = 300;
    private static final int SPACING = 14;

    private SliderGroup generalSliderGroup;

    private JCheckBox useContinuousIteration;
    private JCheckBox useParallelComputation;

    private JCheckBox showShadowsCheckbox;
    private JCheckBox wrapCheckbox;

    private ConwayExplorer simulator_;

    private static final SliderProperties[] GENERAL_SLIDER_PROPS = {
        new SliderProperties(NUM_STEPS_PER_FRAME_SLIDER,   1,   20,  ConwayModel.DEFAULT_NUM_STEPS_PER_FRAME),
        new SliderProperties(SCALE_SLIDER,           1,   20,  ConwayModel.DEFAULT_SCALE_FACTOR),
    };


    /**
     * Constructor
     */
    DynamicOptions(ConwayModel algorithm, ConwayExplorer simulator) {

        simulator_ = simulator;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEtchedBorder());
        setPreferredSize(new Dimension(PREFERRED_WIDTH, 900));

        conwayModel = algorithm;

        JPanel generalPanel = createGeneralControls();
        JPanel brushPanel = createBrushControls();

        ContinuousColorLegend legend = new ContinuousColorLegend(null, algorithm.getColormap(), true);
        add(createIncrementPanel());
        add(createButtons());
        add(createRuleDropdown());
        add(legend);

        add(Box.createVerticalStrut(SPACING));
        add(generalPanel);
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

    private Border createTitledBorder(String title) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title),
                BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    private JPanel createBrushControls() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(createTitledBorder("Brush Parameters (left: raise; right: lower)"));
        return panel;
    }

    /**
     * The dropdown menu at the top for selecting a kernel type.
     * @return a dropdown/down component.
     */
    private JPanel createIncrementPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        useContinuousIteration = createCheckbox("Continuous iteration", ConwayModel.DEFAULT_USE_CONTINUOUS_ITERATION);

        nextButton = new JButton("Next");
        nextButton.addActionListener(this);
        nextButton.setEnabled(!useContinuousIteration.isSelected());

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
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        useParallelComputation = createCheckbox("Parallel computation", ConwayProcessor.DEFAULT_USE_PARALLEL);
        showShadowsCheckbox = createCheckbox("Show shadows", ConwayModel.DEFAULT_SHOW_SHADOWS);
        wrapCheckbox = createCheckbox("Wrap grid", ConwayModel.DEFAULT_WRAP_GRID);

        panel.add(useParallelComputation);
        panel.add(showShadowsCheckbox);
        panel.add(wrapCheckbox);
        return panel;
    }

    private JCheckBox createCheckbox(String labelText, boolean defaultValue) {
        JCheckBox cb = new JCheckBox(labelText);
        cb.setSelected(defaultValue);
        cb.addActionListener(this);
        return cb;
    }

    /**
     * The dropdown menu for selecting a rule type.
     * @return a dropdown/down component.
     */
    private JPanel createRuleDropdown() {

        JPanel ruleChoicePanel = new JPanel();
        JLabel label = new JLabel("Rule to apply: ");

        ruleChoice = new Choice();
        for (Enum ruleType: ConwayProcessor.RuleType.values()) {
            ruleChoice.add(ruleType.name());
        }
        ruleChoice.select(ConwayProcessor.DEFAULT_RULE_TYPE.ordinal());
        ruleChoice.addItemListener(this);

        ruleChoicePanel.add(label);
        ruleChoicePanel.add(ruleChoice);
        return ruleChoicePanel;
    }

    /**
     * The dropdown menu at the top for selecting a kernel type.
     * @return a dropdown/down component.
     */
    private JPanel createButtons() {
        JPanel buttonsPanel = new JPanel();
        //resetButton = new JButton("Reset");
        //resetButton.addActionListener(this);
        //buttonsPanel.add(resetButton);
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
            case NUM_STEPS_PER_FRAME_SLIDER:
                conwayModel.setNumStepsPerFrame((int)value);
                break;
            case SCALE_SLIDER:
                conwayModel.setScale((int)value);
                simulator_.getInteractionHandler().setScale(value);
                break;
            default: throw new IllegalArgumentException("Unexpected slider: " + sliderName);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        ConwayProcessor.RuleType type = ConwayProcessor.RuleType.valueOf(ruleChoice.getSelectedItem());
        conwayModel.setRuleType(type);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Component source = (Component) e.getSource();

        if (source.equals(nextButton)) {
            conwayModel.requestNextStep();
        }
        else if (source.equals(useContinuousIteration)) {
            boolean useCont = useContinuousIteration.isSelected();
            conwayModel.setDefaultUseContinuousIteration(useCont);
            nextButton.setEnabled(!useCont);
            if (!useCont) {
                // do one last step in case the rendering was interrupted.
                ThreadUtil.sleep(100);
                conwayModel.requestNextStep();
            }
        }
        else if (source.equals((useParallelComputation))) {
            conwayModel.setUseParallelComputation(useParallelComputation.isSelected());
        }
        else if (source.equals(wrapCheckbox)) {
            conwayModel.setWrapGrid(wrapCheckbox.isSelected());
            conwayModel.requestRestart();
        }
        else if (source.equals(showShadowsCheckbox)) {
            conwayModel.setShowShadows(showShadowsCheckbox.isSelected());
            conwayModel.requestRestart();
        }
        else throw new IllegalStateException("Unexpected button " + e.getSource());
    }
}
