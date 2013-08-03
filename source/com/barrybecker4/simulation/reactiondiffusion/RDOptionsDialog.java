/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion;

import com.barrybecker4.simulation.common.ui.Simulator;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Barry Becker
 */
public class RDOptionsDialog extends SimulatorOptionsDialog {

    private JCheckBox offscreenRenderingCheckbox_;

    private JCheckBox showProfilingCheckbox_;
    private JCheckBox useParallelRenderingCheckbox_;


    public RDOptionsDialog(Component parent, Simulator simulator ) {
        super(parent, simulator);
    }

    @Override
    protected JPanel createCustomParamPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        RDSimulator sim = (RDSimulator) getSimulator();

        showProfilingCheckbox_ = createCheckBox("Show Profiling Information",
                        "If checked, profiling statistics will be displayed in the console when paused.",
                        RDProfiler.getInstance().isEnabled());

        offscreenRenderingCheckbox_ = createCheckBox("Use offscreen rendering",
                        "If checked, rendering graphics to an offscreen buffer before copying to the screen.",
                        sim.getUseOffScreenRendering());

        useParallelRenderingCheckbox_ = createCheckBox("Use parallel rendering",
                        "Rendering will take advantage of as many cores/threads that are avaialble.",
                        sim.getRenderingOptions().isParallelized());


        panel.add(showProfilingCheckbox_);
        panel.add(offscreenRenderingCheckbox_);
        panel.add(useParallelRenderingCheckbox_);

        return panel;
    }

    @Override
    public void actionPerformed( ActionEvent e )
    {
        super.actionPerformed(e);
        Object source = e.getSource();
        RDSimulator sim = (RDSimulator) getSimulator();

        if ( source == showProfilingCheckbox_ ) {
            RDProfiler.getInstance().setEnabled(showProfilingCheckbox_.isSelected());
        }
        else if ( source == offscreenRenderingCheckbox_ ) {
            sim.setUseOffscreenRendering(offscreenRenderingCheckbox_.isSelected());
        }
        else if ( source == useParallelRenderingCheckbox_ ) {
            sim.getRenderingOptions().setParallelized(useParallelRenderingCheckbox_.isSelected());
        }
    }
}
