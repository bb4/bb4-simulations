// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.simulation.henonphase.algorithm.TravelerParams
import com.barrybecker4.simulation.voronoi.algorithm.VoronoiAlgorithm
import com.barrybecker4.simulation.voronoi.algorithm.model.poisson.PoissonParams
import com.barrybecker4.ui.legend.ContinuousColorLegend
import com.barrybecker4.ui.sliders.SliderGroup
import com.barrybecker4.ui.sliders.SliderGroupChangeListener
import com.barrybecker4.ui.sliders.SliderProperties

import javax.swing.*
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener


/**
  * Dynamic controls for the Voronoi that will show on the right.
  * They change the behavior of the simulation while it is running.
  * @author Barry Becker
  */
object DynamicOptions {
  private val MAX_POINTS_SLIDER = "Max num sample points (N)"
  private val RADIUS = "Min point separation (r)"
  private val K_SLIDER = "Num local samples (k)"
  private val ALPHA_SLIDER = "Alpha"
  private val STEPS_PER_FRAME_SLIDER = "Num steps per Frame"
  private val SLIDER_PROPS = Array(
    new SliderProperties(MAX_POINTS_SLIDER, 10, 100000, VoronoiAlgorithm.DEFAULT_MAX_POINTS),
    new SliderProperties(RADIUS, 3, 100, PoissonParams.DEFAULT_RADIUS, 1000.0),
    new SliderProperties(K_SLIDER, 1, 100, PoissonParams.DEFAULT_K),
    //new SliderProperties(ALPHA_SLIDER, 1, 255, 100),
    SliderProperties(STEPS_PER_FRAME_SLIDER, 1, 1000, 1)
  )
}

class DynamicOptions private[voronoi](var algorithm: VoronoiAlgorithm, var simulator: VoronoiExplorer)
  extends JPanel with ActionListener with SliderGroupChangeListener {

  private var useFixedSize: JCheckBox = _
  private var showVoronoiDiagram: JCheckBox = _
  private val sliderGroup = new SliderGroup(DynamicOptions.SLIDER_PROPS)
  private var currentParams = new PoissonParams()

  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
  setBorder(BorderFactory.createEtchedBorder)
  setPreferredSize(new Dimension(300, 300))

  sliderGroup.setSliderListener(this)
  //val legend = new ContinuousColorLegend(null, algorithm.getColorMap, true)
  val checkBoxes: JPanel = createCheckBoxes
  add(sliderGroup)
  add(Box.createVerticalStrut(10))
  add(checkBoxes)
  add(Box.createVerticalStrut(10))
  //add(legend)
  val fill = new JPanel
  fill.setPreferredSize(new Dimension(1, 1000))
  add(fill)
  add(createFormulaText)

  private def createCheckBoxes = {
    useFixedSize = new JCheckBox("Fixed Size", simulator.getUseFixedSize)
    useFixedSize.addActionListener(this)
    showVoronoiDiagram = new JCheckBox("Show Voronoi diagram", algorithm.getShowVoronoiDiagram)
    showVoronoiDiagram.addActionListener(this)

    val checkBoxes = new JPanel(new GridLayout(0, 1))
    //checkBoxes.add(useConcurrency);
    checkBoxes.add(useFixedSize)
    checkBoxes.add(showVoronoiDiagram)
    checkBoxes.setBorder(BorderFactory.createEtchedBorder)
    checkBoxes
  }

  private def createFormulaText = {
    val textPanel = new JPanel
    textPanel.setLayout(new BorderLayout)
    textPanel
  }

  def reset(): Unit = { sliderGroup.reset() }

  /** One of the buttons was pressed. */
  override def actionPerformed(e: ActionEvent): Unit = {
    if (e.getSource eq useFixedSize) simulator.setUseFixedSize(useFixedSize.isSelected)
    else if (e.getSource eq showVoronoiDiagram) algorithm.toggleShowVoronoiDiagram()
  }

  /** One of the sliders was moved. */
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit = {
    if (sliderName == DynamicOptions.RADIUS) {
      currentParams = new PoissonParams(value, currentParams.k)
      algorithm.setPoissonParams(currentParams)
    }
    else if (sliderName == DynamicOptions.K_SLIDER) {
      currentParams = new PoissonParams(currentParams.radius, value.toInt)
      algorithm.setPoissonParams(currentParams)
    }
    //else if (sliderName == DynamicOptions.ALPHA_SLIDER) algorithm.setAlpha(value.toInt)
    else if (sliderName == DynamicOptions.MAX_POINTS_SLIDER) algorithm.setNumSamplePoints(value.toInt)
    else if (sliderName == DynamicOptions.STEPS_PER_FRAME_SLIDER) algorithm.setStepsPerFrame(value.toInt)
  }
}
