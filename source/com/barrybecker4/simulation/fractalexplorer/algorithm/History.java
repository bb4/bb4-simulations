/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer.algorithm;

import com.barrybecker4.common.math.ComplexNumberRange;

import java.util.LinkedList;

/**
 * Maintains regions (ranges) in the complex space that were visited, so
 * we can back up.
 * @author Barry Becker
 */
public class History {


    /** range of bounding box in complex plane. */
    private LinkedList<ComplexNumberRange> stack;


    public History() {
        stack = new LinkedList<ComplexNumberRange>();
    }


    public void addRangeToHistory(ComplexNumberRange range)  {
        stack.push(range);
    }

    /**
     * Back up one step in the history
     * @return the last range viewed in the history
     */
    public ComplexNumberRange popLastRange() {
         return stack.pop();
    }

}
