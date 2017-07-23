/** Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer;

import com.barrybecker4.common.math.ComplexNumberRange;
import com.barrybecker4.simulation.fractalexplorer.algorithm.FractalAlgorithm;
import com.barrybecker4.ui.legend.ContinuousColorLegend;
import com.barrybecker4.ui.sliders.SliderGroup;
import com.barrybecker4.ui.sliders.SliderGroupChangeListener;
import com.barrybecker4.ui.sliders.SliderProperties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Dynamic controls for the Fractal explorer simulation that will show on the right.
 * They change the behavior of the simulation while it is running.
 * @author Barry Becker
 */
class DynamicOptions extends JPanel
        implements ActionListener, SliderGroupChangeListener {

    static final double INITIAL_TIME_STEP = 10.0;
    static final int DEFAULT_STEPS_PER_FRAME = 1;

    private FractalExplorer simulator;
    private JCheckBox useConcurrency;
    private JCheckBox useFixedSize;
    private JCheckBox useRunLengthOptimization;
    private JButton backButton;
    private JLabel coordinate1;
    private JLabel coordinate2;

    private static final String ITER_SLIDER = "Max Iterations";;
    private static final String TIMESTEP_SLIDER = "Num Rows per Frame";

    private SliderGroup sliderGroup;
    private static final int MIN_NUM_STEPS = (int)(INITIAL_TIME_STEP/10.0);
    private static final int MAX_NUM_STEPS = (int)(10.0 * INITIAL_TIME_STEP);

    private static final SliderProperties[] SLIDER_PROPS = {
            new SliderProperties(ITER_SLIDER,      100,   10000, FractalAlgorithm.DEFAULT_MAX_ITERATIONS(),   1),
            new SliderProperties(TIMESTEP_SLIDER,  MIN_NUM_STEPS,   MAX_NUM_STEPS,   INITIAL_TIME_STEP, 1),
    };


    /** Constructor */
    DynamicOptions(FractalExplorer simulator) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEtchedBorder());
        setPreferredSize(new Dimension(300, 300));

        this.simulator = simulator;

        sliderGroup = new SliderGroup(SLIDER_PROPS);
        sliderGroup.addSliderChangeListener(this);

        ContinuousColorLegend legend_ =
                new ContinuousColorLegend(null, this.simulator.getColorMap(), true);

        JPanel checkBoxes = createCheckBoxes();
        JPanel coordinates = createCoordinatesView();

        add(sliderGroup);
        add(Box.createVerticalStrut(10));
        add(checkBoxes);
        add(Box.createVerticalStrut(10));
        add(legend_);
        add(coordinates);

        backButton = new JButton("Go Back");
        backButton.addActionListener(this);
        add(backButton);

        JPanel fill = new JPanel();
        fill.setPreferredSize(new Dimension(10, 1000));
        add(fill);
    }

    public void setCoordinates(ComplexNumberRange range) {
        coordinate1.setText("c1: " + range.getPoint1());
        coordinate2.setText("c2: " + range.getPoint2());
    }

    private JPanel createCheckBoxes() {

        FractalAlgorithm algorithm = simulator.getAlgorithm();
        useConcurrency = new JCheckBox("Parallel", algorithm.isParallelized());
        useConcurrency.setToolTipText(
                "Take advantage of multiple processors for calculation and rendering if present.");
        useConcurrency.addActionListener(this);

        useFixedSize = new JCheckBox("Fixed Size", simulator.getUseFixedSize());
        useFixedSize.addActionListener(this);

        useRunLengthOptimization = new JCheckBox("Run Length Optimization", algorithm.getUseRunLengthOptimization());
        useRunLengthOptimization.addActionListener(this);

        JPanel checkBoxes = new JPanel(new GridLayout(0, 1));

        checkBoxes.add(useConcurrency);
        checkBoxes.add(useFixedSize);
        checkBoxes.add(useRunLengthOptimization);

        checkBoxes.setBorder(BorderFactory.createEtchedBorder());
        return checkBoxes;
    }

    private JPanel createCoordinatesView() {
        JPanel view = new JPanel();
        view.setLayout(new BorderLayout());
        coordinate1 = new JLabel("Upper Left: ");
        coordinate2 = new JLabel("Lower Right: ");

        view.add(coordinate1, BorderLayout.NORTH);
        view.add(coordinate2, BorderLayout.CENTER);
        return view;
    }

    public void reset() {
        sliderGroup.reset();
    }

    /**
     * One of the buttons was pressed.
     */
    public void actionPerformed(ActionEvent e) {
        //RDRenderingOptions renderingOptions = simulator.getRenderingOptions();
        FractalAlgorithm algorithm = simulator.getAlgorithm();

        if (e.getSource() == useConcurrency) {
            boolean isParallelized = !algorithm.isParallelized();
            algorithm.setParallelized(isParallelized);
        }
        else if (e.getSource() == useFixedSize) {
            simulator.setUseFixedSize(useFixedSize.isSelected());
        }
        else if (e.getSource() == useRunLengthOptimization) {
            algorithm.setUseRunLengthOptimization(useRunLengthOptimization.isSelected());
        }
        else if (e.getSource() == backButton) {
            algorithm.goBack();
        }
    }

    /**
     * One of the sliders was moved.
     */
    public void sliderChanged(int sliderIndex, String sliderName, double value) {

        FractalAlgorithm algorithm = simulator.getAlgorithm();

        if (sliderName.equals(ITER_SLIDER)) {
            algorithm.setMaxIterations((int)value);
        }
        else if (sliderName.equals(TIMESTEP_SLIDER)) {
            simulator.setTimeStep(value);
        }
    }

}
