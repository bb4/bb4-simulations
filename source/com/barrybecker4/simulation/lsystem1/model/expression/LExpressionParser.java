// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem1.model.expression;

import com.barrybecker4.common.expression.ExpressionParser;
import com.barrybecker4.common.expression.Tokens;
import com.barrybecker4.common.expression.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses the text form of an L-system expression into a tree representation.
 * See https://en.wikipedia.org/wiki/L-system
 * @author Barry Becker
 */
public class LExpressionParser extends ExpressionParser {

    /** Constructor */
    public LExpressionParser() {
        super(new LOperatorsDefinition());
    }

    /**
     * Recursive method to find all the tree nodes for the terms a the current level.
     * For example, given this expression
     * FF(F(-F)(++F))
     * the items in []'s represent the array of nodes returned.
     * [FF][F(-F)][++F]
     * The parts that were in ()'s become their own subtrees via recursive calls.
     *
     * @param exp the expression to get the nodes at the current level for
     * @return array of nodes representing terms that the current level.
     * @throws Error if there is a syntax error causing the expression to be invalid
     */
    @Override
    protected List<TreeNode> getNodesAtLevel(String exp) {

        int pos = 0;
        List<TreeNode> nodes = new ArrayList<TreeNode>();
        String token = "";
        char ch = exp.charAt(pos);

        while (pos < exp.length() && !token.equals("" + Tokens.RIGHT_PAREN.getSymbol())) {
            if (ch == ' ') {
                // spaces are ignored
            }
            else if (ch == Tokens.LEFT_PAREN.getSymbol()) {

                if (token.length() > 0)  {
                    nodes.add(new TreeNode(token, opDef));
                    token = "";
                }

                int closingParenPos = findClosingParen(exp, pos + 1);
                // this method will make the recursive call
                processSubExpression(exp, pos + 1, token, closingParenPos, nodes);
                pos = closingParenPos;
            }
            else if (LTokens.isTerminal(ch)) {
                token += ch;
            }
            else {
                throw new IllegalStateException("Unexpected character " + ch +" in expression: " + exp);
            }
            pos++;
            if (pos < exp.length()) {
                ch = exp.charAt(pos);
            }
        }
        // add the final node
        if (token.length() > 0) {
            pushNodesForToken(token, nodes);
        }

        return nodes;
    }

    /**
     * Parse a parenthesized sub expression recursively.
     * @return current token. May have been reset to "".
     */
    @Override
    protected String processSubExpression(
        String exp, int pos, String token, int closingParenPos, List<TreeNode> nodes) {

        // recursive call for sub expression
        TreeNode subTree = parse(exp.substring(pos, closingParenPos));
        subTree.hasParens = true;
        nodes.add(subTree);
        return token;
    }

    /**
     * Converts a list of nodes to a single node by reducing them to
     * subtrees in order of operator precedence.
     */
    @Override
    protected TreeNode makeTreeFromNodes(List<TreeNode> nodes) {

        TreeNode node = new TreeNode("", opDef);
        node.children.addAll(nodes);

        nodes.clear();
        nodes.add(node);

        return nodes.get(0);
    }

    @Override
    protected void pushNodesForToken(String token, List<TreeNode> nodes) {
        if (token == null) return;
        nodes.add(new TreeNode(token, opDef));
    }
}
