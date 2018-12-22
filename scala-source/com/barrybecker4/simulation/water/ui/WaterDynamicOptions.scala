// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.water.ui

import java.awt._
import java.awt.event.{ActionEvent, ActionListener}

import com.barrybecker4.simulation.water.model
import com.barrybecker4.ui.legend.ContinuousColorLegend
import com.barrybecker4.ui.sliders.{SliderGroup, SliderGroupChangeListener, SliderProperties}
import javax.swing._


/**
  * Dynamic controls for the Fluid simulation.
  * @author Barry Becker
  */
object WaterDynamicOptions {

  private val VISC_SLIDER = "Viscosity"
  private val TIME_STEP_SLIDER = "Time Step"
  private val DEFAULT_VISCOSITY = 0

  private val SLIDER_PROPS = Array(
    new SliderProperties(VISC_SLIDER, 0, 50.0, DEFAULT_VISCOSITY, 100.0),
    new SliderProperties(TIME_STEP_SLIDER, 0.0001, 0.1, WaterSimulator.INITIAL_TIME_STEP, 10000.0)
  )
}

class WaterDynamicOptions private[ui](var simulator: WaterSimulator)
  extends JPanel with ActionListener with SliderGroupChangeListener {

  private var showVelocities: JCheckBox = _

  setBorder(BorderFactory.createEtchedBorder)
  val controlsPanel = new JPanel
  controlsPanel.setLayout(new BoxLayout(controlsPanel, BoxLayout.Y_AXIS))
  this.add(controlsPanel, BorderLayout.CENTER)
  private var sliderGroup = new SliderGroup(WaterDynamicOptions.SLIDER_PROPS)
  sliderGroup.addSliderChangeListener(this)
  sliderGroup.setPreferredSize(new Dimension(300, 260))
  val checkBoxes: JPanel = createCheckBoxes

  controlsPanel.add(sliderGroup)
  controlsPanel.add(Box.createVerticalStrut(10))
  controlsPanel.add(checkBoxes)
  controlsPanel.add(Box.createVerticalStrut(10))

  private def createCheckBoxes = {
    val renderOpts = simulator.getRenderer.getOptions
    showVelocities = createCheckBox("Show velocities",
      "if checked, show velocity vectors", renderOpts.getShowVelocities)
    val checkBoxes = new JPanel(new GridLayout(0, 2))
    checkBoxes.add(showVelocities)
    checkBoxes.setBorder(BorderFactory.createEtchedBorder)
    checkBoxes
  }

  private def createCheckBox(label: String, tooltip: String, initiallyChecked: Boolean) = {
    val cb = new JCheckBox(label, initiallyChecked)
    cb.setToolTipText(tooltip)
    cb.addActionListener(this)
    cb
  }

  def reset(): Unit = {
    sliderGroup.reset()
    // make sure we honor current check selections
    val renderOpts = simulator.getRenderer.getOptions
    renderOpts.setShowVelocities(showVelocities.isSelected)
  }

  /** One of the buttons was pressed */
  override def actionPerformed(e: ActionEvent): Unit = {
    val renderer = simulator.getRenderer
    val renderOpts = renderer.getOptions
    if (e.getSource eq showVelocities) renderOpts.setShowVelocities(!renderOpts.getShowVelocities)
  }

  /** One of the sliders was moved. */
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit = {
    sliderName match {
      //case WaterDynamicOptions.VISC_SLIDER => simulator.getEnvironment.setViscosity(value)
      case WaterDynamicOptions.TIME_STEP_SLIDER => simulator.setTimeStep(value)
    }
  }
}
