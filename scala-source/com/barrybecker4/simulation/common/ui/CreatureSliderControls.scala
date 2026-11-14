// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common.ui

import com.barrybecker4.ui.sliders.SliderGroup


/**
  * Slider groups used by population DynamicOptions panels.
  * `reset` and `setSliderListener` come from [[SliderGroup]].
  */
trait CreatureSliderControls { self: SliderGroup =>
  def checkSliderChanged(sliderName: String, value: Double): Unit
  def update(): Unit = ()
}
