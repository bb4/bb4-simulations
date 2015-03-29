/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading.options.ui;

import com.barrybecker4.simulation.trading.model.generationstrategy.GenerationStrategyEnum;
import com.barrybecker4.simulation.trading.model.generationstrategy.IGenerationStrategy;
import com.barrybecker4.simulation.trading.model.tradingstrategy.ITradingStrategy;
import com.barrybecker4.simulation.trading.model.tradingstrategy.TradingStrategyEnum;
import com.barrybecker4.simulation.trading.options.StockGenerationOptions;
import com.barrybecker4.simulation.trading.options.TradingOptions;
import com.barrybecker4.ui.components.NumberInput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;

/**
 * @author Barry Becker
 */
public class StockGenerationOptionsPanel extends JPanel implements ItemListener {

    /** number of dice to use.  */
    private NumberInput numStocksField;

    /** Number of time periods (for example months or years)  */
    private NumberInput numTimePeriodsField;


    /** Starting value of each stock in dollars  */
    private NumberInput startingValueField;


    private StockGenerationOptions generationOptions;

    private JComboBox<String> strategyCombo;
    private JPanel strategyOptionsPanel;


    /**
     * constructor
     */
    public StockGenerationOptionsPanel() {

        generationOptions = new StockGenerationOptions();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

         numStocksField =
                new NumberInput("Number of stocks in each sample (1 - 1000): ", generationOptions.numStocks,
                        "The number of stocks in each trial. The average value of which will be one data point.",
                        1, 1000, true);
        numTimePeriodsField =
                new NumberInput("Number of time periods (1 - 1000): ", generationOptions.numTimePeriods,
                        "Number of time periods (for example months or years).",
                        1, 1000, true);

        startingValueField =
                new NumberInput("Starting stock value : ", generationOptions.startingValue,
                        "Starting value of each stock in the sample (in dollars). For simplicity, they are all the same.",
                        1, 1000000, false);

        JPanel strategyDropDownElement = createStrategyDropDown();

        strategyOptionsPanel = new JPanel(new BorderLayout());
        strategyOptionsPanel.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, this.getBackground()));
        strategyOptionsPanel.add(generationOptions.generationStrategy.getOptionsUI(), BorderLayout.CENTER);

        add(numStocksField);
        add(numTimePeriodsField);
        add(startingValueField);

        add(strategyDropDownElement);
        add(strategyOptionsPanel);

        setBorder(Section.createBorder("Stock Generation Options"));
    }


    private JPanel createStrategyDropDown() {

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEADING));

        JLabel label = new JLabel("Stock generation strategy : ");

        java.util.List<String> choices = Arrays.asList(GenerationStrategyEnum.getLabels());
        strategyCombo = new JComboBox<>((String[]) choices.toArray());
        strategyCombo.setSelectedItem(StockGenerationOptions.DEFAULT_GENERATION_STRATEGY.getLabel());

        generationOptions.generationStrategy = getCurrentlySelectedStrategy();
        strategyCombo.addItemListener(this);
        setStrategyTooltip();

        panel.add(label);
        panel.add(strategyCombo);
        return panel;
    }


    @Override
    public void itemStateChanged(ItemEvent e) {
        setStrategyTooltip();

        generationOptions.generationStrategy = getCurrentlySelectedStrategy();
        strategyOptionsPanel.removeAll();
        strategyOptionsPanel.add(generationOptions.generationStrategy.getOptionsUI());

        // This will allow the dialog to resize appropriately given the new content.
        Container dlg = SwingUtilities.getAncestorOfClass(JDialog.class, this);
        if (dlg != null) ((JDialog) dlg).pack();
    }

    private IGenerationStrategy getCurrentlySelectedStrategy() {
        return GenerationStrategyEnum.valueForLabel((String) strategyCombo.getSelectedItem()).getStrategy();
    }

    private void setStrategyTooltip() {
        strategyCombo.setToolTipText(
                GenerationStrategyEnum.valueForLabel((String) strategyCombo.getSelectedItem()).getDescription());
    }

    StockGenerationOptions getOptions() {

        generationOptions.numStocks = numStocksField.getIntValue();
        generationOptions.numTimePeriods = numTimePeriodsField.getIntValue();
        generationOptions.startingValue = startingValueField.getValue();

        generationOptions.generationStrategy.acceptSelectedOptions();

        return generationOptions;
    }

}