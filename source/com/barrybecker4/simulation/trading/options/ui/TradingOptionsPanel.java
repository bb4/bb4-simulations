/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading.options.ui;

import com.barrybecker4.simulation.trading.model.StrategyPlugins;
import com.barrybecker4.simulation.trading.model.tradingstrategy.ITradingStrategy;
import com.barrybecker4.simulation.trading.options.TradingOptions;
import com.barrybecker4.ui.components.NumberInput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.List;

/**
 * There are basic trading options, and then there is the trading strategy.
 *
 * @author Barry Becker
 */
public class TradingOptionsPanel extends JPanel implements ItemListener{

    /** Starting total in dollars */
    private NumberInput startingTotalField;

    /** The amount of money to use to purchase stock(s) at the starting price.  */
    private NumberInput startingInvestmentPercentField;

    /** The amount of money to use to purchase stock(s) at the starting price.  */
    private NumberInput theoreticalMaxGainField;

    private TradingOptions tradingOptions;

    private JComboBox<String> strategyCombo;
    private JPanel strategyOptionsPanel;

    private StrategyPlugins<ITradingStrategy> tradingPlugins =
            new StrategyPlugins<>("com.barrybecker4.simulation.trading.model.tradingstrategy", ITradingStrategy.class);

    /**
     * constructor
     */
    public TradingOptionsPanel() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        tradingOptions = new TradingOptions();
        startingTotalField =
                new NumberInput("Starting Total Funds (1000 - 1000000): ", tradingOptions.startingTotal,
                        "The total amount of money that you start with. you will choose to invest some portion of it initially.",
                        1000, 1000000, false);
        startingInvestmentPercentField =
                new NumberInput("Starting investment percent: ", 100 *tradingOptions.startingInvestmentPercent,
                        "The percent of total funds to invest initially.",
                        0, 100, false);

        theoreticalMaxGainField =
                new NumberInput("Theoretical max gain: ", tradingOptions.theoreticalMaxGain,
                        "Enter value for the biggest profit you could hope to get from this model. "
                                + "Used only to determine the max extent of the x axis.",
                        0, 100000000, false);

        // needed to get the field to extend to the left.
        startingTotalField.setAlignmentX(CENTER_ALIGNMENT);
        startingInvestmentPercentField.setAlignmentX(CENTER_ALIGNMENT);
        theoreticalMaxGainField.setAlignmentX(CENTER_ALIGNMENT);

        JPanel strategyDropDownElement = createStrategyDropDown();

        strategyOptionsPanel = new JPanel(new BorderLayout());
        strategyOptionsPanel.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, this.getBackground()));
        strategyOptionsPanel.add(tradingOptions.tradingStrategy.getOptionsUI(), BorderLayout.CENTER);

        // these are common to all strategies
        add(startingTotalField);
        add(startingInvestmentPercentField);
        add(theoreticalMaxGainField);

        add(strategyDropDownElement);
        add(strategyOptionsPanel);

        setBorder(Section.createBorder("Trading Options"));
    }

    private JPanel createStrategyDropDown() {

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEADING));

        JLabel label = new JLabel("Trading strategy : ");

        List<String> choices = tradingPlugins.getStrategies();;
        String[] ch = choices.toArray(new String[choices.size()]);
        strategyCombo = new JComboBox<>(ch);
        strategyCombo.setSelectedItem(TradingOptions.DEFAULT_TRADING_STRATEGY.getName());

        tradingOptions.tradingStrategy = getCurrentlySelectedStrategy();
        strategyCombo.addItemListener(this);
        setStrategyTooltip();

        panel.add(label);
        panel.add(strategyCombo);
        return panel;
    }


    TradingOptions getOptions() {

        tradingOptions.startingTotal = startingTotalField.getValue();
        tradingOptions.startingInvestmentPercent = startingInvestmentPercentField.getValue() / 100.0;

        tradingOptions.theoreticalMaxGain = theoreticalMaxGainField.getValue();
        tradingOptions.tradingStrategy.acceptSelectedOptions();

        return tradingOptions;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        setStrategyTooltip();

        tradingOptions.tradingStrategy = getCurrentlySelectedStrategy();
        strategyOptionsPanel.removeAll();
        strategyOptionsPanel.add(tradingOptions.tradingStrategy.getOptionsUI());

        // This will allow the dialog to resize appropriately given the new content.
        Container dlg = SwingUtilities.getAncestorOfClass(JDialog.class, this);
        if (dlg != null) ((JDialog) dlg).pack();
    }

    private ITradingStrategy getCurrentlySelectedStrategy() {
        return tradingPlugins.getStrategy((String) strategyCombo.getSelectedItem());
    }

    private void setStrategyTooltip() {
        strategyCombo.setToolTipText(
                tradingPlugins.getStrategy((String) strategyCombo.getSelectedItem()).getDescription());
    }

}