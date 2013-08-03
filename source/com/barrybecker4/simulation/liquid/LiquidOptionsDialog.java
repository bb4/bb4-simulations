/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid;

import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;
import com.barrybecker4.simulation.liquid.config.ConfigurationEnum;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Use this modal dialog to let the user choose from among the
 * different game options.
 *
 * @author Bary Becker
 */
class LiquidOptionsDialog extends SimulatorOptionsDialog {

    /** type of distribution function to test.   */
    private JComboBox configurationChoiceField_;
    private JCheckBox showPressureCheckbox_;
    private JCheckBox showCellStatusCheckbox_;

    private JCheckBox showGridCheckbox_;
    private JCheckBox showVelocitiesCheckbox_;
    private JCheckBox useSingleStepModeCheckbox_;


    /** constructor  */
    LiquidOptionsDialog(Component parent, LiquidSimulator simulator ) {
        super( parent, simulator );
    }


    @Override
    protected void addAdditionalToggles(JPanel togglesPanel) {

        LiquidSimulator sim = (LiquidSimulator) getSimulator();

        showGridCheckbox_ =
               createCheckBox("Show Wireframe", "draw showing the underlying wireframe mesh",
                        sim.getRenderingOptions().getShowGrid() );

        showVelocitiesCheckbox_ =
                createCheckBox("Show Velocity Vectors",
                    "show lines representing velocity vectors on each partical mass",
                    sim.getRenderingOptions().getShowVelocities() );

        showPressureCheckbox_ =
                createCheckBox("Show Force Vectors",
                        "show lines representing force vectors on each partical mass",
                        sim.getRenderingOptions().getShowPressures());

        showCellStatusCheckbox_ =
                createCheckBox( "Show Cell Status",  "show status for each of the cells" ,
                                sim.getRenderingOptions().getShowCellStatus());

        useSingleStepModeCheckbox_ =
                createCheckBox( "Use Single Stepping",
                                "For debugging is may be useful to press a key to advance each timestep" ,
                                sim.getSingleStepMode());

        togglesPanel.add(showGridCheckbox_);
        togglesPanel.add(showVelocitiesCheckbox_);
        togglesPanel.add(showPressureCheckbox_);
        togglesPanel.add(showCellStatusCheckbox_);
        togglesPanel.add(useSingleStepModeCheckbox_);
    }


    @Override
    protected JPanel createCustomParamPanel() {

        JPanel customParamPanel = new JPanel();
        customParamPanel.setLayout( new BorderLayout() );

        JPanel liquidParamPanel = new JPanel();
        liquidParamPanel.setLayout( new BoxLayout(liquidParamPanel, BoxLayout.Y_AXIS ) );
        liquidParamPanel.setBorder(
                BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(), "Liquid Parameters" ) );

        configurationChoiceField_ = createConfigChoice();

        liquidParamPanel.add( configurationChoiceField_ );
        customParamPanel.add(liquidParamPanel, BorderLayout.NORTH);

        return customParamPanel;
    }

    private JComboBox createConfigChoice() {

        JComboBox configurationChoice = new JComboBox();

        configurationChoice.setModel(
                new DefaultComboBoxModel(ConfigurationEnum.values()));
        configurationChoice.setToolTipText(ConfigurationEnum.values()[0].getDescription());
        configurationChoice.addActionListener(this);
        configurationChoice.setSelectedItem(ConfigurationEnum.getDefaultValue());
        return configurationChoice;
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        super.actionPerformed(e);

        Object source = e.getSource();

        if ( source == configurationChoiceField_ ) {

            ConfigurationEnum selectedValue =
                   ((ConfigurationEnum)configurationChoiceField_.getSelectedItem());
            configurationChoiceField_.setToolTipText(selectedValue.getDescription());
        }
    }

    @Override
    protected void ok() {

        // set the liquid environment
        LiquidSimulator simulator = (LiquidSimulator) getSimulator();

        ConfigurationEnum selected = (ConfigurationEnum) configurationChoiceField_.getSelectedItem();

        simulator.loadEnvironment(selected.getFileName());

        simulator.getRenderingOptions().setShowGrid(showGridCheckbox_.isSelected());
        simulator.getRenderingOptions().setShowVelocities(showVelocitiesCheckbox_.isSelected());
        simulator.getRenderingOptions().setShowPressures(showPressureCheckbox_.isSelected());
        simulator.getRenderingOptions().setShowCellStatus(showCellStatusCheckbox_.isSelected());
        simulator.setSingleStepMode(useSingleStepModeCheckbox_.isSelected());
        super.ok();
    }
}