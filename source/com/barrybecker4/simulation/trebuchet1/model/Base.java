/*
 * // Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */
package com.barrybecker4.simulation.trebuchet1.model;

import java.awt.*;

/**
 * @author Barry Becker   (Sep 25, 2005)
 */
public class Base extends RenderablePart {

    private static final int BASE_WIDTH = 400;
    private static final int STRUT_BASE_HALF_WIDTH = 50;

    private static final BasicStroke BASE_STROKE = new BasicStroke(2.0f);
    private static final Color BASE_COLOR = new Color(10, 40, 160);

    Base() {}

    public void render(Graphics2D g2, double scale) {

        g2.setStroke(BASE_STROKE);
        g2.setColor(BASE_COLOR);

        g2.draw3DRect((int) (scale * BASE_X), BASE_Y, (int) (scale * BASE_WIDTH), (int) (scale * 10.0), false);
        g2.drawLine((int) (scale * (STRUT_BASE_X - STRUT_BASE_HALF_WIDTH)),  BASE_Y,
                    (int) (scale * STRUT_BASE_X), (int) (BASE_Y - scale * SCALE_FACTOR * height));
        g2.drawLine((int) (scale * (STRUT_BASE_X + STRUT_BASE_HALF_WIDTH)),  BASE_Y,
                    (int) (scale * STRUT_BASE_X), (int) (BASE_Y - scale * SCALE_FACTOR * height));
    }
}
