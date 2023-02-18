// Copyright by Barry G. Becker, 2016-2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.ui.options

import com.barrybecker4.ui.sliders.SliderGroup
import com.barrybecker4.ui.sliders.SliderProperties
import CreatureSliderGroup._
import com.barrybecker4.simulation.habitat.creatures.populations.Population


/**
  * Represents a set of sliders to associate with a creature population.
 *
  * @author Barry Becker
  */
object CreatureSliderGroup {
  private val SIZE_LABEL = " size"
  private val GESTATION_LABEL = " gestation period"
  private val STARVATION_LABEL = " starvation time"
  private val MAX_AGE_LABEL = " max age"
  private val NUTRITION_LABEL = " nutritional value"
  private val NORM_SPEED_LABEL = " normal speed"
  private val MAX_SPEED_LABEL = " top speed"
  private val SPAWN_RATE_LABEL = " spawn rate"
  private val MIN_FACTOR = 0.2
  private val MAX_FACTOR = 6

  private final def createSliderProperties(creaturePop: Population): Array[SliderProperties] = {
    val ctype = creaturePop.creatureType
    val normSpeed = ctype.normalSpeed
    val props = if (normSpeed == 0) new Array[SliderProperties](6)
    else new Array[SliderProperties](8)
    val creatureName = ctype.name

    val size = ctype.size
    props(0) = new SliderProperties(creatureName + CreatureSliderGroup.SIZE_LABEL,
      CreatureSliderGroup.MIN_FACTOR * size, CreatureSliderGroup.MAX_FACTOR * size, size, 200)

    val gestation = ctype.gestationPeriod
    props(1) = new SliderProperties(creatureName + CreatureSliderGroup.GESTATION_LABEL, 1,
      CreatureSliderGroup.MAX_FACTOR * gestation, gestation)

    val starveTime = ctype.starvationThreshold
    props(2) = new SliderProperties(creatureName + CreatureSliderGroup.STARVATION_LABEL,
      (CreatureSliderGroup.MIN_FACTOR * starveTime).toInt, CreatureSliderGroup.MAX_FACTOR * starveTime, starveTime)

    val maxAge = ctype.maxAge
    props(3) = new SliderProperties(creatureName + CreatureSliderGroup.MAX_AGE_LABEL,
      (CreatureSliderGroup.MIN_FACTOR * maxAge).toInt, CreatureSliderGroup.MAX_FACTOR * maxAge, maxAge)

    val nutrition = ctype.nutritionalValue
    props(4) = new SliderProperties(creatureName + CreatureSliderGroup.NUTRITION_LABEL, 1,
      CreatureSliderGroup.MAX_FACTOR * nutrition, nutrition)

    val spawnRate = ctype.spawnRate
    props(5) = new SliderProperties(creatureName + CreatureSliderGroup.SPAWN_RATE_LABEL, -10,
      10, spawnRate)

    if (normSpeed > 0) {
      props(6) = new SliderProperties(creatureName + CreatureSliderGroup.NORM_SPEED_LABEL, 0,
        CreatureSliderGroup.MAX_FACTOR * normSpeed, normSpeed, 1000.0)
      val maxSpeed = ctype.maxSpeed
      props(7) = new SliderProperties(creatureName + CreatureSliderGroup.MAX_SPEED_LABEL, 0,
        CreatureSliderGroup.MAX_FACTOR * maxSpeed, maxSpeed, 1000.0)
    }

    props
  }
}

class CreatureSliderGroup(creaturePop: Population) extends SliderGroup(createSliderProperties(creaturePop)) {

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
      else if (sliderName.endsWith(CreatureSliderGroup.SPAWN_RATE_LABEL))
        creatureType.spawnRate = value.toInt
      else throw new IllegalStateException("Unexpected sliderName:" + sliderName)
    }
  }
}

