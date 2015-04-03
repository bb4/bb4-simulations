/*
 * Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/license.MIT
 */

package com.barrybecker4.simulation.trading.model;

import com.barrybecker4.common.util.PackageReflector;
import com.barrybecker4.simulation.trading.model.tradingstrategy.ITradingStrategy;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Finds plugins for type of stock series generation strategies.
 * Can be used to populate a dropdown.
 *
 * @author Barry Becker
 */
public class TradingStrategyPlugins {


    private static final List<String> strategyNames;
    private static Map<String, ITradingStrategy> valueMap = new HashMap<>();

    static {

        strategyNames = new ArrayList<String>();

        try {
            List<Class> strategyClasses =
                    new PackageReflector().getClasses("com.barrybecker4.simulation.trading.model.tradingstrategy");

            for (Class<?> c : strategyClasses) {
                // Skip the abstract class (if any) because it cannot (and should not) be instantiated.
                if (!Modifier.isAbstract(c.getModifiers()) && ITradingStrategy.class.isAssignableFrom(c)) {
                    ITradingStrategy strategy = (ITradingStrategy) c.newInstance();
                    strategyNames.add(strategy.getName());
                    valueMap.put(strategy.getName(), strategy);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public List<String> getStrategies() {
        return strategyNames;
    }

    /**
     * Create an instance of the algorithm given the controller and a refreshable.
     */
    public ITradingStrategy getStrategy(String name) {
        if (!valueMap.containsKey(name)) {
            throw new IllegalArgumentException("Could not find strategy with name " + name);
        }
        return valueMap.get(name);
    }

}
