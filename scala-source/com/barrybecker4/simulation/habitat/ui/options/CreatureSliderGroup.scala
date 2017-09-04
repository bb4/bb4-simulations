// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.ui.options

import com.barrybecker4.simulation.habitat.creatures.Population
import com.barrybecker4.ui.sliders.SliderGroup
import com.barrybecker4.ui.sliders.SliderProperties


/**
  * Represents a set of sliders to associate with a creature population.
  * @author Barry Becker
  */
object CreatureSliderGroup {
  private val SIZE_LABEL = " size"
  private val GESTATION_LABEL = " gestation period"
  private val STARVATION_LABEL = " starvation time"
  private val NUTRITION_LABEL = " nutritional value"
  private val NORM_SPEED_LABEL = " normal speed"
  private val MAX_SPEED_LABEL = " top speed"
  private val MIN_FACTOR = 0.2
  private val MAX_FACTOR = 6
}

class CreatureSliderGroup(var creaturePop: Population) extends SliderGroup {
  commonInit(createSliderProperties)

  private def createSliderProperties = {
    val ctype = creaturePop.creatureType
    val normSpeed = ctype.normalSpeed
    val props = if (normSpeed == 0) new Array[SliderProperties](4)
    else new Array[SliderProperties](6)
    val creatureName = ctype.name
    setBackground(ctype.color)
    val size = ctype.size
    props(0) = new SliderProperties(creatureName + CreatureSliderGroup.SIZE_LABEL,
      CreatureSliderGroup.MIN_FACTOR * size, CreatureSliderGroup.MAX_FACTOR * size, size, 200)
    val gestation = ctype.gestationPeriod
    props(1) = new SliderProperties(creatureName + CreatureSliderGroup.GESTATION_LABEL, 1,
      (CreatureSliderGroup.MAX_FACTOR * gestation).toInt, gestation)
    val starveTime = ctype.starvationThreshold
    props(2) = new SliderProperties(creatureName + CreatureSliderGroup.STARVATION_LABEL,
      (CreatureSliderGroup.MIN_FACTOR * starveTime).toInt, CreatureSliderGroup.MAX_FACTOR * starveTime, starveTime)
    val nutrition = ctype.nutritionalValue
    props(3) = new SliderProperties(creatureName + CreatureSliderGroup.NUTRITION_LABEL, 1,
      (CreatureSliderGroup.MAX_FACTOR * nutrition).toInt, nutrition)
    if (normSpeed > 0) {
      props(4) = new SliderProperties(creatureName + CreatureSliderGroup.NORM_SPEED_LABEL, 0,
        CreatureSliderGroup.MAX_FACTOR * normSpeed, normSpeed, 1000.0)
      val maxSpeed = ctype.maxSpeed
      props(5) = new SliderProperties(creatureName + CreatureSliderGroup.MAX_SPEED_LABEL, 0,
        CreatureSliderGroup.MAX_FACTOR * maxSpeed, maxSpeed, 1000.0)
    }
    props
  }

  /**
    * One of the sliders was potentially moved.
    * Check for match based on name.
    */
  def checkSliderChanged(sliderName: String, value: Double): Unit = {
    val creatureType = creaturePop.creatureType
    for (props <- this.getSliderProperties) {
      if (sliderName == props.getName) if (sliderName.endsWith(CreatureSliderGroup.SIZE_LABEL))
        creatureType.size = value
      else if (sliderName.endsWith(CreatureSliderGroup.GESTATION_LABEL))
        creatureType.gestationPeriod = value.toInt
      else if (sliderName.endsWith(CreatureSliderGroup.STARVATION_LABEL))
        creatureType.starvationThreshold = value.toInt
      else if (sliderName.endsWith(CreatureSliderGroup.NUTRITION_LABEL))
        creatureType.nutritionalValue = value.toInt
      else if (sliderName.endsWith(CreatureSliderGroup.NORM_SPEED_LABEL))
        creatureType.normalSpeed = value
      else if (sliderName.endsWith(CreatureSliderGroup.MAX_SPEED_LABEL))
        creatureType.maxSpeed = value
      else throw new IllegalStateException("Unexpected sliderName:" + sliderName)
    }
  }
}

