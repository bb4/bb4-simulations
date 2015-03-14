package com.barrybecker4.simulation.trading.options.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * @author Barry Becker
 */
public class Section {

    private static final int HORZ_MARGIN = 8;
    private static final int VERT_MARGIN = 4;

    private static final Color OUTER_MARGIN_COLOR = new Color(255, 255, 255, 200);// new Color(50, 80, 200, 70);
    private static final Color MATTE_COLOR =  new Color(40, 70, 180, 0);


    public static Border createBorder(String title) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(VERT_MARGIN, HORZ_MARGIN, VERT_MARGIN, HORZ_MARGIN, OUTER_MARGIN_COLOR),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(VERT_MARGIN, HORZ_MARGIN, VERT_MARGIN, HORZ_MARGIN, MATTE_COLOR),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder(title),
                                BorderFactory.createMatteBorder(VERT_MARGIN, HORZ_MARGIN, VERT_MARGIN, HORZ_MARGIN, MATTE_COLOR)
                        )
                )
        );
    }
}
