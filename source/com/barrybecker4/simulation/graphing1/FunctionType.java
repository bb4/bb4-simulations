/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.graphing1;

import com.barrybecker4.common.math.function.ArrayFunction;
import com.barrybecker4.common.math.function.ErrorFunction;
import com.barrybecker4.common.math.function.Function;

/**
 * Different types of functions to test.
 *
 * @author Barry Becker
 */
public enum FunctionType {

    DIAGONAL("Two point diagonal", getDiagonalFunc()),
    HORZ_LINE("Horizontal Line", getHorzLineFunc()),
    VERT_LINE("Vertical Line", getVertLineFunc()),
    SQUARE("Square Function", getSquareFunc()),
    TEETH("Teeth", getTeethFunc()),
    JAGGED("Jagged", getJaggedFunc()),
    ERROR("Error Function", getErrorFunc()),
    SMOOTH("Smooth Function", getSmoothFunc()),
    TYPICAL_SMOOTH("Typical Smooth", getTypicalSmoothFunc()),
    V("V Function", getVFunc());

    private String name;
    public Function function;

    private static final double[] NULL_FUNC = null;

    /**
     * Constructor
     */
    FunctionType(String name, Function function) {
        this.name = name;
        this.function = function;
    }


    private static Function getHorzLineFunc() {
        double[] data = {1.0, 1.0};
        return new ArrayFunction(data, NULL_FUNC);
    }

    private static Function getVertLineFunc() {
        double[] data = {0.0, 0.0, 0.0, 1.0, 1.0, 1.0};
        return new ArrayFunction(data, NULL_FUNC);
    }

    private static Function getDiagonalFunc() {
        double[] data = {0.0, 1.0};
        return new ArrayFunction(data, NULL_FUNC);
    }

    private static Function getVFunc() {
        double[] data = {1.0, 0.0, 1.0};
        return new ArrayFunction(data, NULL_FUNC);
    }

     private static Function getSquareFunc() {
        double[] data = {0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0};
        return new ArrayFunction(data, NULL_FUNC);
    }

    private static Function getTeethFunc() {
        double[] data = {0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0};
        return new ArrayFunction(data, NULL_FUNC);
    }

    private static Function getJaggedFunc() {
        double[] data = {0.0, .5, 0.1, 0.8, 0.6, 1.0};
        return new ArrayFunction(data, NULL_FUNC);
    }

    private static Function getSmoothFunc() {
        double[] data = {0.0, 0.1, 0.25, 0.5, 0.75, 0.9, 1.0};
        return new ArrayFunction(data);
    }

     private static Function getTypicalSmoothFunc() {
        double[] data = {0.0, .1, 0.3, 0.6, 0.7, 0.75, 0.7, 0.5, 0.4, 0.36, 0.39, 0.45, 0.56, 0.7, 1.0};
        return new ArrayFunction(data, NULL_FUNC);
    }

    private static Function getErrorFunc() {
        return new ErrorFunction();
    }

    public String toString() {
        return name;
    }
}