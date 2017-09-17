// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.predprey.options

import com.barrybecker4.simulation.predprey.creatures.Population
import com.barrybecker4.ui.sliders.SliderGroup
import com.barrybecker4.ui.sliders.SliderProperties


/**
  * Represents a set of sliders to associate with a creature population.
  * @author Barry Becker
  */
object CreatureSliderGroup {
  private val POPULATION_LABEL = " Population"
  private val BIRTH_RATE_LABEL = " Birth Rate"
  private val DEATH_RATE_LABEL = " Death Rate"
}

class CreatureSliderGroup(var creaturePop: Population) extends SliderGroup {
  commonInit(createSliderProperties)

  private def createSliderProperties = {
    val props = new Array[SliderProperties](3)
    val creatureName = creaturePop.getName
    props(0) = new SliderProperties(creatureName + CreatureSliderGroup.POPULATION_LABEL,
      0, 2000, creaturePop.getInitialPopulation)
    props(1) = new SliderProperties(creatureName + CreatureSliderGroup.BIRTH_RATE_LABEL,
      0, creaturePop.getMaxBirthRate, creaturePop.getInitialBirthRate, 1000)
    props(2) = new SliderProperties(creatureName + CreatureSliderGroup.DEATH_RATE_LABEL,
      0, creaturePop.getMaxDeathRate, creaturePop.getInitialDeathRate, 1000.0)
    props
  }

  def update() { this.setSliderValue(0, creaturePop.getPopulation) }

  /**
    * One of the sliders was potentially moved.
    * Check for match based on name.
    */
  def checkSliderChanged(sliderName: String, value: Double): Unit = {
    for (props <- this.getSliderProperties) {
      if (sliderName == props.getName) {
        if (sliderName.endsWith(CreatureSliderGroup.POPULATION_LABEL)) creaturePop.setPopulation(value)
        else if (sliderName.endsWith(CreatureSliderGroup.BIRTH_RATE_LABEL)) creaturePop.birthRate = value
        else if (sliderName.endsWith(CreatureSliderGroup.DEATH_RATE_LABEL)) creaturePop.deathRate = value
        else throw new IllegalStateException("Unexpected sliderName:" + sliderName)
      }
    }
  }
}