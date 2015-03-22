/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading.options.ui;

import com.barrybecker4.simulation.trading.options.TradingOptions;
import com.barrybecker4.ui.components.NumberInput;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.List;

/**
 * public float startingTotal = DEFAULT_STARTING_TOTAL;
    public float startingInvestment = DEFAULT_STARTING_INVESTMENT;
    public ChangePolicy gainPolicy = DEFAULT_GAIN_POLICY;
    public ChangePolicy lossPolicy = DEFAULT_LOSS_POLICY;

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

    // these will come from the trading strategy
    private ChangePolicyPanel gainPolicyPanel;
    private ChangePolicyPanel lossPolicyPanel;

    private JComboBox<String> strategyCombo;

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

        // these are common to all strategies
        add(startingTotalField);
        add(startingInvestmentPercentField);
        add(theoreticalMaxGainField);

        add(createStrategyDropDown());

        // these are strategy specific
        add(createStrategyOptions());

        setBorder(Section.createBorder("Trading Options"));
    }

    private JPanel createStrategyDropDown() {

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEADING));

        JLabel label = new JLabel("Trading strategy : ");

        List<String> choices = Arrays.asList("foo", "bar");
        strategyCombo = new JComboBox<>((String[]) choices.toArray());
        strategyCombo.addItemListener(this);

        panel.add(label);
        panel.add(strategyCombo);
        return panel;
    }


    private JPanel createStrategyOptions() {
        JPanel strategyPanel = new JPanel(new BorderLayout());
        strategyPanel.setBorder(BorderFactory.createEtchedBorder());

        gainPolicyPanel = new ChangePolicyPanel("% gain which triggers next transaction",
                "% of current investment to sell on gain",
                tradingOptions.gainPolicy);
        lossPolicyPanel = new ChangePolicyPanel("% market loss which triggers next transaction",
                "% of current reserve to use to buy on loss",
                tradingOptions.lossPolicy);

        gainPolicyPanel.setAlignmentX(CENTER_ALIGNMENT);
        lossPolicyPanel.setAlignmentX(CENTER_ALIGNMENT);

        strategyPanel.add(gainPolicyPanel, BorderLayout.NORTH);
        strategyPanel.add(lossPolicyPanel, BorderLayout.CENTER);

        return strategyPanel;
    }


    TradingOptions getOptions() {

        tradingOptions.startingTotal = startingTotalField.getValue();
        tradingOptions.startingInvestmentPercent = startingInvestmentPercentField.getValue() / 100.0;
        tradingOptions.gainPolicy = gainPolicyPanel.getChangePolicy();
        tradingOptions.lossPolicy = lossPolicyPanel.getChangePolicy();
        tradingOptions.theoreticalMaxGain = theoreticalMaxGainField.getValue();

        return tradingOptions;
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        System.out.println("selected = " + strategyCombo.getSelectedItem());
    }
}