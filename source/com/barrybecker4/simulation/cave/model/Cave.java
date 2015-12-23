package com.barrybecker4.simulation.cave.model;

import java.util.Random;

/**
 * @author Barry Becker
 */
public class Cave {

    public static final byte WALL = 1;
    public static final byte NEW_WALL = 2;
    public static final byte FLOOR = 0;
    public static final byte NEW_FLOOR = -1;

    private static final int SEED = 0;
    private static final Random RAND = new Random();

    //private int width;
    //private int height;

    /** byte array. Positive values indicates solid rock */
    private byte[][] map;

    public Cave(int width, int height, double density) {
        map = genMap(width, height, density);
    }

    public int getWidth() {
        return map.length;
    }
    public int getHeight() {
        return map[0].length;
    }

    public boolean isWall(int x, int y) {
        byte val = map[x][y];
        return val == WALL || val == NEW_WALL;
    }

    public void setValue(int x, int y, byte value) {
        map[x][y] = value;
    }

    public Cave createCopy() {
        Cave newCave = new Cave(getWidth(), getHeight(), 0);
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                newCave.setValue(x, y, map[x][y]);
            }
        }
        return newCave;
    }

    public byte getValue(int x, int y) {
        return map[x][y];
    }

    private char getChar(int x, int y) {
        char c;
        byte v = map[x][y];
        switch (v) {
            case FLOOR : c = ' '; break;
            case WALL : c = '0'; break;
            case NEW_WALL : c = 'W'; break;
            case NEW_FLOOR : c = '.'; break;
            default: throw new IllegalStateException("Unexpected value: " + v);
        }
        return c;
    }


    /** generate the initial random 2D map data */
    private byte[][] genMap(int width, int height, double density) {
        RAND.setSeed(SEED);
        byte[][] theMap = new byte[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                theMap[x][y] = (RAND.nextDouble() < density ? WALL : FLOOR);
            }
        }
        return theMap;
    }

    public void print() {
        System.out.println(this.toString());
    }

    public String toString() {
        StringBuilder bldr = new StringBuilder();
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                bldr.append(getChar(x, y));
                //if (map[x][y] > 0) bldr.append("0");
                //else bldr.append(" ");
            }
            bldr.append("\n");
        }
        return bldr.toString();
    }
}
