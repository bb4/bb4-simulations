// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid

import com.barrybecker4.ui.sliders.SliderGroup
import com.barrybecker4.ui.sliders.SliderGroupChangeListener
import com.barrybecker4.ui.sliders.SliderProperties

import javax.swing.*
import java.awt.*
import com.barrybecker4.simulation.liquid.model.UiParameter

import scala.collection.immutable.List


/**
  * Dynamic controls for the liquid simulation that will show on the right.
  * They change the behavior of the simulation while it is running.
  */
class LiquidDynamicOptions private[liquid](var liquidSim: LiquidSimulator)
  extends JPanel with SliderGroupChangeListener {

  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
  setBorder(BorderFactory.createEtchedBorder)
  setPreferredSize(new Dimension(300, 300))

  private val uiParameters = liquidSim.getEnvironment.getUiParameters()
  private val sliderProperties = createSliderProperties(uiParameters)
  private val sliderGroup = new SliderGroup(sliderProperties)
  sliderGroup.setSliderListener(this)
  add(sliderGroup)
  
  val fill = new JPanel
  fill.setPreferredSize(new Dimension(10, 1000))
  add(fill)


  protected def createCheckBox(label: String, ttip: String, initialValue: Boolean): JCheckBox = {
    val cb = new JCheckBox(label, initialValue)
    cb.setToolTipText(ttip)
    cb
  }

  private def createSliderProperties(uiParameters: List[UiParameter]): Array[SliderProperties] = {
    uiParameters.map(parameter => {
      new SliderProperties(
        parameter.displayName,
        parameter.minValue,
        parameter.maxValue,
        parameter.initialValue,
        parameter.scale
      )
    }).toArray
  }

  def reset(): Unit = { sliderGroup.reset() }

  /** One of the sliders was moved. */
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit = {
    if (sliderName == "time step (dt)") {
      liquidSim.setTimeStep(value)
    } else {
      val env = liquidSim.getEnvironment
      val fieldName = uiParameters(sliderIndex).name

      var currentClass: Class[_] = env.getClass
      var fieldFound = false

      while (currentClass != null && !fieldFound) { // search inheritance hierarchy
        try {
          val field = currentClass.getDeclaredField(fieldName)
          field.setAccessible(true)
          //println(s"Setting field $fieldName in ${currentClass.getSimpleName} = $value")
          field.set(env, value)
          fieldFound = true
        } catch {
          case _: NoSuchFieldException =>
            currentClass = currentClass.getSuperclass
        }
      }

      if (!fieldFound) {
        println(s"Could not find field '$fieldName' in class hierarchy of ${env.getClass.getSimpleName}")
      }
    }
  }
  
}
