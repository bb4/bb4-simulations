/** Copyright by Barry G. Becker, 2000-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.spirograph

import com.barrybecker4.ui.application.ApplicationApplet
import com.barrybecker4.ui.util.GUIUtil
import javax.swing._
import java.awt._


/**
  * That old spirograph game from the 70's brought into the computer age.
  * Based on work originally done by David Little.
  * http://www.math.psu.edu/dlittle/java/parametricequations/index.html
  * @author Barry Becker
  */
object Spirograph {

  /** Main method - to allow running as an application instead of applet.
    */
  def main(args: Array[String]): Unit = {
    val applet = new Spirograph
    GUIUtil.showApplet(applet)
  }
}

class Spirograph extends ApplicationApplet {
  override def createMainPanel: JPanel = {
    val state = ControlSliderGroup.createGraphState
    val graphPanel = new GraphPanel(state)
    val controlPanel = new ControlPanel(graphPanel, state)
    val mainPanel = new JPanel(new BorderLayout)
    mainPanel.add(BorderLayout.CENTER, graphPanel)
    mainPanel.add(BorderLayout.EAST, controlPanel)
    mainPanel
  }

  override def getName = "Spirograph"
}

