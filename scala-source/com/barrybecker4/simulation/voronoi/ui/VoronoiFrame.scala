// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.ui

import javax.swing.JFrame
import java.awt.BorderLayout


class VoronoiFrame(val numPoints: Int) extends JFrame("VoronoiProcessor Visualization") {
  val panel = new VoronoiPanel(numPoints)
  getContentPane.setLayout(new BorderLayout)
  getContentPane.add(panel, BorderLayout.CENTER)
  getContentPane.add(panel)
  pack()
  setVisible(true)
  panel.start()
}
