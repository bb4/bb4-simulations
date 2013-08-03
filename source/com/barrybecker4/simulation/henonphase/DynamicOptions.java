/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.henonphase;

import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.simulation.henonphase.algorithm.HenonAlgorithm;
import com.barrybecker4.simulation.henonphase.algorithm.TravelerParams;
import com.barrybecker4.ui.legend.ContinuousColorLegend;
import com.barrybecker4.ui.sliders.SliderGroup;
import com.barrybecker4.ui.sliders.SliderGroupChangeListener;
import com.barrybecker4.ui.sliders.SliderProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dynamic controls for the RD simulation that will show on the right.
 * They change the behavior of the simulation while it is running.
 * @author Barry Becker
 */
class DynamicOptions extends JPanel
                     implements ActionListener, SliderGroupChangeListener {

    private HenonAlgorithm algorithm_;
    private HenonPhaseExplorer simulator_;
    private JCheckBox useConcurrency_;
    private JCheckBox useFixedSize_;
    private JCheckBox useUniformSeeds_;
    private JCheckBox connectPoints_;

    private static final String PHASE_ANGLE_SLIDER = "Phase Angle";
    private static final String MULTIPLIER_SLIDER = "Multiplier";
    private static final String OFFSET_SLIDER = "Offset";
    private static final String ALPHA_SLIDER = "Alpha";
    private static final String NUM_TRAVELORS_SLIDER = "Num Travelor Particles";
    private static final String ITER_PER_FRAME_SLIDER = "Num Iterations per Frame";
    private static final String ITER_SLIDER = "Max Iterations";

    private SliderGroup sliderGroup_;
    private JTextArea formulaText_;

    private TravelerParams currentParams = new TravelerParams() ;


    private static final SliderProperties[] SLIDER_PROPS = {

        new SliderProperties(PHASE_ANGLE_SLIDER,   0,    2.0 * Math.PI,    TravelerParams.DEFAULT_PHASE_ANGLE,  1000.0),
        new SliderProperties(MULTIPLIER_SLIDER,   0.9,    1.1,    TravelerParams.DEFAULT_MULTIPLIER,  1000.0),
        new SliderProperties(OFFSET_SLIDER,     -0.2,    0.2,    TravelerParams.DEFAULT_OFFSET,  1000.0),
        new SliderProperties(ALPHA_SLIDER,     1,    255,    100),
        new SliderProperties(NUM_TRAVELORS_SLIDER,  1,  10000,    HenonAlgorithm.DEFAULT_NUM_TRAVELERS),
        new SliderProperties(ITER_PER_FRAME_SLIDER, 1,  HenonAlgorithm.DEFAULT_MAX_ITERATIONS/10, HenonAlgorithm.DEFAULT_FRAME_ITERATIONS),
        new SliderProperties(ITER_SLIDER,        100,     100000,    HenonAlgorithm.DEFAULT_MAX_ITERATIONS),
    };


    /**
     * Constructor
     */
    DynamicOptions(HenonAlgorithm algorithm, HenonPhaseExplorer simulator) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEtchedBorder());
        setPreferredSize(new Dimension(300, 300));

        algorithm_ = algorithm;
        simulator_ = simulator;

        sliderGroup_ = new SliderGroup(SLIDER_PROPS);
        sliderGroup_.addSliderChangeListener(this);

        ContinuousColorLegend legend_ =
                new ContinuousColorLegend(null, algorithm_.getColorMap(), true);

        JPanel checkBoxes = createCheckBoxes();
        add(sliderGroup_);
        add(Box.createVerticalStrut(10));
        add(checkBoxes);
        add(Box.createVerticalStrut(10));
        add(legend_);
        JPanel fill = new JPanel();
        fill.setPreferredSize(new Dimension(1, 1000));
        add(fill);
        add(createFormulaText());
    }

    private JPanel createCheckBoxes() {

        /*
        useConcurrency_ = new JCheckBox("Parallel", algorithm_.isParallelized());
        useConcurrency_.setToolTipText(
                "Take advantage of multiple processors for calculation and rendering if present.");
        useConcurrency_.addActionListener(this);    */

        useFixedSize_ = new JCheckBox("Fixed Size", simulator_.getUseFixedSize());
        useFixedSize_.addActionListener(this);

        useUniformSeeds_ = new JCheckBox("Uniform seeds", algorithm_.getUseUniformSeeds());
        useUniformSeeds_.addActionListener(this);

        connectPoints_ = new JCheckBox("Connect points", algorithm_.getConnectPoints());
        connectPoints_.addActionListener(this);

        JPanel checkBoxes = new JPanel(new GridLayout(0, 1));

        //checkBoxes.add(useConcurrency_);
        checkBoxes.add(useFixedSize_);
        checkBoxes.add(useUniformSeeds_);
        checkBoxes.add(connectPoints_);

        checkBoxes.setBorder(BorderFactory.createEtchedBorder());
        return checkBoxes;
    }


    private JPanel createFormulaText() {

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout());

        formulaText_ = new JTextArea();
        formulaText_.setEditable(false);
        formulaText_.setBackground(getBackground());
        updateFormulaText();

        textPanel.add(formulaText_, BorderLayout.CENTER);
        return textPanel;
    }

    private void updateFormulaText() {

        StringBuilder text = new StringBuilder();

        text.append("term = ");
        if (currentParams.isDefaultMultiplier()) {
            text.append(FormatUtil.formatNumber(currentParams.getMultiplier())).append(" * ");
        }
        text.append("y");

        if (currentParams.isDefaultOffset()) {
            text.append(" + ").append(FormatUtil.formatNumber(currentParams.getOffset()));
        }
        text.append(" - x * x");

        text.append("\n");
        String angle = FormatUtil.formatNumber(currentParams.getAngle());
        text.append("x' = x * cos(").append(angle).append(") - term * sin(").append(angle).append(")\n");
        text.append("y' = x * sin(").append(angle).append(") + term * cos(").append(angle).append(")");

        formulaText_.setText(text.toString());
    }

    public void reset() {
        sliderGroup_.reset();
    }

    /**
     * One of the buttons was pressed.
     */
    public void actionPerformed(ActionEvent e) {

        /*
        if (e.getSource() == useConcurrency_) {
            boolean isParallelized = !algorithm_.isParallelized();
            algorithm_.setParallelized(isParallelized);
        } */
        if (e.getSource() == useFixedSize_) {
            simulator_.setUseFixedSize(useFixedSize_.isSelected());
        }
        else if (e.getSource() == useUniformSeeds_) {
            algorithm_.toggleUseUniformSeeds();
        }
        else if (e.getSource() == connectPoints_) {
            algorithm_.toggleConnectPoints();
        }
    }

    /**
     * One of the sliders was moved.
     */
    public void sliderChanged(int sliderIndex, String sliderName, double value) {

        if (sliderName.equals(PHASE_ANGLE_SLIDER)) {
            currentParams = new TravelerParams(value, currentParams.getMultiplier(), currentParams.getOffset());
            algorithm_.setTravelerParams(currentParams);
            updateFormulaText();
        }
        else if (sliderName.equals(MULTIPLIER_SLIDER)) {
            currentParams = new TravelerParams(currentParams.getAngle(), value, currentParams.getOffset());
            algorithm_.setTravelerParams(currentParams);
            updateFormulaText();
        }
        else if (sliderName.equals(OFFSET_SLIDER)) {
            currentParams =  new TravelerParams(currentParams.getAngle(), currentParams.getMultiplier(), value);
            algorithm_.setTravelerParams(currentParams);
            updateFormulaText();
        }
        else if (sliderName.equals(ALPHA_SLIDER)) {
            algorithm_.setAlpha((int) value);
        }
        else if (sliderName.equals(NUM_TRAVELORS_SLIDER)) {
            algorithm_.setNumTravelors((int) value);
        }
        else if (sliderName.equals(ITER_PER_FRAME_SLIDER)) {
            algorithm_.setStepsPerFrame((int) value);
        }
        else if (sliderName.equals(ITER_SLIDER)) {
            algorithm_.setMaxIterations((int)value);
        }
    }

}
