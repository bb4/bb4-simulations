// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake

import com.barrybecker4.ui.sliders.SliderGroup
import com.barrybecker4.ui.sliders.SliderGroupChangeListener
import com.barrybecker4.ui.sliders.SliderProperties
import javax.swing._
import java.awt._


/**
  * Dynamic controls for the RD simulation that will show on the right.
  * They change the behavior of the simulation while it is running.
  *
  * @author Barry Becker
  */
object SnakeDynamicOptions {
  private val DIRECTION_SLIDER = "Direction"
  private val WAVE_SPEED_SLIDER = "Wave Speed"
  private val WAVE_AMPLITUDE_SLIDER = "Wave Amplitude"
  private val WAVE_PERIOD_SLIDER = "Wave Period"
  private val MASS_SCALE_SLIDER = "Mass Scale"
  private val SPRING_CONST_SLIDER = "Spring Constant"
  private val SPRING_DAMPING_SLIDER = "Spring Damping"
  private val SCALE_SLIDER = "Scale"
  private val TIMESTEP_SLIDER = "Time Step Size"
}

class SnakeDynamicOptions private[snake](var snakeSim: SnakeSimulator) extends JPanel with SliderGroupChangeListener {
  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
  setBorder(BorderFactory.createEtchedBorder)
  setPreferredSize(new Dimension(300, 300))
  private var sliderGroup = new SliderGroup(createSliderProperties)
  sliderGroup.addSliderChangeListener(this)
  add(sliderGroup)
  val fill = new JPanel
  fill.setPreferredSize(new Dimension(10, 1000))
  add(fill)

  private def createSliderProperties: Array[SliderProperties] = {
    val params = new LocomotionParameters
    Array[SliderProperties](//                                     MIN  MAX   INITIAL   SCALE
      new SliderProperties(SnakeDynamicOptions.DIRECTION_SLIDER, -1.0, 1.0, params.direction, 100),
      new SliderProperties(SnakeDynamicOptions.WAVE_SPEED_SLIDER, 0.00001, 0.01, params.waveSpeed, 1000),
      new SliderProperties(SnakeDynamicOptions.WAVE_AMPLITUDE_SLIDER, 0.000, 0.3, params.waveAmplitude, 100),
      new SliderProperties(SnakeDynamicOptions.WAVE_PERIOD_SLIDER, .5, 10.0, params.wavePeriod, 100),
      new SliderProperties(SnakeDynamicOptions.MASS_SCALE_SLIDER, 0.1, 6.0, params.massScale, 100),
      new SliderProperties(SnakeDynamicOptions.SPRING_CONST_SLIDER, 0.1, 4.0, params.springK, 100),
      new SliderProperties(SnakeDynamicOptions.SPRING_DAMPING_SLIDER, 0.1, 4.0, params.springDamping, 100),
      new SliderProperties(SnakeDynamicOptions.SCALE_SLIDER, 0.2, 2.0, snakeSim.getScale, 100),
      new SliderProperties(SnakeDynamicOptions.TIMESTEP_SLIDER, 0.001, 0.5, SnakeSimulator.INITIAL_TIME_STEP, 1000))
  }

  def reset() { sliderGroup.reset() }

  /** One of the sliders was moved. */
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit = {
    val params = snakeSim.getLocomotionParams
    sliderName match {
      case SnakeDynamicOptions.DIRECTION_SLIDER => params.direction = value
      case SnakeDynamicOptions.WAVE_SPEED_SLIDER => params.waveSpeed = value
      case SnakeDynamicOptions.WAVE_AMPLITUDE_SLIDER => params.waveAmplitude = value
      case SnakeDynamicOptions.WAVE_PERIOD_SLIDER => params.wavePeriod = value
      case SnakeDynamicOptions.MASS_SCALE_SLIDER => params.massScale = value
      case SnakeDynamicOptions.SPRING_CONST_SLIDER => params.springK = value
      case SnakeDynamicOptions.SPRING_DAMPING_SLIDER => params.springDamping = value
      case SnakeDynamicOptions.SCALE_SLIDER => snakeSim.setScale(value)
      case SnakeDynamicOptions.TIMESTEP_SLIDER => snakeSim.setTimeStep(value)
    }
  }
}
