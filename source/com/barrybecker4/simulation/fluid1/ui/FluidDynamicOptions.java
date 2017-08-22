/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fluid1.ui;

import com.barrybecker4.simulation.fluid1.model.FluidEnvironment;
import com.barrybecker4.simulation.fluid1.rendering.EnvironmentRenderer;
import com.barrybecker4.simulation.fluid1.rendering.RenderingOptions;
import com.barrybecker4.ui.legend.ContinuousColorLegend;
import com.barrybecker4.ui.sliders.SliderGroup;
import com.barrybecker4.ui.sliders.SliderGroupChangeListener;
import com.barrybecker4.ui.sliders.SliderProperties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Dynamic controls for the Fluid simulation.
 * @author Barry Becker
 */
public class FluidDynamicOptions extends JPanel
                              implements ActionListener, SliderGroupChangeListener {

    private FluidSimulator simulator_;

    private JCheckBox useConcurrentCalculation_;
    private JCheckBox useConcurrentRendering;
    private JCheckBox useLinearInterpolation;
    private JCheckBox showVelocities;
    private JCheckBox showGrid;

    private static final String DR_SLIDER = "Diffusion Rate";
    private static final String VISC_SLIDER = "Viscosity";
    private static final String FORCE_SLIDER = "Force";
    private static final String SD_SLIDER = "Source Density";
    private static final String NUM_ITERATIONS_SLIDER = "Num Solver Iterations";
    private static final String NS_SLIDER = "Num Steps per Frame";
    private static final String TIME_STEP_SLIDER = "Time Step";

    private SliderGroup sliderGroup;

    private static final double MIN_STEPS = Math.ceil(FluidSimulator.DEFAULT_STEPS_PER_FRAME/10.0);
    private static final double MAX_STEPS = 20.0 * FluidSimulator.DEFAULT_STEPS_PER_FRAME;

    private static final SliderProperties[] SLIDER_PROPS = {
        new SliderProperties(DR_SLIDER,         0,       10.0,   FluidEnvironment.DEFAULT_DIFFUSION_RATE,   100.0),
        new SliderProperties(VISC_SLIDER,       0,       50.0,   FluidEnvironment.DEFAULT_VISCOSITY,        100.0),
        new SliderProperties(FORCE_SLIDER,      0.01,    30.0,  InteractionHandler.DEFAULT_FORCE,          100.0),
        new SliderProperties(SD_SLIDER,         0.01,     4.0,   InteractionHandler.DEFAULT_SOURCE_DENSITY,  100.0),
        new SliderProperties(NUM_ITERATIONS_SLIDER,  1,   100,  FluidEnvironment.DEFAULT_NUM_SOLVER_ITERATIONS,  1.0),
        new SliderProperties(NS_SLIDER, MIN_STEPS, MAX_STEPS, FluidSimulator.DEFAULT_STEPS_PER_FRAME,    1.0),
        new SliderProperties(TIME_STEP_SLIDER, 0.001, 0.1,   FluidSimulator.INITIAL_TIME_STEP,        1000.0)
    };


    FluidDynamicOptions(FluidSimulator simulator) {

        setBorder(BorderFactory.createEtchedBorder());

        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS));
        this.add(controlsPanel, BorderLayout.CENTER);

        simulator_ = simulator;

        sliderGroup = new SliderGroup(SLIDER_PROPS);
        sliderGroup.addSliderChangeListener(this);
        sliderGroup.setPreferredSize(new Dimension(300, 260));

        JPanel checkBoxes = createCheckBoxes();

        ContinuousColorLegend legend_ = new ContinuousColorLegend(null, simulator_.getRenderer().getColorMap(), true);

        controlsPanel.add(sliderGroup);

        controlsPanel.add(Box.createVerticalStrut(10));
        controlsPanel.add(checkBoxes);
        controlsPanel.add(Box.createVerticalStrut(10));
        controlsPanel.add(legend_);
    }

    private JPanel createCheckBoxes() {

        RenderingOptions renderOpts =  simulator_.getRenderer().getOptions();

        // not yet supported.
        //useConcurrentCalculation_ = createCheckBox("Parallel calculation",
        //        "Will take advantage of multiple processors for calculation if present.", false);

        useConcurrentRendering = createCheckBox("Parallel rendering",
                "Will take advantage of multiple processors for rendering if present.", renderOpts.isParallelized());

        useLinearInterpolation = createCheckBox("Use linear interpolation",
                "If checked, use linear interpolation when rendering, to give a smoother look.",
                renderOpts.getUseLinearInterpolation());

        showVelocities = createCheckBox("Show velocities", "if checked, show velocity vectors",
                                          renderOpts.getShowVelocities());
        showGrid = createCheckBox("Show grid",
                "Draw the background grid that shows the cells.", renderOpts.getShowGrid());

        JPanel checkBoxes = new JPanel(new GridLayout(0, 2));

        //checkBoxes.add(useConcurrentCalculation_);
        checkBoxes.add(useConcurrentRendering);
        checkBoxes.add(useLinearInterpolation);
        checkBoxes.add(showVelocities);
        checkBoxes.add(showGrid);

        checkBoxes.setBorder(BorderFactory.createEtchedBorder());
        return checkBoxes;
    }


    private JCheckBox createCheckBox(String label, String tooltip, boolean initiallyChecked)   {
        JCheckBox cb = new JCheckBox(label, initiallyChecked);
        cb.setToolTipText(tooltip);
        cb.addActionListener(this);
        return cb;
    }

    public void reset() {
        sliderGroup.reset();
        // make sure we honor current check selections
        RenderingOptions renderOpts =  simulator_.getRenderer().getOptions();
        renderOpts.setShowGrid(showGrid.isSelected());
        renderOpts.setShowVelocities(showVelocities.isSelected());
        renderOpts.setUseLinearInterpolation(useLinearInterpolation.isSelected());
    }

    /**
     * One of the buttons was pressed
     */
    public void actionPerformed(ActionEvent e) {
        EnvironmentRenderer renderer = simulator_.getRenderer();
        RenderingOptions renderOpts =  renderer.getOptions();

        if (e.getSource() == useConcurrentCalculation_) {
            // gs_.setParallelized(!gs_.isParallelized());
        }
        else if (e.getSource() == useConcurrentRendering) {
            renderOpts.setParallelized(!renderOpts.isParallelized());
        }
        else if (e.getSource() == useLinearInterpolation) {
            renderOpts.setUseLinearInterpolation(!renderOpts.getUseLinearInterpolation());
        }
        else if (e.getSource() == showVelocities) {
            renderOpts.setShowVelocities(!renderOpts.getShowVelocities());
        }
        else if (e.getSource() == showGrid) {
            renderOpts.setShowGrid(!renderOpts.getShowGrid());
        }
    }

    /**
     * One of the sliders was moved.
     */
    public void sliderChanged(int sliderIndex, String sliderName, double value) {

        switch (sliderName) {
            case VISC_SLIDER:
                simulator_.getEnvironment().setViscosity(value);
                break;
            case DR_SLIDER:
                simulator_.getEnvironment().setDiffusionRate(value);
                break;
            case FORCE_SLIDER:
                simulator_.getInteractionHandler().setForce(value);
                break;
            case SD_SLIDER:
                simulator_.getInteractionHandler().setSourceDensity(value);
                break;
            case NUM_ITERATIONS_SLIDER:
                simulator_.getEnvironment().setNumSolverIterations((int) value);
                break;
            case NS_SLIDER:
                simulator_.setNumStepsPerFrame((int) value);
                break;
            case TIME_STEP_SLIDER:
                simulator_.setTimeStep(value);
                break;
        }
    }

}
