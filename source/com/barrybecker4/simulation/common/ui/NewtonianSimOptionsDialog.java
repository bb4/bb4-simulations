/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.common.ui;

import com.barrybecker4.ui.components.NumberInput;

import javax.swing.*;
import java.awt.*;

/**
 * @author Barry Becker
 */
public abstract class NewtonianSimOptionsDialog extends SimulatorOptionsDialog {

    // rendering option controls
    private JCheckBox drawMeshCheckbox_;
    private JCheckBox showVelocitiesCheckbox_;
    private JCheckBox showForcesCheckbox_;

    // physics param options controls
    private NumberInput staticFrictionField_;
    private NumberInput dynamicFrictionField_;


    /**
     * Constructor
     */
    public NewtonianSimOptionsDialog(Component parent, Simulator simulator ) {
        super( parent, simulator );
    }

    @Override
    public String getTitle() {
        return "Newtonian Simulation Configuration";
    }

    @Override
    protected void addAdditionalToggles(JPanel togglesPanel) {

        NewtonianSimulator sim = (NewtonianSimulator) getSimulator();

        togglesPanel.add(createMeshCheckBox(sim));
        togglesPanel.add(createVelocitiesCheckBox(sim));
        togglesPanel.add(createForcesCheckBox(sim));
    }

    protected JCheckBox createMeshCheckBox(NewtonianSimulator sim) {
        drawMeshCheckbox_ =
                createCheckBox("Show Wireframe", "draw showing the underlying wireframe mesh", sim.getDrawMesh() );
        return drawMeshCheckbox_;
    }

    protected JCheckBox createVelocitiesCheckBox(NewtonianSimulator sim) {
        showVelocitiesCheckbox_ =
                createCheckBox("Show Velocity Vectors",
                    "show lines representing velocity vectors on each partical mass",
                    sim.getShowVelocityVectors() );
        return showVelocitiesCheckbox_;
    }

    protected JCheckBox createForcesCheckBox(NewtonianSimulator sim) {
        showForcesCheckbox_ =
                createCheckBox("Show Force Vectors",
                        "show lines representing force vectors on each partical mass",  sim.getShowForceVectors());
        return showForcesCheckbox_;
    }

    @Override
    protected JPanel createGlobalPhysicalParamPanel()
    {
        JPanel globalParamPanel = new JPanel();
        globalParamPanel.setLayout( new BorderLayout() );

        JPanel frictionPanel = new JPanel();
        frictionPanel.setLayout( new BoxLayout(frictionPanel, BoxLayout.Y_AXIS ) );
        frictionPanel.setBorder(
                BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Friction" ) );

        NewtonianSimulator sim = (NewtonianSimulator) getSimulator();
        staticFrictionField_ =
                new NumberInput( "static Friction (.0 small - 0.4 large):  ", sim.getStaticFriction(),
                                 "This controls amount of static surface friction.", 0.0, 0.4, false);
        dynamicFrictionField_ =
                new NumberInput( "dynamic friction (.0 small - .4 large):  ", sim.getDynamicFriction(),
                                  "This controls amount of dynamic surface friction.", 0.0, 0.4, false);

        frictionPanel.add( staticFrictionField_ );
        frictionPanel.add( dynamicFrictionField_ );

        globalParamPanel.add(frictionPanel, BorderLayout.NORTH);

        return globalParamPanel;
    }



    @Override
    protected void ok() {
        super.ok();

        NewtonianSimulator simulator = (NewtonianSimulator) getSimulator();
        // set the common rendering and global physics options
        simulator.setDrawMesh( drawMeshCheckbox_.isSelected() );
        simulator.setShowVelocityVectors( showVelocitiesCheckbox_.isSelected() );
        if (showForcesCheckbox_!=null)
            simulator.setShowForceVectors( showForcesCheckbox_.isSelected() );

        double staticFriction = staticFrictionField_.getValue();
        double dynamicFriction =  dynamicFrictionField_.getValue();
        assert(staticFriction >= dynamicFriction);
        simulator.setStaticFriction( staticFriction);
        simulator.setDynamicFriction( dynamicFriction );
    }

}
