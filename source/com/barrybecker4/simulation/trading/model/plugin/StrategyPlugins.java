/*
 * Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/license.MIT
 */
package com.barrybecker4.simulation.trading.model.plugin;

import com.barrybecker4.common.util.PackageReflector;
import com.barrybecker4.simulation.trading.model.tradingstrategy.ITradingStrategy;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.security.AccessControlException;
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
public class StrategyPlugins<E extends IStrategyPlugin> {


    private List<String> strategyNames = new ArrayList<>();
    private Map<String, E> valueMap = new HashMap<>();


    /**
     * Constructor
     * Uses reflection to dynamically load available strategies.
     *
     * @param packageName name of the package to get the plugin classes from
     *   Something like "com.barrybecker4.simulation.trading.model.generationstrategy"
     */
    public StrategyPlugins(String packageName, Class<?> clzz, List<E> defaultStrategies)  {

        try {
            List<Class> strategyClasses =
                    new PackageReflector().getClasses(packageName);

            for (Class<?> c : strategyClasses) {
                // Skip the abstract class (if any) because it cannot (and should not) be instantiated.
                if (!Modifier.isAbstract(c.getModifiers()) && clzz.isAssignableFrom(c)) {
                    E strategy = (E) c.newInstance();
                    String name = strategy.getName();
                    strategyNames.add(name);
                    valueMap.put(name, strategy);
                }
            }
            System.out.println("Trading strategy plugins found: " + valueMap);
        } catch (AccessControlException | NullPointerException e) {
            // fallback to local strategies if cannot access filesystem (as for applet or webstart)
            System.out.println("Unable to access filesystem to load plugin. Will use default strategies only");
            for (E s : defaultStrategies) {
                valueMap.put(s.getName(), s);
            }
        } catch (ClassNotFoundException | InstantiationException | IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public List<String> getStrategies() {
        return strategyNames;
    }

    /**
     * Create an instance of the algorithm given the controller and a refreshable.
     */
    public E getStrategy(String name) {
        if (!valueMap.containsKey(name)) {
            System.out.println("Could not find strategy with name " + name + " among " + valueMap);
        }
        return valueMap.get(name);
    }

}
