/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion;

import com.barrybecker4.simulation.reactiondiffusion.algorithm.GrayScottController;
import com.barrybecker4.simulation.reactiondiffusion.algorithm.GrayScottModel;
import com.barrybecker4.simulation.reactiondiffusion.rendering.RDRenderingOptions;
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
class RDDynamicOptions extends JPanel
                       implements ActionListener, SliderGroupChangeListener {

    private GrayScottController gs_;
    private RDSimulator simulator_;

    private JCheckBox showU_;
    private JCheckBox showV_;
    private JCheckBox useComputeConcurrency_;
    private JCheckBox useRenderingConcurrency_;
    private JCheckBox useFixedSize_;

    private static final String K_SLIDER = "K";
    private static final String F_SLIDER = "F";
    private static final String H_SLIDER = "H";
    private static final String BH_SLIDER = "Bump Height";
    private static final String SH_SLIDER = "Specular Highlight";
    private static final String NS_SLIDER = "Num Steps per Frame";
    private static final String TIMESTEP_SLIDER = "Time Step Size";

    private SliderGroup sliderGroup_;
    private static final double MIN_NUM_STEPS = RDSimulator.DEFAULT_STEPS_PER_FRAME/10.0;
    private static final double MAX_NUM_STEPS = 10.0 * RDSimulator.DEFAULT_STEPS_PER_FRAME;

    private static final SliderProperties[] SLIDER_PROPS = {
        new SliderProperties(K_SLIDER,      0,           0.3,      GrayScottModel.K0,     1000),
        new SliderProperties(F_SLIDER,      0,           0.3,      GrayScottModel.F0,     1000),
        new SliderProperties(H_SLIDER,      0.008,      0.05,      GrayScottController.H0,     10000),
        new SliderProperties(BH_SLIDER,     0,          20.0,     0.0,               10),
        new SliderProperties(SH_SLIDER,     0,          1.0,      0.0,               100),
        new SliderProperties(NS_SLIDER,  MIN_NUM_STEPS,   MAX_NUM_STEPS,   RDSimulator.DEFAULT_STEPS_PER_FRAME, 1),
        new SliderProperties(TIMESTEP_SLIDER,   0.1,     2.0,     RDSimulator.INITIAL_TIME_STEP,    100),
    };


    /**
     * Constructor
     */
    RDDynamicOptions(GrayScottController gs, RDSimulator simulator) {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEtchedBorder());
        setPreferredSize(new Dimension(300, 300));

        gs_ = gs;
        simulator_ = simulator;

        sliderGroup_ = new SliderGroup(SLIDER_PROPS);
        sliderGroup_.addSliderChangeListener(this);

        JPanel checkBoxes = createCheckBoxes();
        ContinuousColorLegend legend_ =
                new ContinuousColorLegend(null, simulator_.getColorMap(), true);


        add(sliderGroup_);
        add(Box.createVerticalStrut(10));
        add(checkBoxes);
        add(Box.createVerticalStrut(10));
        add(legend_);

        JPanel fill = new JPanel();
        fill.setPreferredSize(new Dimension(10, 1000));
        add(fill);
    }

    private JPanel createCheckBoxes() {

        RDRenderingOptions renderingOptions = simulator_.getRenderingOptions();
        showU_ = new JCheckBox("U Value", renderingOptions.isShowingU());
        showU_.addActionListener(this);

        showV_ = new JCheckBox("V Value", renderingOptions.isShowingV());
        showV_.addActionListener(this);

        useComputeConcurrency_ =
                createCheckBox("Parallel caclulation",
                               "Take advantage of multiple processors for RD calculation if checked.",
                               gs_.isParallelized());

        useRenderingConcurrency_ =
                createCheckBox("Parallel rendering",
                              "Take advantage of multiple processors for rendering if checked.",
                              renderingOptions.isParallelized());

        useFixedSize_ =
                createCheckBox("Fixed Size",
                               "Use just a small fixed size area for rendering rather than the whole resizable area.",
                               simulator_.getUseFixedSize());

        JPanel checkBoxes = new JPanel(new GridLayout(0, 2));
        checkBoxes.add(showU_);
        checkBoxes.add(showV_);
        checkBoxes.add(useComputeConcurrency_);
        checkBoxes.add(useRenderingConcurrency_);
        checkBoxes.add(useFixedSize_);

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
        sliderGroup_.reset();
    }

    /**
     * One of the buttons was pressed.
     */
    public void actionPerformed(ActionEvent e) {
        RDRenderingOptions renderingOptions = simulator_.getRenderingOptions();

        if (e.getSource() == showU_) {
            renderingOptions.setShowingU(!renderingOptions.isShowingU());
        }
        else if (e.getSource() == showV_) {
            renderingOptions.setShowingV(!renderingOptions.isShowingV());
            repaint();
        }
        else if (e.getSource() == useComputeConcurrency_) {
            boolean isParallelized = !gs_.isParallelized();
            gs_.setParallelized(isParallelized);
        }
        else if (e.getSource() == useRenderingConcurrency_) {
            boolean isParallelized = !renderingOptions.isParallelized();
            renderingOptions.setParallelized(isParallelized);
        }
        else if (e.getSource() == useFixedSize_) {
            simulator_.setUseFixedSize(useFixedSize_.isSelected());
        }
    }

    /**
     * One of the sliders was moved.
     */
    public void sliderChanged(int sliderIndex, String sliderName, double value) {
        switch (sliderName) {
            case F_SLIDER:
                gs_.getModel().setF(value);
                break;
            case K_SLIDER:
                gs_.getModel().setK(value);
                break;
            case H_SLIDER:
                gs_.setH(value);
                break;
            case BH_SLIDER:
                simulator_.getRenderingOptions().setHeightScale(value);
                sliderGroup_.setEnabled(SH_SLIDER, value > 0);
                break;
            case SH_SLIDER:
                simulator_.getRenderingOptions().setSpecular(value);
                break;
            case NS_SLIDER:
                simulator_.setNumStepsPerFrame((int) value);
                break;
            case TIMESTEP_SLIDER:
                simulator_.setTimeStep(value);
                break;
        }
    }

}
