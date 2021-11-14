// Copyright by Barry G. Becker, 2013-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.sierpinski

import com.barrybecker4.ui.application.ApplicationApplet
import com.barrybecker4.ui.util.GUIUtil
import javax.swing._
import java.awt._


object SierpinskiApplet {
  def main(args: Array[String]): Unit = {
    val simulator = new SierpinskiApplet
    GUIUtil.showApplet(simulator)
  }
}

/**
  * @author Barry Becker
  */
class SierpinskiApplet() extends ApplicationApplet {

  override def createMainPanel: JPanel = {
    val sierpinskiComp = new SierpinskiComponent
    sierpinskiComp.setBorder(BorderFactory.createEtchedBorder)
    val mainPanel = new JPanel
    mainPanel.setLayout(new BorderLayout)
    mainPanel.add(sierpinskiComp, BorderLayout.CENTER)
    mainPanel
  }

  override def getName = "Sierpinski Triangle"
}