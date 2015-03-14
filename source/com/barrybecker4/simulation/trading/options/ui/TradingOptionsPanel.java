/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading.options.ui;

import com.barrybecker4.simulation.trading.options.GraphingOptions;
import com.barrybecker4.simulation.trading.options.TradingOptions;
import com.barrybecker4.ui.components.NumberInput;

import javax.swing.*;

/**
 * public float startingTotal = DEFAULT_STARTING_TOTAL;
    public float startingInvestment = DEFAULT_STARTING_INVESTMENT;
    public ChangePolicy gainStrategy = DEFAULT_GAIN_POLICY;
    public ChangePolicy lossStrategy = DEFAULT_LOSS_POLICY;

 * @author Barry Becker
 */
public class TradingOptionsPanel extends JPanel {

    /** Starting total in dollars */
    private NumberInput startingTotalField;

    /** The amount of money to use to purchase stock(s) at the starting price.  */
    private NumberInput startingInvestmentPercentField;


    private ChangePolicyPanel gainPolicyPanel;
    private ChangePolicyPanel lossPolicyPanel;
    private TradingOptions tradingOptions;

    /**
     * constructor
     */
    public TradingOptionsPanel() {

        tradingOptions = new TradingOptions();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        startingTotalField =
                new NumberInput("Starting Total Funds (1000 - 1000000): ", tradingOptions.startingTotal,
                        "The total amount of money that you start with. you will choose to invest some portion of it initially.",
                        1000, 1000000, false);
        startingInvestmentPercentField =
                new NumberInput("Starting investment percent: ", tradingOptions.startingInvestmentPercent,
                        "The percent of total funds to invest initially.",
                        0, 100, false);

        gainPolicyPanel = new ChangePolicyPanel("% gain which triggers next transaction",
                "% of current investment to sell on gain",
                tradingOptions.gainStrategy);
        lossPolicyPanel = new ChangePolicyPanel("% market loss which triggers next transaction",
                "% of current reserve to use to buy on loss",
                tradingOptions.lossStrategy);

        add(startingTotalField);
        add(startingInvestmentPercentField);
        add(gainPolicyPanel);
        add(lossPolicyPanel);

        setBorder(Section.createBorder("Trading Strategy Options"));
    }


    TradingOptions getOptions() {

        tradingOptions.startingTotal = startingTotalField.getValue();
        tradingOptions.startingInvestmentPercent = startingInvestmentPercentField.getValue();
        tradingOptions.gainStrategy = gainPolicyPanel.getChangePolicy();
        tradingOptions.lossStrategy = lossPolicyPanel.getChangePolicy();

        return tradingOptions;
    }

}