/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading.model.tradingstrategy;

/**
 * Enum for type of trading strategy.
 * Can be used to populate a dropdown.
 *
 * @author Barry Becker
 */
public enum TradingStrategyEnum  {

    BUY_AND_HOLD,
    PERCENT_OF_RESERVE,
    PERCENT_OF_INVESTMENT,
    SELL_WHAT_WAS_BOUGHT;

    private String label;

    /**
     *Private constructor
     * Creates a new instance of Algorithm
     */
    TradingStrategyEnum() {
        this.label = this.name();
    }

    public String getLabel() {
        return label;
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
}
