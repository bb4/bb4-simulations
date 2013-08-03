/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer;

import com.barrybecker4.simulation.fractalexplorer.algorithm.FractalAlgorithm;
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

    private FractalAlgorithm algorithm_;
    private FractalExplorer simulator_;
    private JCheckBox useConcurrency_;
    private JCheckBox useFixedSize_;
    private JCheckBox useRunLengthOptimization_;
    private JButton backButton_;

    private static final String ITER_SLIDER = "Max Iterations";;
    private static final String TIMESTEP_SLIDER = "Num Rows per Frame";

    private SliderGroup sliderGroup_;
    private static final int MIN_NUM_STEPS = (int)(FractalExplorer.INITIAL_TIME_STEP/10.0);
    private static final int MAX_NUM_STEPS = (int)(10.0 * FractalExplorer.INITIAL_TIME_STEP);

    private static final SliderProperties[] SLIDER_PROPS = {
        new SliderProperties(ITER_SLIDER,      100,           10000,      FractalAlgorithm.DEFAULT_MAX_ITERATIONS,   1),
        new SliderProperties(TIMESTEP_SLIDER,  MIN_NUM_STEPS,   MAX_NUM_STEPS,   FractalExplorer.INITIAL_TIME_STEP, 1),
    };


    /**
     * Constructor
     */
    DynamicOptions(FractalAlgorithm algorithm, FractalExplorer simulator) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEtchedBorder());
        setPreferredSize(new Dimension(300, 300));

        algorithm_ = algorithm;
        simulator_ = simulator;

        sliderGroup_ = new SliderGroup(SLIDER_PROPS);
        sliderGroup_.addSliderChangeListener(this);

        ContinuousColorLegend legend_ =
                new ContinuousColorLegend(null, simulator_.getColorMap(), true);

        JPanel checkBoxes = createCheckBoxes();
        add(sliderGroup_);
        add(Box.createVerticalStrut(10));
        add(checkBoxes);
        add(Box.createVerticalStrut(10));
        add(legend_);

        backButton_ = new JButton("Go Back");
        backButton_.addActionListener(this);
        add(backButton_);

        JPanel fill = new JPanel();
        fill.setPreferredSize(new Dimension(10, 1000));
        add(fill);
    }

    private JPanel createCheckBoxes() {

        useConcurrency_ = new JCheckBox("Parallel", algorithm_.isParallelized());
        useConcurrency_.setToolTipText(
                "Take advantage of multiple processors for calculation and rendering if present.");
        useConcurrency_.addActionListener(this);

        useFixedSize_ = new JCheckBox("Fixed Size", simulator_.getUseFixedSize());
        useFixedSize_.addActionListener(this);

        useRunLengthOptimization_ = new JCheckBox("Run Length Optimization", algorithm_.getUseRunLengthOptimization());
        useRunLengthOptimization_.addActionListener(this);

        JPanel checkBoxes = new JPanel(new GridLayout(0, 1));

        checkBoxes.add(useConcurrency_);
        checkBoxes.add(useFixedSize_);
        checkBoxes.add(useRunLengthOptimization_);

        checkBoxes.setBorder(BorderFactory.createEtchedBorder());
        return checkBoxes;
    }


    public void reset() {
        sliderGroup_.reset();
    }

    /**
     * One of the buttons was pressed.
     */
    public void actionPerformed(ActionEvent e) {
        //RDRenderingOptions renderingOptions = simulator_.getRenderingOptions();

        if (e.getSource() == useConcurrency_) {
            boolean isParallelized = !algorithm_.isParallelized();
            algorithm_.setParallelized(isParallelized);
        }
        else if (e.getSource() == useFixedSize_) {
            simulator_.setUseFixedSize(useFixedSize_.isSelected());
        }
        else if (e.getSource() == useRunLengthOptimization_) {
            algorithm_.setUseRunLengthOptimization(useRunLengthOptimization_.isSelected());
        }
        else if (e.getSource() == backButton_) {
            algorithm_.goBack();
        }
    }

    /**
     * One of the sliders was moved.
     */
    public void sliderChanged(int sliderIndex, String sliderName, double value) {

        if (sliderName.equals(ITER_SLIDER)) {
            algorithm_.setMaxIterations((int)value);
        }
        else if (sliderName.equals(TIMESTEP_SLIDER)) {
            simulator_.setTimeStep(value);
        }
    }

}
