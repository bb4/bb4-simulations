/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.liquid.model;

/**
 * Possible status of the cell. determined by what's in it.
 *
 * @author Barry Becker
 */
public enum CellStatus {

    EMPTY('.'),      // no liquid
    SURFACE('S'),    // has liquid and full cell is adjacent
    FULL('F'),       // liquid on all sides
    OBSTACLE('o'),   // solid object (like a wall)
    ISOLATED('I');   // has liquid, but no full cells are adjacent


    /**
     * Symbol to use for the specific status.
     */
    private final char symbol_;

    /**
     * constructor for cell type enum
     *
     * @param symbol character representation of the type.
     */
    CellStatus(char symbol) {
       symbol_ = symbol;
    }

    /**
     * @return a unique symbol that represents the cell status.
     */
    public char getSymbol() {
        return symbol_;
    }
}
