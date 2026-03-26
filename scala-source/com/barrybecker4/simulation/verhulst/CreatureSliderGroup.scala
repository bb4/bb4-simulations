package com.barrybecker4.simulation.verhulst

import com.barrybecker4.ui.sliders.SliderGroup
import com.barrybecker4.ui.sliders.SliderProperties


/**
  * Represents a set of sliders to associate with a creature population.
  * @author Barry Becker
  */
object CreatureSliderGroup {
  private val BIRTH_RATE_LABEL = "Birth Rate"

  private[verhulst] def createSliderProperties(creaturePop: Population) = {
    val creatureName = creaturePop.getName
    Array[SliderProperties](
      new SliderProperties(creatureName + BIRTH_RATE_LABEL,
        1.9, creaturePop.getMaxBirthRate, creaturePop.getInitialBirthRate, 1000.0)
    )
  }
}


class CreatureSliderGroup(creaturePop: Population)
    extends SliderGroup(CreatureSliderGroup.createSliderProperties(creaturePop)) {

  /** One of the sliders was potentially moved. Check for match based on name. */
  def checkSliderChanged(sliderName: String, value: Double): Unit = {
    for (props <- this.getSliderProperties if sliderName == props.getName) {
      if (sliderName.endsWith(CreatureSliderGroup.BIRTH_RATE_LABEL)) creaturePop.birthRate = value
    }
  }
}
