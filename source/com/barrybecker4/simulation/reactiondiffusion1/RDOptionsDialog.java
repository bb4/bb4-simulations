/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion1;

import com.barrybecker4.simulation.common1.ui.Simulator;
import com.barrybecker4.simulation.common1.ui.SimulatorOptionsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Barry Becker
 */
public class RDOptionsDialog extends SimulatorOptionsDialog {

    private JCheckBox offscreenRenderingCheckbox;

    private JCheckBox showProfilingCheckbox;
    private JCheckBox useParallelRenderingCheckbox;


    RDOptionsDialog(Component parent, Simulator simulator ) {
        super(parent, simulator);
    }

    @Override
    protected JPanel createCustomParamPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        RDSimulator sim = (RDSimulator) getSimulator();

        showProfilingCheckbox = createCheckBox("Show Profiling Information",
                        "If checked, profiling statistics will be displayed in the console when paused.",
                        RDProfiler.getInstance().isEnabled());

        offscreenRenderingCheckbox = createCheckBox("Use offscreen rendering",
                        "If checked, rendering graphics to an offscreen buffer before copying to the screen.",
                        sim.getUseOffScreenRendering());

        useParallelRenderingCheckbox = createCheckBox("Use parallel rendering",
                        "Rendering will take advantage of as many cores/threads that are avaialble.",
                        sim.getRenderingOptions().isParallelized());


        panel.add(showProfilingCheckbox);
        panel.add(offscreenRenderingCheckbox);
        panel.add(useParallelRenderingCheckbox);

        return panel;
    }

    @Override
    public void actionPerformed( ActionEvent e )
    {
        super.actionPerformed(e);
        Object source = e.getSource();
        RDSimulator sim = (RDSimulator) getSimulator();

        if ( source == showProfilingCheckbox) {
            RDProfiler.getInstance().setEnabled(showProfilingCheckbox.isSelected());
        }
        else if ( source == offscreenRenderingCheckbox) {
            sim.setUseOffscreenRendering(offscreenRenderingCheckbox.isSelected());
        }
        else if ( source == useParallelRenderingCheckbox) {
            sim.getRenderingOptions().setParallelized(useParallelRenderingCheckbox.isSelected());
        }
    }
}
