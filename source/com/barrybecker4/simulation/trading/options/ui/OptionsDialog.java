/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading.options.ui;

import com.barrybecker4.simulation.common.ui.Simulator;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;
import com.barrybecker4.simulation.trading.TradingSimulator;

import javax.swing.*;
import java.awt.*;

/**
 * @author Barry Becker
 */
public class OptionsDialog extends SimulatorOptionsDialog {


    private StockGenerationOptionsPanel stockGenerationOptionsPanel;
    private TradingOptionsPanel tradingOptionsPanel;
    private GraphingOptionsPanel graphingOptionsPanel;

    /**
     * constructor
     */
    public OptionsDialog(Component parent, Simulator simulator) {
        super( parent, simulator );
    }

    @Override
    public String getTitle()
    {
        return "Stock Simulation Configuration";
    }

    @Override
    protected JPanel createCustomParamPanel() {
        JPanel paramPanel = new JPanel();
        paramPanel.setLayout(new BorderLayout());

        stockGenerationOptionsPanel = new StockGenerationOptionsPanel();
        tradingOptionsPanel = new TradingOptionsPanel();
        graphingOptionsPanel = new GraphingOptionsPanel();

        paramPanel.add(stockGenerationOptionsPanel, BorderLayout.NORTH);
        paramPanel.add(tradingOptionsPanel, BorderLayout.CENTER);
        paramPanel.add(graphingOptionsPanel, BorderLayout.SOUTH);

        return paramPanel;
    }


    @Override
    protected void ok() {

        TradingSimulator simulator = (TradingSimulator) getSimulator();
        simulator.setSampleOptions(stockGenerationOptionsPanel.getOptions());
        //simulator.setTradingOptions(tradingOptionsPanel.getOptions());
        simulator.setGraphingOptions(graphingOptionsPanel.getOptions());
    }

}