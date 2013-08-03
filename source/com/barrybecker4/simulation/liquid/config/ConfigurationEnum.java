/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid.config;

/**
 * Different configurations to choose from.
 *
 * @author Bary Becker
 */
public enum ConfigurationEnum {


    SPIGOT_RIGHT("Spigot to the Right", "A spigot aimed to the right", getFileBase() + "spigotRight.xml"),
    SPIGOT_LEFT("Spigot to the Left", "A spigot aimed to the left", getFileBase() + "spigotLeft.xml"),
    BASIC("Basic", "A stream of water into a pool",  getFileBase() + "config1.xml"),
    FALLING_BLOB("Falling water", "A falling blob of water onto the floor",  getFileBase() + "fallingWater.xml"),
    FALLING_BLOB_SMALL("Falling water small", "A small falling blob of water onto the floor",  getFileBase() + "fallingWaterSmall.xml"),
    WATER_WALL_LEFT("Water wall (left)", "An initial wall of water on the left", getFileBase() + "wallOfWaterLeft.xml"),
    WATER_WALL_RIGHT("Water wall (right)", "An initial wall of water on the right", getFileBase() + "wallOfWaterRight.xml"),
    PULSE_LARGE("Pulsing spigot", "Water pulsing out of the spigot", getFileBase() + "pulse_large.xml"),
    PULSE_SMALL("Pulsing spigot (3x3)", "Water pulsing out of the spigot (3x3)", getFileBase() + "pulse_small.xml"),
    PULSE_SMALLEST("Pulsing spigot (2x2)", "Water pulsing out of the spigot (2x2)", getFileBase() + "pulse_smallest.xml");


    private String name;
    private String description;
    private String fileName;

    /**
     * Constructor
     * @param name the nice name for the configuration that will show in the dropDown.
     * @param fileName the actual xml filename that contains the config data.
     */
    ConfigurationEnum(String name, String description, String fileName) {
        this.name = name;
        this.description = description;
        this.fileName = fileName;
    }

    private static String getFileBase() {
        return "com/barrybecker4/simulation/liquid/data/";
    }

    public static ConfigurationEnum getDefaultValue() {
        return PULSE_LARGE;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name;
    }
}
