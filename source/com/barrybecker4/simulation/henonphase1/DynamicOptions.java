/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.henonphase1;

import com.barrybecker4.common.format.FormatUtil;
import com.barrybecker4.simulation.henonphase1.algorithm.HenonAlgorithm;
import com.barrybecker4.simulation.henonphase1.algorithm.TravelerParams;
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

    private HenonAlgorithm algorithm;
    private HenonPhaseExplorer simulator;
    private JCheckBox useFixedSize;
    private JCheckBox useUniformSeeds;
    private JCheckBox connectPoints;

    private static final String PHASE_ANGLE_SLIDER = "Phase Angle";
    private static final String MULTIPLIER_SLIDER = "Multiplier";
    private static final String OFFSET_SLIDER = "Offset";
    private static final String ALPHA_SLIDER = "Alpha";
    private static final String NUM_TRAVELORS_SLIDER = "Num Travelor Particles";
    private static final String ITER_PER_FRAME_SLIDER = "Num Iterations per Frame";
    private static final String ITER_SLIDER = "Max Iterations";

    private SliderGroup sliderGroup;
    private JTextArea formulaText;

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

        this.algorithm = algorithm;
        this.simulator = simulator;

        sliderGroup = new SliderGroup(SLIDER_PROPS);
        sliderGroup.addSliderChangeListener(this);

        ContinuousColorLegend legend =
                new ContinuousColorLegend(null, this.algorithm.getColorMap(), true);

        JPanel checkBoxes = createCheckBoxes();
        add(sliderGroup);
        add(Box.createVerticalStrut(10));
        add(checkBoxes);
        add(Box.createVerticalStrut(10));
        add(legend);
        JPanel fill = new JPanel();
        fill.setPreferredSize(new Dimension(1, 1000));
        add(fill);
        add(createFormulaText());
    }

    private JPanel createCheckBoxes() {

        /*
        useConcurrency = new JCheckBox("Parallel", algorithm.isParallelized());
        useConcurrency.setToolTipText(
                "Take advantage of multiple processors for calculation and rendering if present.");
        useConcurrency.addActionListener(this);    */

        useFixedSize = new JCheckBox("Fixed Size", simulator.getUseFixedSize());
        useFixedSize.addActionListener(this);

        useUniformSeeds = new JCheckBox("Uniform seeds", algorithm.getUseUniformSeeds());
        useUniformSeeds.addActionListener(this);

        connectPoints = new JCheckBox("Connect points", algorithm.getConnectPoints());
        connectPoints.addActionListener(this);

        JPanel checkBoxes = new JPanel(new GridLayout(0, 1));

        //checkBoxes.add(useConcurrency);
        checkBoxes.add(useFixedSize);
        checkBoxes.add(useUniformSeeds);
        checkBoxes.add(connectPoints);

        checkBoxes.setBorder(BorderFactory.createEtchedBorder());
        return checkBoxes;
    }


    private JPanel createFormulaText() {

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout());

        formulaText = new JTextArea();
        formulaText.setEditable(false);
        formulaText.setBackground(getBackground());
        updateFormulaText();

        textPanel.add(formulaText, BorderLayout.CENTER);
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

        formulaText.setText(text.toString());
    }

    public void reset() {
        sliderGroup.reset();
    }

    /**
     * One of the buttons was pressed.
     */
    public void actionPerformed(ActionEvent e) {

        /*
        if (e.getSource() == useConcurrency) {
            boolean isParallelized = !algorithm.isParallelized();
            algorithm.setParallelized(isParallelized);
        } */
        if (e.getSource() == useFixedSize) {
            simulator.setUseFixedSize(useFixedSize.isSelected());
        }
        else if (e.getSource() == useUniformSeeds) {
            algorithm.toggleUseUniformSeeds();
        }
        else if (e.getSource() == connectPoints) {
            algorithm.toggleConnectPoints();
        }
    }

    /**
     * One of the sliders was moved.
     */
    public void sliderChanged(int sliderIndex, String sliderName, double value) {

        if (sliderName.equals(PHASE_ANGLE_SLIDER)) {
            currentParams = new TravelerParams(value, currentParams.getMultiplier(), currentParams.getOffset());
            algorithm.setTravelerParams(currentParams);
            updateFormulaText();
        }
        else if (sliderName.equals(MULTIPLIER_SLIDER)) {
            currentParams = new TravelerParams(currentParams.getAngle(), value, currentParams.getOffset());
            algorithm.setTravelerParams(currentParams);
            updateFormulaText();
        }
        else if (sliderName.equals(OFFSET_SLIDER)) {
            currentParams =  new TravelerParams(currentParams.getAngle(), currentParams.getMultiplier(), value);
            algorithm.setTravelerParams(currentParams);
            updateFormulaText();
        }
        else if (sliderName.equals(ALPHA_SLIDER)) {
            algorithm.setAlpha((int) value);
        }
        else if (sliderName.equals(NUM_TRAVELORS_SLIDER)) {
            algorithm.setNumTravelors((int) value);
        }
        else if (sliderName.equals(ITER_PER_FRAME_SLIDER)) {
            algorithm.setStepsPerFrame((int) value);
        }
        else if (sliderName.equals(ITER_SLIDER)) {
            algorithm.setMaxIterations((int)value);
        }
    }
}
