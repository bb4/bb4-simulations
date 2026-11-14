// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common.ui

import com.barrybecker4.ui.sliders.{SliderGroup, SliderGroupChangeListener}
import javax.swing.JPanel


/**
  * Shared DynamicOptions implementation for population simulations that show
  * one [[CreatureSliderControls]] group per creature population.
  * @author Barry Becker
  */
class CreaturePopulationOptionsPanel(
  groups: Seq[SliderGroup & CreatureSliderControls]
) extends JPanel with SliderGroupChangeListener {

  DynamicOptionsPanelSupport.configureVerticalOptionsPanel(this)

  private val sliderGroups: Seq[SliderGroup & CreatureSliderControls] = groups.map { group =>
    group.setSliderListener(this)
    add(group)
    group
  }

  add(DynamicOptionsPanelSupport.createScrollFiller())

  def update(): Unit = sliderGroups.foreach(_.update())
  def reset(): Unit = sliderGroups.foreach(_.reset())

  /** One of the sliders was moved. */
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit =
    sliderGroups.foreach(_.checkSliderChanged(sliderName, value))
}
