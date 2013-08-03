// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.model.expression;

/**
 * Terminal L-system tokens
 * @author Barry Becker
 */
public enum LTokens {

    PLUS('+'),
    MINUS('-'),
    F('F');

    private char symbol;

    LTokens(char c) {
       symbol = c;
    }

    public char getSymbol() {
        return symbol;
    }

    public static boolean isTerminal(char c) {
        return (c == F.getSymbol() || c == PLUS.getSymbol() || c == MINUS.getSymbol());
    }

}
