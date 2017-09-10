// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem1.model.expression;

import com.barrybecker4.common.expression.Operator;
import com.barrybecker4.common.expression.OperatorsDefinition;
import com.barrybecker4.common.expression.TreeNode;

import java.util.List;

/**
 * The expected binary operators in the text expression.
 * @author Barry Becker
 */
public class LOperatorsDefinition implements OperatorsDefinition {

    /**
     * Defines the order of precedence for the operators
     * This at the same level are evaluated from left to right.
     */
    private static Operator[][] OPERATOR_PRECEDENCE = {};

    public Operator[][] getOperatorPrecedence() {
        return OPERATOR_PRECEDENCE;
    }

    /** @return true if the specified character is an operator */
    public boolean isOperator(char ch) {
        return false;
    }

    /** @return true if the last node is an operator or there were no previous nodes  */
    public boolean isLastNodeOperator(List<TreeNode> nodes) {
        return nodes.isEmpty() || nodes.get(nodes.size() - 1).isOperator();
    }
}