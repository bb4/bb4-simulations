/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion

import com.barrybecker4.simulation.reactiondiffusion.algorithm.GrayScottController
import com.barrybecker4.simulation.reactiondiffusion.algorithm.GrayScottModel
import com.barrybecker4.simulation.reactiondiffusion.rendering.RDRenderingOptions
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
  *
  * @author Barry Becker
  */
object RDDynamicOptions {
  private val K_SLIDER = "K"
  private val F_SLIDER = "F"
  private val H_SLIDER = "H"
  private val BH_SLIDER = "Bump Height"
  private val SH_SLIDER = "Specular Highlight"
  private val NS_SLIDER = "Num Steps per Frame"
  private val TIMESTEP_SLIDER = "Time Step Size"
  private val MIN_NUM_STEPS = RDSimulator.DEFAULT_STEPS_PER_FRAME / 10.0
  private val MAX_NUM_STEPS = 10.0 * RDSimulator.DEFAULT_STEPS_PER_FRAME
  private val SLIDER_PROPS = Array(
    new SliderProperties(K_SLIDER, 0, 0.3, GrayScottModel.K0, 1000),
    new SliderProperties(F_SLIDER, 0, 0.3, GrayScottModel.F0, 1000),
    new SliderProperties(H_SLIDER, 0.008, 0.05, GrayScottController.H0, 10000),
    new SliderProperties(BH_SLIDER, 0, 20.0, 0.0, 10),
    new SliderProperties(SH_SLIDER, 0, 1.0, 0.0, 100),
    new SliderProperties(NS_SLIDER, MIN_NUM_STEPS, MAX_NUM_STEPS, RDSimulator.DEFAULT_STEPS_PER_FRAME, 1),
    new SliderProperties(TIMESTEP_SLIDER, 0.1, 2.0, RDSimulator.INITIAL_TIME_STEP, 100)
  )
}

class RDDynamicOptions private[reactiondiffusion](var gs: GrayScottController, var simulator: RDSimulator)
  extends JPanel with ActionListener with SliderGroupChangeListener {
  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
  setBorder(BorderFactory.createEtchedBorder)
  setPreferredSize(new Dimension(300, 300))
  private var sliderGroup  = new SliderGroup(RDDynamicOptions.SLIDER_PROPS)
  sliderGroup.addSliderChangeListener(this)
  val checkBoxes: JPanel = createCheckBoxes
  val legend: ContinuousColorLegend = new ContinuousColorLegend(null, this.simulator.getColorMap, true)
  add(sliderGroup)
  add(Box.createVerticalStrut(10))
  add(checkBoxes)
  add(Box.createVerticalStrut(10))
  add(legend)
  val fill = new JPanel
  fill.setPreferredSize(new Dimension(10, 1000))
  add(fill)
  private var showU: JCheckBox = _
  private var showV: JCheckBox = _
  private var useComputeConcurrency: JCheckBox = _
  private var useRenderingConcurrency: JCheckBox = _
  private var useFixedSize: JCheckBox = _

  private def createCheckBoxes = {
    val renderingOptions = simulator.getRenderingOptions
    showU = new JCheckBox("U Value", renderingOptions.isShowingU)
    showU.addActionListener(this)
    showV = new JCheckBox("V Value", renderingOptions.isShowingV)
    showV.addActionListener(this)
    useComputeConcurrency = createCheckBox("Parallel calculation", "Take advantage of multiple processors for RD calculation if checked.", gs.isParallelized)
    useRenderingConcurrency = createCheckBox("Parallel rendering", "Take advantage of multiple processors for rendering if checked.", renderingOptions.isParallelized)
    useFixedSize = createCheckBox("Fixed Size", "Use just a small fixed size area for rendering rather than the whole resizable area.", simulator.getUseFixedSize)
    val checkBoxes = new JPanel(new GridLayout(0, 2))
    checkBoxes.add(showU)
    checkBoxes.add(showV)
    checkBoxes.add(useComputeConcurrency)
    checkBoxes.add(useRenderingConcurrency)
    checkBoxes.add(useFixedSize)
    checkBoxes.setBorder(BorderFactory.createEtchedBorder)
    checkBoxes
  }

  private def createCheckBox(label: String, tooltip: String, initiallyChecked: Boolean) = {
    val cb = new JCheckBox(label, initiallyChecked)
    cb.setToolTipText(tooltip)
    cb.addActionListener(this)
    cb
  }

  def reset() { sliderGroup.reset() }

  /** One of the buttons was pressed. */
  override def actionPerformed(e: ActionEvent) {
    val renderingOptions = simulator.getRenderingOptions
    if (e.getSource eq showU) renderingOptions.setShowingU(!renderingOptions.isShowingU)
    else if (e.getSource eq showV) {
      renderingOptions.setShowingV(!renderingOptions.isShowingV)
      repaint()
    }
    else if (e.getSource eq useComputeConcurrency) {
      val isParallelized = !gs.isParallelized
      gs.setParallelized(isParallelized)
    }
    else if (e.getSource eq useRenderingConcurrency) {
      val isParallelized = !renderingOptions.isParallelized
      renderingOptions.setParallelized(isParallelized)
    }
    else if (e.getSource eq useFixedSize) simulator.setUseFixedSize(useFixedSize.isSelected)
  }

  /** One of the sliders was moved. */
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double) {
    sliderName match {
      case RDDynamicOptions.F_SLIDER => gs.getModel.setF(value)
      case RDDynamicOptions.K_SLIDER => gs.getModel.setK(value)
      case RDDynamicOptions.H_SLIDER => gs.setH(value)
      case RDDynamicOptions.BH_SLIDER =>
        simulator.getRenderingOptions.setHeightScale(value)
        sliderGroup.setEnabled(RDDynamicOptions.SH_SLIDER, value > 0)
      case RDDynamicOptions.SH_SLIDER => simulator.getRenderingOptions.setSpecular(value)
      case RDDynamicOptions.NS_SLIDER => simulator.setNumStepsPerFrame(value.toInt)
      case RDDynamicOptions.TIMESTEP_SLIDER => simulator.setTimeStep(value)
    }
  }
}
