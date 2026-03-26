// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.predprey.options

import com.barrybecker4.simulation.predprey.PredPreySimulator
import com.barrybecker4.ui.sliders.SliderGroupChangeListener
import javax.swing._
import java.awt._

/**
  * Dynamic controls for the predator–prey simulation (sliders on the right).
  * They change the behavior of the simulation while it is running.
  * @author Barry Becker
  */
class DynamicOptions(simulator: PredPreySimulator) extends JPanel with SliderGroupChangeListener {

  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
  setBorder(BorderFactory.createEtchedBorder)
  setPreferredSize(new Dimension(300, 300))

  private val sliderGroups: Seq[CreatureSliderGroup] =
    simulator.getCreatures.map { cp =>
      val group = new CreatureSliderGroup(cp)
      group.setSliderListener(this)
      add(group)
      group
    }

  private val fill = new JPanel
  fill.setPreferredSize(new Dimension(1, 1000))
  add(fill)

  def update(): Unit = sliderGroups.foreach(_.update())
  def reset(): Unit = sliderGroups.foreach(_.reset())

  /** One of the sliders was moved. */
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit =
    sliderGroups.foreach(_.checkSliderChanged(sliderName, value))
}
