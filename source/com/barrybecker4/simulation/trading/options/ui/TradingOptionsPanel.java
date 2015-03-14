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

    /** The % market gain necessary to trigger next transaction.  */
    private NumberInput gainThresholdPercentField;

    /** Percent of current investment to sell when gain threshold reached.  */
    private NumberInput gainSellPercentField;

    /** The % market loss necessary to trigger next transaction.  */
    private NumberInput lossThresholdPercentField;

    /** Percent of current reserve to use to buy with when loss threshold reached.  */
    private NumberInput lossBuyPercentField;

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


        add(startingTotalField);
        add(startingInvestmentPercentField);

        setBorder(BorderFactory.createTitledBorder("Trading Strategy Options"));
    }


    TradingOptions getOptions() {

        tradingOptions.startingTotal = startingTotalField.getValue();
        tradingOptions.startingInvestmentPercent = startingInvestmentPercentField.getValue();

        return tradingOptions;
    }

}