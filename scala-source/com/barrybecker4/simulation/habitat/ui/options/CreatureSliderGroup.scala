// Copyright by Barry G. Becker, 2016-2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.habitat.ui.options

import com.barrybecker4.ui.sliders.{SliderGroup, SliderProperties}
import CreatureSliderGroup.*
import com.barrybecker4.simulation.habitat.creatures.CreatureType
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

  private def baseSliderProperties(creatureName: String, ctype: CreatureType): Array[SliderProperties] = {
    val props = new Array[SliderProperties](6)
    val size = ctype.size
    props(0) = new SliderProperties(creatureName + SIZE_LABEL,
      MIN_FACTOR * size, MAX_FACTOR * size, size, 200)

    val gestation = ctype.gestationPeriod
    props(1) = new SliderProperties(creatureName + GESTATION_LABEL, 1,
      MAX_FACTOR * gestation, gestation)

    val starveTime = ctype.starvationThreshold
    props(2) = new SliderProperties(creatureName + STARVATION_LABEL,
      (MIN_FACTOR * starveTime).toInt, MAX_FACTOR * starveTime, starveTime)

    val maxAge = ctype.maxAge
    props(3) = new SliderProperties(creatureName + MAX_AGE_LABEL,
      (MIN_FACTOR * maxAge).toInt, MAX_FACTOR * maxAge, maxAge)

    val nutrition = ctype.nutritionalValue
    props(4) = new SliderProperties(creatureName + NUTRITION_LABEL, 1,
      MAX_FACTOR * nutrition, nutrition)

    val spawnRate = ctype.spawnRate
    props(5) = new SliderProperties(creatureName + SPAWN_RATE_LABEL, -10,
      10, spawnRate)
    props
  }

  private def speedSliderProperties(creatureName: String, ctype: CreatureType): Array[SliderProperties] = {
    val props = new Array[SliderProperties](2)
    val normSpeed = ctype.normalSpeed
    props(0) = new SliderProperties(creatureName + NORM_SPEED_LABEL, 0,
      MAX_FACTOR * normSpeed, normSpeed, 1000.0)
    val maxSpeed = ctype.maxSpeed
    props(1) = new SliderProperties(creatureName + MAX_SPEED_LABEL, 0,
      MAX_FACTOR * maxSpeed, maxSpeed, 1000.0)
    props
  }

  private def createSliderProperties(creaturePop: Population): Array[SliderProperties] = {
    val ctype = creaturePop.creatureType
    val creatureName = ctype.name
    val base = baseSliderProperties(creatureName, ctype)
    if (ctype.normalSpeed == 0) base
    else
      Array.concat(base, speedSliderProperties(creatureName, ctype))
  }
}

class CreatureSliderGroup(creaturePop: Population) extends SliderGroup(createSliderProperties(creaturePop)) {

  /**
    * One of the sliders was potentially moved.
    * Check for match based on name.
    */
  def checkSliderChanged(sliderName: String, value: Double): Unit = {
    val creatureType = creaturePop.creatureType
    for (props <- this.getSliderProperties if sliderName == props.getName) {
      if (sliderName.endsWith(SIZE_LABEL)) {
        creatureType.size = value
      } else if (sliderName.endsWith(GESTATION_LABEL)) {
        creatureType.gestationPeriod = value.toInt
      } else if (sliderName.endsWith(STARVATION_LABEL)) {
        creatureType.starvationThreshold = value.toInt
      } else if (sliderName.endsWith(MAX_AGE_LABEL)) {
        creatureType.maxAge = value.toInt
      } else if (sliderName.endsWith(NUTRITION_LABEL)) {
        creatureType.nutritionalValue = value.toInt
      } else if (sliderName.endsWith(NORM_SPEED_LABEL)) {
        creatureType.normalSpeed = value
      } else if (sliderName.endsWith(MAX_SPEED_LABEL)) {
        creatureType.maxSpeed = value
      } else if (sliderName.endsWith(SPAWN_RATE_LABEL)) {
        creatureType.spawnRate = value.toInt
      } else {
        throw new IllegalStateException("Unexpected sliderName:" + sliderName)
      }
    }
  }
}
