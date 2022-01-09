// Copyright by Barry G. Becker, 2021 - 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.ui

import com.barrybecker4.simulation.dungeon.ui.ParameterSliders.GENERAL_SLIDER_PROPS
import com.barrybecker4.ui.sliders.{SliderGroup, SliderGroupChangeListener, SliderProperties}
import ParameterSliders.*
import com.barrybecker4.simulation.dungeon.model.options.{DungeonOptions, RoomOptions}


object ParameterSliders {
  private val MAX_ROOM_WIDTH_SLIDER = "Max Width"
  private val MAX_ROOM_HEIGHT_SLIDER = "Max Height"
  private val PERCENT_FILLED_SLIDER = "Percent Filled"
  private val CONNECTIVITY_SLIDER = "Room connectivity"
  private val ROOM_PADDING_SLIDER = "Wall Border Thickness"
  private val CELL_SIZE_SLIDER = "Cell Size"
  private val RANDOM_SKEW_SLIDER = "Random Gaussian Skew"
  private val RANDOM_BIAS_SLIDER = "Random Gaussian Bias"

  private val minDim = 2 * (RoomOptions.MIN_ROOM_DIM + 2 * RoomOptions.DEFAULT_ROOM_PADDING)
  private val GENERAL_SLIDER_PROPS = Array(
    new SliderProperties(MAX_ROOM_WIDTH_SLIDER, minDim, 60, RoomOptions.DEFAULT_MAX_ROOM_WIDTH, 1),
    new SliderProperties(MAX_ROOM_HEIGHT_SLIDER, minDim, 60, RoomOptions.DEFAULT_MAX_ROOM_HEIGHT, 1),
    new SliderProperties(PERCENT_FILLED_SLIDER, 10, 100, RoomOptions.DEFAULT_PERCENT_FILLED, 1),
    new SliderProperties(CONNECTIVITY_SLIDER, 0.1, 1.0, DungeonOptions.DEFAULT_CONNECTIVITY, 100),
    new SliderProperties(ROOM_PADDING_SLIDER, 0, 4, RoomOptions.DEFAULT_ROOM_PADDING, 1),
    new SliderProperties(CELL_SIZE_SLIDER, 1, 30, DungeonOptions.DEFAULT_CELL_SIZE, 1),
    new SliderProperties(RANDOM_SKEW_SLIDER, 0.0, 2.0, RoomOptions.DEFAULT_RANDOM_SKEW, 100),
    new SliderProperties(RANDOM_BIAS_SLIDER, 0, 2.0, RoomOptions.DEFAULT_RANDOM_BIAS + 1.0, 100),
  )
  private val MAX_ROOM_WIDTH_SLIDER_IDX = 0
  private val MAX_ROOM_HEIGHT_SLIDER_IDX = 1
}

class ParameterSliders extends SliderGroup(GENERAL_SLIDER_PROPS) {

  def onSliderChanged(sliderIndex: Int, sliderName: String, value: Double,
                      oldOptions: DungeonOptions, listener: SliderGroupChangeListener): DungeonOptions = {
    sliderName match {
      case ParameterSliders.MAX_ROOM_WIDTH_SLIDER => oldOptions.setMaxRoomWidth(value.toInt)
      case ParameterSliders.MAX_ROOM_HEIGHT_SLIDER => oldOptions.setMaxRoomHeight(value.toInt)
      case ParameterSliders.PERCENT_FILLED_SLIDER => oldOptions.setPercentFilled(value.toInt)
      case ParameterSliders.CONNECTIVITY_SLIDER => oldOptions.setConnectivity(value.toFloat)
      case ParameterSliders.ROOM_PADDING_SLIDER => updateOnPaddingChange(value.toInt, oldOptions)
      case ParameterSliders.CELL_SIZE_SLIDER => oldOptions.setCellSize(value.toInt)
      case ParameterSliders.RANDOM_SKEW_SLIDER => oldOptions.setRandomSkew(value)
      case ParameterSliders.RANDOM_BIAS_SLIDER => oldOptions.setRandomBias(value - 1.0)
      case _ => throw new IllegalArgumentException("Unexpected slider: " + sliderName)
    }
  }

  private def updateOnPaddingChange(paddingValue: Int, oldOptions: DungeonOptions): DungeonOptions = {
    val minValue = 2 * (RoomOptions.MIN_ROOM_DIM + 2 * paddingValue)
    setSliderMinimum(MAX_ROOM_WIDTH_SLIDER_IDX, minValue)
    setSliderMinimum(MAX_ROOM_HEIGHT_SLIDER_IDX, minValue)
    var newOptions = oldOptions
    
    val listener: SliderGroupChangeListener = this.getSliderListener().get
    removeSliderListener()

    if (minValue > getSliderValueAsInt(MAX_ROOM_WIDTH_SLIDER_IDX)) {
      newOptions = oldOptions.setMaxRoomWidth(minValue)
      setSliderValue(MAX_ROOM_WIDTH_SLIDER_IDX, minValue)
    }
    if (minValue > getSliderValueAsInt(MAX_ROOM_HEIGHT_SLIDER_IDX)) {
      newOptions = oldOptions.setMaxRoomHeight(minValue)
      setSliderValue(MAX_ROOM_HEIGHT_SLIDER_IDX, minValue)
    }
    setSliderListener(listener)
    newOptions.setRoomPadding(paddingValue)
  }

}
