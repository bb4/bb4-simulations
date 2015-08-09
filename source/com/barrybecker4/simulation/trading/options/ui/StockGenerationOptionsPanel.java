/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading.options.ui;

import com.barrybecker4.simulation.trading.model.generationstrategy.*;
import com.barrybecker4.simulation.trading.model.plugin.StrategyPlugins;
import com.barrybecker4.simulation.trading.options.StockGenerationOptions;
import com.barrybecker4.ui.components.NumberInput;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    private StrategyPlugins<? extends IGenerationStrategy> generationStrategies =
            new StrategyPlugins<>("com.barrybecker4.simulation.trading.model.generationstrategy",
                    IGenerationStrategy.class,
                    Arrays.asList(
                            new FlatStrategy(), new GaussianStrategy(),  new SineStrategy(),
                            new RandomUpsAndDownsStrategy(), new RandomWithAdditiveMomentumStrategy()
                    ));


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

        // needed to get the field to extend to the left.
        numStocksField.setAlignmentX(CENTER_ALIGNMENT);
        numTimePeriodsField.setAlignmentX(CENTER_ALIGNMENT);
        startingValueField.setAlignmentX(CENTER_ALIGNMENT);

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

        List<String> choices = generationStrategies.getStrategies();

        strategyCombo = new JComboBox<>(choices.toArray(new String[choices.size()]));
        strategyCombo.setSelectedItem(StockGenerationOptions.DEFAULT_GENERATION_STRATEGY.getName());

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
        return generationStrategies.getStrategy((String) strategyCombo.getSelectedItem());
    }

    private void setStrategyTooltip() {
        strategyCombo.setToolTipText(
                generationStrategies.getStrategy((String) strategyCombo.getSelectedItem()).getDescription());
    }

    StockGenerationOptions getOptions() {

        generationOptions.numStocks = numStocksField.getIntValue();
        generationOptions.numTimePeriods = numTimePeriodsField.getIntValue();
        generationOptions.startingValue = startingValueField.getValue();

        generationOptions.generationStrategy.acceptSelectedOptions();

        return generationOptions;
    }

}