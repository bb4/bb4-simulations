// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.model.expression;

import com.barrybecker4.common.expression.TreeNode;
import junit.framework.TestCase;

/**
 * @author Barry Becker
 */
public class LExpressionParserTest extends TestCase {

    /** instance under test */
    private LExpressionParser parser;

    /** used to verify parsed tree */
    private LTreeSerializer serializer;

    /**
     * common initialization for all go test cases.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        parser = new LExpressionParser();
        serializer = new LTreeSerializer();
    }


    public void testFOnlyExp() {
        verifyParse("F");
    }

    public void testSimpleExp() {
        verifyParse("F(-F)");
    }

    public void testExpWithSpaces() {
        verifyParse("F (- F)", "F(-F)");
    }

    public void testTwoLevelNestedExp() {
        verifyParse("FF(F(-F))(++F)");
    }

    /**
     * @param exp the expression to parse
     */
    private void verifyParse(String exp) {
        verifyParse(exp, exp);
    }


    /**
     * @param exp the expression to parse
     */
    private void verifyParse(String exp, String expSerializedStr) {
        TreeNode root = parser.parse(exp);
        String serialized = serializer.serialize(root);
        assertEquals("Unexpected parsed expression tree.",
                expSerializedStr, serialized);
    }

}
