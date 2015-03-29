/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading.model.tradingstrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum for type of trading strategy.
 * Can be used to populate a dropdown.
 *
 * @author Barry Becker
 */
public enum TradingStrategyEnum  {

    BUY_AND_HOLD("buy and hold",
            "No transactions are made after the initial investment"),
    PERCENT_OF_RESERVE("percent of reserve",
            "When the marked goes up, we sell a percent of investment; when it goes down we buy a percent of reserve"),
    PERCENT_OF_INVESTMENT("percent of investment", ""),
    SELL_WHAT_WAS_BOUGHT("sell what was bought",
            "Initial investment is never sold, but a fixed percentage is bought when market drops, " +
             "and that same amount is then sold when market rises some threshold above the price that it was bought at.");

    private String label;
    private String description;
    private static Map<String, TradingStrategyEnum> valueMap = new HashMap<>();

    static {
        for (TradingStrategyEnum e : TradingStrategyEnum.values()) {
            TradingStrategyEnum.valueMap.put(e.getLabel(), e);
        }
    }

    /**
     * Private constructor
     * Creates a new instance of Algorithm
     */
    private TradingStrategyEnum(String label, String description) {
        this.label = label;
        this.description = description;

    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Create an instance of the algorithm given the controller and a refreshable.
     */
    public ITradingStrategy getStrategy() {

        switch (this) {
            case BUY_AND_HOLD :
                return new BuyAndHoldStrategy();
            case PERCENT_OF_RESERVE :
                return new BuyPercentOfReserveStrategy();
            case PERCENT_OF_INVESTMENT :
                return new BuyPercentOfInvestmentStrategy();
            case SELL_WHAT_WAS_BOUGHT :
                return new SellWhatWasBoughtStrategy();
        }
        return null;
    }

    public static String[] getLabels() {
        String[] labels = new String[TradingStrategyEnum.values().length];
        int i = 0;
        for (TradingStrategyEnum v : TradingStrategyEnum.values()) {
           labels[i++] =  v.getLabel();
        }
        return labels;
    }

    public static TradingStrategyEnum valueForLabel(String label) {
        return valueMap.get(label);
    }
}
