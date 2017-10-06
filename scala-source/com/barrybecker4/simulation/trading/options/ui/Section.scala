// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.options.ui

import javax.swing._
import javax.swing.border.Border
import java.awt._


/**
  * @author Barry Becker
  */
object Section {
  private val HORZ_MARGIN = 8
  private val VERT_MARGIN = 4
  private val OUTER_MARGIN_COLOR = new Color(255, 255, 255, 200) // new Color(50, 80, 200, 70);
  private val MATTE_COLOR = new Color(40, 70, 180, 0)

  def createBorder(title: String): Border =
    BorderFactory.createCompoundBorder(
      BorderFactory.createMatteBorder(VERT_MARGIN, HORZ_MARGIN, VERT_MARGIN, HORZ_MARGIN, OUTER_MARGIN_COLOR),
      BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(VERT_MARGIN, HORZ_MARGIN, VERT_MARGIN, HORZ_MARGIN, MATTE_COLOR),
        BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(title),
          BorderFactory.createMatteBorder(VERT_MARGIN, HORZ_MARGIN, VERT_MARGIN, HORZ_MARGIN, MATTE_COLOR)
        )
      )
    )
}
