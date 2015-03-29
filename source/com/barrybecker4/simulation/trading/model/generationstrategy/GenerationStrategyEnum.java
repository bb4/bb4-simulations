/*
 * Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/license.MIT
 */
package com.barrybecker4.simulation.trading.model.generationstrategy;

import com.barrybecker4.simulation.trading.model.tradingstrategy.BuyAndHoldStrategy;
import com.barrybecker4.simulation.trading.model.tradingstrategy.BuyPercentOfInvestmentStrategy;
import com.barrybecker4.simulation.trading.model.tradingstrategy.BuyPercentOfReserveStrategy;
import com.barrybecker4.simulation.trading.model.tradingstrategy.ITradingStrategy;
import com.barrybecker4.simulation.trading.model.tradingstrategy.SellWhatWasBoughtStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum for type of trading strategy.
 * Can be used to populate a dropdown.
 *
 * @author Barry Becker
 */
public enum GenerationStrategyEnum {

    FLAT("flat",
            "Does not change at all. Horizontal line."),
    RANDOM_UPS_AND_DOWNS("random ups and downs",
            "Each time period the market goes up by some random percent or down some sandom percent, according to a coin toss."),
    GAUSSIAN("gaussian",
            "Each time period the market goes up or down by a guassian distributed random value whose mean and vaiance are specified");

    private String label;
    private String description;
    private static Map<String, GenerationStrategyEnum> valueMap = new HashMap<>();

    static {
        for (GenerationStrategyEnum e : GenerationStrategyEnum.values()) {
            GenerationStrategyEnum.valueMap.put(e.getLabel(), e);
        }
    }

    /**
     * Private constructor
     * Creates a new instance of Algorithm
     */
    private GenerationStrategyEnum(String label, String description) {
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
    public IGenerationStrategy getStrategy() {

        switch (this) {
            case FLAT :
                return new FlatStrategy();
            case RANDOM_UPS_AND_DOWNS:
                return new RandomUpsAndDownsStrategy();
        }
        return null;
    }

    public static String[] getLabels() {
        String[] labels = new String[GenerationStrategyEnum.values().length];
        int i = 0;
        for (GenerationStrategyEnum v : GenerationStrategyEnum.values()) {
           labels[i++] =  v.getLabel();
        }
        return labels;
    }

    public static GenerationStrategyEnum valueForLabel(String label) {
        if (!valueMap.containsKey(label))  {
            throw new IllegalArgumentException("Unknown strategy label : " + label);
        }
        return valueMap.get(label);
    }
}
