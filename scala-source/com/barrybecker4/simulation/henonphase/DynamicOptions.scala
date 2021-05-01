// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.henonphase

import com.barrybecker4.common.format.FormatUtil
import com.barrybecker4.simulation.henonphase.algorithm.HenonAlgorithm
import com.barrybecker4.simulation.henonphase.algorithm.TravelerParams
import com.barrybecker4.ui.legend.ContinuousColorLegend
import com.barrybecker4.ui.sliders.SliderGroup
import com.barrybecker4.ui.sliders.SliderGroupChangeListener
import com.barrybecker4.ui.sliders.SliderProperties
import javax.swing._
import java.awt._
import java.awt.event.ActionEvent
import java.awt.event.ActionListener


/**
  * Dynamic controls for the RD simulation that will show on the right.
  * They change the behavior of the simulation while it is running.
  * @author Barry Becker
  */
object DynamicOptions {
  private val PHASE_ANGLE_SLIDER = "Phase Angle"
  private val MULTIPLIER_SLIDER = "Multiplier"
  private val OFFSET_SLIDER = "Offset"
  private val ALPHA_SLIDER = "Alpha"
  private val NUM_TRAVELORS_SLIDER = "Num Travelor Particles"
  private val ITER_PER_FRAME_SLIDER = "Num Iterations per Frame"
  private val ITER_SLIDER = "Max Iterations"
  private val SLIDER_PROPS = Array(
    new SliderProperties(PHASE_ANGLE_SLIDER, 0, 2.0 * Math.PI, TravelerParams.DEFAULT_PHASE_ANGLE, 1000.0),
    new SliderProperties(MULTIPLIER_SLIDER, 0.9, 1.1, TravelerParams.DEFAULT_MULTIPLIER, 1000.0),
    new SliderProperties(OFFSET_SLIDER, -0.2, 0.2, TravelerParams.DEFAULT_OFFSET, 1000.0),
    new SliderProperties(ALPHA_SLIDER, 1, 255, 100),
    new SliderProperties(NUM_TRAVELORS_SLIDER, 1, 10000, HenonAlgorithm.DEFAULT_NUM_TRAVELERS),
    new SliderProperties(ITER_PER_FRAME_SLIDER, 1,
      HenonAlgorithm.DEFAULT_MAX_ITERATIONS / 10, HenonAlgorithm.DEFAULT_FRAME_ITERATIONS),
    new SliderProperties(ITER_SLIDER, 100, 100000, HenonAlgorithm.DEFAULT_MAX_ITERATIONS))
}

class DynamicOptions private[henonphase](var algorithm: HenonAlgorithm, var simulator: HenonPhaseExplorer)
  extends JPanel with ActionListener with SliderGroupChangeListener {

  private var useFixedSize: JCheckBox = _
  private var useUniformSeeds: JCheckBox = _
  private var connectPoints: JCheckBox = _
  private var sliderGroup = new SliderGroup(DynamicOptions.SLIDER_PROPS)
  private var formulaText: JTextArea = _
  private var currentParams = new TravelerParams

  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
  setBorder(BorderFactory.createEtchedBorder)
  setPreferredSize(new Dimension(300, 300))

  sliderGroup.addSliderChangeListener(this)
  val legend = new ContinuousColorLegend(null, this.algorithm.getColorMap, true)
  val checkBoxes: JPanel = createCheckBoxes
  add(sliderGroup)
  add(Box.createVerticalStrut(10))
  add(checkBoxes)
  add(Box.createVerticalStrut(10))
  add(legend)
  val fill = new JPanel
  fill.setPreferredSize(new Dimension(1, 1000))
  add(fill)
  add(createFormulaText)


  private def createCheckBoxes = {
    useFixedSize = new JCheckBox("Fixed Size", simulator.getUseFixedSize)
    useFixedSize.addActionListener(this)
    useUniformSeeds = new JCheckBox("Uniform seeds", algorithm.getUseUniformSeeds)
    useUniformSeeds.addActionListener(this)
    connectPoints = new JCheckBox("Connect points", algorithm.getConnectPoints)
    connectPoints.addActionListener(this)
    val checkBoxes = new JPanel(new GridLayout(0, 1))
    //checkBoxes.add(useConcurrency);
    checkBoxes.add(useFixedSize)
    checkBoxes.add(useUniformSeeds)
    checkBoxes.add(connectPoints)
    checkBoxes.setBorder(BorderFactory.createEtchedBorder)
    checkBoxes
  }

  private def createFormulaText = {
    val textPanel = new JPanel
    textPanel.setLayout(new BorderLayout)
    formulaText = new JTextArea
    formulaText.setEditable(false)
    formulaText.setBackground(getBackground)
    updateFormulaText()
    textPanel.add(formulaText, BorderLayout.CENTER)
    textPanel
  }

  private def updateFormulaText(): Unit = {
    val text = new StringBuilder
    text.append("term = ")
    if (currentParams.isDefaultMultiplier)
      text.append(FormatUtil.formatNumber(currentParams.multiplier)).append(" * ")
    text.append("y")
    if (currentParams.isDefaultOffset)
      text.append(" + ").append(FormatUtil.formatNumber(currentParams.offset))
    text.append(" - x * x")
    text.append("\n")
    val angle = FormatUtil.formatNumber(currentParams.angle)
    text.append("x' = x * cos(").append(angle).append(") - term * sin(").append(angle).append(")\n")
    text.append("y' = x * sin(").append(angle).append(") + term * cos(").append(angle).append(")")
    formulaText.setText(text.toString)
  }

  def reset(): Unit = { sliderGroup.reset() }

  /** One of the buttons was pressed. */
  override def actionPerformed(e: ActionEvent): Unit = {
    if (e.getSource eq useFixedSize) simulator.setUseFixedSize(useFixedSize.isSelected)
    else if (e.getSource eq useUniformSeeds) algorithm.toggleUseUniformSeeds()
    else if (e.getSource eq connectPoints) algorithm.toggleConnectPoints()
  }

  /** One of the sliders was moved. */
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit = {
    if (sliderName == DynamicOptions.PHASE_ANGLE_SLIDER) {
      currentParams = new TravelerParams(value, currentParams.multiplier, currentParams.offset)
      algorithm.setTravelerParams(currentParams)
      updateFormulaText()
    }
    else if (sliderName == DynamicOptions.MULTIPLIER_SLIDER) {
      currentParams = new TravelerParams(currentParams.angle, value, currentParams.offset)
      algorithm.setTravelerParams(currentParams)
      updateFormulaText()
    }
    else if (sliderName == DynamicOptions.OFFSET_SLIDER) {
      currentParams = new TravelerParams(currentParams.angle, currentParams.multiplier, value)
      algorithm.setTravelerParams(currentParams)
      updateFormulaText()
    }
    else if (sliderName == DynamicOptions.ALPHA_SLIDER) algorithm.setAlpha(value.toInt)
    else if (sliderName == DynamicOptions.NUM_TRAVELORS_SLIDER) algorithm.setNumTravelors(value.toInt)
    else if (sliderName == DynamicOptions.ITER_PER_FRAME_SLIDER) algorithm.setStepsPerFrame(value.toInt)
    else if (sliderName == DynamicOptions.ITER_SLIDER) algorithm.setMaxIterations(value.toInt)
  }
}
