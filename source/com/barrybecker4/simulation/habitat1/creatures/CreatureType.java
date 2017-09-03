/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.habitat1.creatures;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Everything we need to know about a creature.
 * There are many different sorts of creatures.
 *
 * Add more creatures like sheep, fox, chickens, ants, vulture,
 *
 * @author Barry Becker
 */
public enum CreatureType {
                                        // size,   mSpeed,  normpeed, gest, starve,  nutr
    GRASS("grass", new Color(40, 255, 20),    2.0,    0.0,      0.0,    11,    42,     1),
    WILDEBEEST(  "cow",   new Color(70, 60, 100),   15.0,    0.002,    0.0001,  25,    94,    10),
    RAT(  "rat",   new Color(140, 105, 20),   2.0,    0.005,    0.002,   8,    30,     1),
    CAT(  "cat",   new Color(0, 195, 220),    5.0,    0.01,     0.004,  15,    58,     4),
    LION("lion",   new Color(240, 200, 20),   9.0,    0.02,    0.008,   21,    83,     6);


    private String name;
    private Color color;
    private double size;
    private double normalSpeed;
    private double maxSpeed;

    /** when numDaysPregnant = this then reproduce */
    private int gestationPeriod;

    /** when hunger >= this then die. */
    private int starvationThreshold;

    private int nutritionalValue;

    private static final Map<CreatureType, List<CreatureType>> predatorMap = new HashMap<CreatureType, List<CreatureType>>();
    private static final Map<CreatureType, List<CreatureType>> preyMap = new HashMap<CreatureType, List<CreatureType>>();


    static {
        // eaten by relationship
        predatorMap.put(GRASS, Arrays.asList(WILDEBEEST, RAT));
        predatorMap.put(WILDEBEEST, Arrays.asList(LION));
        predatorMap.put(RAT, Arrays.asList(CAT, LION));
        predatorMap.put(CAT, Arrays.asList(LION));
        predatorMap.put(LION, Collections.<CreatureType>emptyList());

        for (CreatureType creature : values()) {
            List<CreatureType> preys = new ArrayList<CreatureType>();

            for (CreatureType potentialprey : values()) {
                List<CreatureType> preds = predatorMap.get(potentialprey);
                if (preds.contains(creature)) {
                    preys.add(potentialprey);
                }
            }
            preyMap.put(creature, preys);
        }
    }

    CreatureType(String name, Color color, double size, double normlSpeed,
                 double maxSpeed, int gestationPeriod, int starvationTheshold, int nutritionalValue)  {
        this.name = name;
        this.color = color;
        this.size = size;
        this.normalSpeed = normlSpeed;
        this.maxSpeed = maxSpeed;
        this.gestationPeriod = gestationPeriod;
        this.starvationThreshold = starvationTheshold;
        this.nutritionalValue = nutritionalValue;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double s) {
        size = s;
    }

    public double getNormalSpeed() {
        return normalSpeed;
    }

    public void setNormalSpeed(double s) {
        normalSpeed = s;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double s) {
        maxSpeed = s;
    }

    public int getGestationPeriod() {
        return gestationPeriod;
    }

    public void setGestationPeriod(int g) {
        gestationPeriod = g;
    }

    public int getStarvationThreshold() {
        return starvationThreshold;
    }

    public void setStarvationThreshold(int value) {
        starvationThreshold = value;
    }

    public int getNutritionalValue() {
        return nutritionalValue;
    }

    public void setNutritionalValue(int value) {
        nutritionalValue = value;
    }

    public List<CreatureType> getPredators() {
        return predatorMap.get(this);
    }

    public List<CreatureType> getPreys() {
        return preyMap.get(this);
    }

    public String toString() {
        return getName();
    }

    /** for testing */
    public static void main(String[] args) {
        System.out.println("preyMap = " + preyMap);
    }
}
