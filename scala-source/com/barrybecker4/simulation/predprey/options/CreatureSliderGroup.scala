// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.predprey.options

import com.barrybecker4.simulation.predprey.creatures.Population
import com.barrybecker4.ui.sliders.SliderGroup
import com.barrybecker4.ui.sliders.SliderProperties

/**
  * Represents a set of sliders to associate with a creature population.
  * @author Barry Becker
  */
object CreatureSliderGroup:
  private val POPULATION_LABEL = " Population"
  private val BIRTH_RATE_LABEL = " Birth Rate"
  private val DEATH_RATE_LABEL = " Death Rate"

  private def createSliderProperties(creaturePop: Population): Array[SliderProperties] =
    val creatureName = creaturePop.getName
    Array(
      new SliderProperties(creatureName + POPULATION_LABEL, 0, 2000, creaturePop.getInitialPopulation),
      new SliderProperties(creatureName + BIRTH_RATE_LABEL, 0, creaturePop.getMaxBirthRate, creaturePop.getInitialBirthRate, 1000),
      new SliderProperties(creatureName + DEATH_RATE_LABEL, 0, creaturePop.getMaxDeathRate, creaturePop.getInitialDeathRate, 1000.0)
    )

class CreatureSliderGroup(creaturePop: Population) extends SliderGroup(CreatureSliderGroup.createSliderProperties(creaturePop)):

  def update(): Unit = setSliderValue(0, creaturePop.getPopulation)

  /**
    * One of the sliders was potentially moved.
    * Check for match based on name.
    */
  def checkSliderChanged(sliderName: String, value: Double): Unit =
    if getSliderProperties.exists(_.getName == sliderName) then
      if sliderName.endsWith(CreatureSliderGroup.POPULATION_LABEL) then creaturePop.setPopulation(value)
      else if sliderName.endsWith(CreatureSliderGroup.BIRTH_RATE_LABEL) then creaturePop.birthRate = value
      else if sliderName.endsWith(CreatureSliderGroup.DEATH_RATE_LABEL) then creaturePop.deathRate = value
      else throw IllegalStateException(s"Unexpected sliderName: $sliderName")
