// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid

import com.barrybecker4.simulation.liquid.compute.GridUpdater
import com.barrybecker4.ui.sliders.SliderGroup
import com.barrybecker4.ui.sliders.SliderGroupChangeListener
import com.barrybecker4.ui.sliders.SliderProperties
import javax.swing._
import java.awt._
import java.awt.event.ActionEvent
import java.awt.event.ActionListener



/**
  * Dynamic controls for the liquid simulation that will show on the right.
  * They change the behavior of the simulation while it is running.
  * @author Barry Becker
  */
object LiquidDynamicOptions {
  private val VISCOSITY_SLIDER = "Viscosity"
  private val B0_SLIDER = "Relaxation Coefficient"
  private val TIMESTEP_SLIDER = "Time Step Size"
}

class LiquidDynamicOptions private[liquid](var liquidSim: LiquidSimulator)
  extends JPanel with SliderGroupChangeListener with ActionListener {

  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
  setBorder(BorderFactory.createEtchedBorder)
  setPreferredSize(new Dimension(300, 300))
  private var sliderGroup = new SliderGroup(createSliderProperties)
  sliderGroup.addSliderChangeListener(this)
  add(sliderGroup)
  private var advectionOnlyCheckBox = createCheckBox(
    "Do advection only", "If checked we will not apply the Navier Stokes solver", liquidSim.getAdvectionOnly)

  add(advectionOnlyCheckBox)
  val fill = new JPanel
  fill.setPreferredSize(new Dimension(10, 1000))
  add(fill)


  protected def createCheckBox(label: String, ttip: String, initialValue: Boolean): JCheckBox = {
    val cb = new JCheckBox(label, initialValue)
    cb.setToolTipText(ttip)
    cb.addActionListener(this)
    cb
  }

  private def createSliderProperties = {
    var sliderProps = Array[SliderProperties](//              MIN   MAX    INITIAL    SCALE
      new SliderProperties(LiquidDynamicOptions.VISCOSITY_SLIDER, 0.0, 0.1, GridUpdater.DEFAULT_VISCOSITY, 100),
      new SliderProperties(LiquidDynamicOptions.B0_SLIDER, 1.0, 2.0, GridUpdater.DEFAULT_B0, 100),
      new SliderProperties(LiquidDynamicOptions.TIMESTEP_SLIDER, 0.001, 0.4, 0.01, 1000)
    )
    sliderProps
  }

  def reset() { sliderGroup.reset() }

  /** One of the sliders was moved. */
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double) {
    if (sliderName == LiquidDynamicOptions.VISCOSITY_SLIDER) liquidSim.getEnvironment.setViscosity(value)
    else if (sliderName == LiquidDynamicOptions.B0_SLIDER) liquidSim.getEnvironment.setB0(value)
    else if (sliderName == LiquidDynamicOptions.TIMESTEP_SLIDER) liquidSim.setTimeStep(value)
  }

  override def actionPerformed(e: ActionEvent) {
    if (e.getSource eq advectionOnlyCheckBox) liquidSim.setAdvectionOnly(advectionOnlyCheckBox.isSelected)
  }
}
