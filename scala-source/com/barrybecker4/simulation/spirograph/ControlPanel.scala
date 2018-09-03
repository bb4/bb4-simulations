/** Copyright by Barry G. Becker, 2000-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.spirograph

import com.barrybecker4.simulation.spirograph.model.GraphState
import com.barrybecker4.simulation.spirograph.model.GraphStateChangeListener
import com.barrybecker4.common.app.AppContext
import com.barrybecker4.ui.components.GradientButton
import com.barrybecker4.ui.sliders.ColorSliderGroup
import javax.swing._
import javax.swing.border.EtchedBorder
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener


/**
  * A panel that holds sliders and buttons that control the rendering of the spirograph.
  * @author Barry Becker
  */
class ControlPanel(var graphPanel: GraphPanel, var state: GraphState)
  extends JPanel with ActionListener with GraphStateChangeListener {

  setBorder(BorderFactory.createEmptyBorder(4, 4, 12, 3))
  this.state.addStateListener(this)
  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
  private val sliderGroup: ControlSliderGroup = new ControlSliderGroup(graphPanel, state)
  add(sliderGroup)
  add(createButtonGroup)
  val colorSelector = new ColorSliderGroup
  colorSelector.setColorChangeListener(this.state)
  add(colorSelector)
  val fill = new JPanel
  fill.setPreferredSize(new Dimension(100, 1000))
  add(fill)
  add(createFunctionPanel)
  parameterChanged()
  private var xFunction: JLabel = _
  private var yFunction: JLabel = _
  private var hideButton: GradientButton = _
  private var drawButton: GradientButton = _

  private def createButtonGroup = {
    val bp = new JPanel(new BorderLayout)
    val p = new JPanel
    p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS))
    p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5), BorderFactory.createEtchedBorder(EtchedBorder.RAISED)))
    hideButton = createButton(AppContext.getLabel("HIDE_DECORATION"))
    val reset = createButton(AppContext.getLabel("RESET"))
    drawButton = createButton(AppContext.getLabel("DRAW"))
    val bl = new JPanel(new BorderLayout)
    bl.add(hideButton, BorderLayout.CENTER)
    p.add(createButtonPanel(hideButton))
    p.add(createButtonPanel(reset))
    p.add(createButtonPanel(drawButton))
    bp.add(p, BorderLayout.CENTER)
    bp
  }

  private def createFunctionPanel = {
    val functionPanel = new JPanel
    functionPanel.setLayout(new GridLayout(2, 1, 0, 0))
    xFunction = new JLabel("", SwingConstants.CENTER)
    yFunction = new JLabel("", SwingConstants.CENTER)
    functionPanel.add(xFunction)
    functionPanel.add(yFunction)
    functionPanel
  }

  private def createButton(label: String) = {
    val button = new GradientButton(label)
    button.addActionListener(this)
    button
  }

  private def createButtonPanel(button: GradientButton) = {
    val bp = new JPanel(new BorderLayout)
    bp.add(button, BorderLayout.CENTER)
    bp
  }

  /** a button was pressed. */
  override def actionPerformed(e: ActionEvent): Unit = {
    val source = e.getSource
    assert(source.isInstanceOf[GradientButton])
    val obj = source.asInstanceOf[AbstractButton].getText
    if (sliderGroup.getRadius2Value != 0) if (AppContext.getLabel("DRAW") == obj) {
      drawButton.setText(AppContext.getLabel("PAUSE"))
      graphPanel.startDrawingGraph()
    }
    else if (AppContext.getLabel("PAUSE") == obj) {
      graphPanel.setPaused(true)
      drawButton.setText(AppContext.getLabel("RESUME"))
    }
    else if (AppContext.getLabel("RESUME") == obj) {
      graphPanel.setPaused(false)
      drawButton.setText(AppContext.getLabel("PAUSE"))
    }
    if (AppContext.getLabel("RESET") == obj) {
      graphPanel.reset()
      drawButton.setText(AppContext.getLabel("DRAW"))
    }
    else if (AppContext.getLabel("HIDE_DECORATION") == obj) {
      hideButton.setText(AppContext.getLabel("SHOW_DECORATION"))
      state.showDecoration = false
      graphPanel.repaint()
    }
    else if (AppContext.getLabel("SHOW_DECORATION") == obj) {
      hideButton.setText(AppContext.getLabel("HIDE_DECORATION"))
      state.showDecoration = true
      graphPanel.repaint()
    }
  }

  /** implements GraphStateChangeListener interface */
  override def parameterChanged(): Unit = {
    val equations = sliderGroup.getEquations
    xFunction.setText(equations.getXEquation)
    yFunction.setText(equations.getYEquation)
    if (state.isMaxVelocity) drawButton.setText(AppContext.getLabel("DRAW"))
  }

  override def renderingComplete(): Unit = {
    drawButton.setText(AppContext.getLabel("DRAW"))
  }
}