// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.options

import com.barrybecker4.simulation.habitat.HabitatSimulator
import com.barrybecker4.ui.sliders.SliderGroupChangeListener
import javax.swing._
import java.awt._
import scala.collection.mutable.ArrayBuffer


/**
  * Dynamic controls for the RD simulation that will show on the right.
  * They change the behavior of the simulation while it is running.
  * @author Barry Becker
  */
class DynamicOptions(val simulator: HabitatSimulator) extends JPanel with SliderGroupChangeListener {

  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
  setBorder(BorderFactory.createEtchedBorder)
  setPreferredSize(new Dimension(300, 300))
  private var sliderGroups = ArrayBuffer[CreatureSliderGroup]()

  for (creaturePop <- simulator.getPopulations) {
    val group = new CreatureSliderGroup(creaturePop)
    group.addSliderChangeListener(this)
    sliderGroups.append(group)
    add(group)
  }
  val fill = new JPanel
  fill.setPreferredSize(new Dimension(1, 1000))
  add(fill)


  def update() {}
  def reset(){ for (group <- sliderGroups) group.reset() }

  /** One of the sliders was moved. */
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit = {
    for (group <- sliderGroups) {
      group.checkSliderChanged(sliderName, value)
    }
  }
}
