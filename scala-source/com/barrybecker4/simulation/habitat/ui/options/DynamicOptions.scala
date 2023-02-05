// Copyright by Barry G. Becker, 2016-2023. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.ui.options

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

  setLayout(new BorderLayout())
  setPreferredSize(new Dimension(300, 300))
  private val sliderGroups = ArrayBuffer[CreatureSliderGroup]()

  val tabbedPane = new JTabbedPane()

  for (creaturePop <- simulator.getPopulations) {
    val group = new CreatureSliderGroup(creaturePop)
    group.setSliderListener(this)
    sliderGroups.append(group)

    val groupPanel = new JPanel(new BorderLayout())
    groupPanel.add(group, BorderLayout.NORTH);
    
    val creatureType = creaturePop.creatureType
    tabbedPane.add(s"<html>${creatureType.name} <font color='$color'>&${hexColor(creatureType.color)};</font></html>", groupPanel)
  }

  add(tabbedPane, BorderLayout.CENTER)


  def update(): Unit = {}
  def reset(): Unit = { for (group <- sliderGroups) group.reset() }

  private def hexColor(color: Color): String =
    "#" + Integer.toHexString(color.getRGB).substring(2)

  /** One of the sliders was moved. */
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit = {
    for (group <- sliderGroups) {
      group.checkSliderChanged(sliderName, value)
    }
  }
}
