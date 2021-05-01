/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion

import com.barrybecker4.simulation.reactiondiffusion.algorithm.GrayScottController
import com.barrybecker4.simulation.reactiondiffusion.algorithm.GrayScottModel
import RDDynamicOptions._
import com.barrybecker4.ui.legend.ContinuousColorLegend
import com.barrybecker4.ui.sliders.SliderGroup
import com.barrybecker4.ui.sliders.SliderGroupChangeListener
import com.barrybecker4.ui.sliders.SliderProperties
import javax.swing._
import java.awt._
import java.awt.event.{ActionEvent, ActionListener, ItemEvent, ItemListener}

import com.barrybecker4.simulation.cave.model.CaveProcessor
import com.barrybecker4.simulation.reactiondiffusion.algorithm.configuration.Initializer


/**
  * Dynamic controls for the RD simulation that will show on the right.
  * They change the behavior of the simulation while it is running.
  * @author Barry Becker
  */
object RDDynamicOptions {
  private val F_SLIDER = "Feed Rate (F)"
  private val K_SLIDER = "Decay Rate (K)"

  private val H_SLIDER = "H"
  private val BH_SLIDER = "Bump Height"
  private val SH_SLIDER = "Specular Highlight"
  private val NS_SLIDER = "Num Steps per Frame"
  private val TIMESTEP_SLIDER = "Time Step Size"
  private val BRUSH_RADIUS_SLIDER = "Brush Radius"
  private val BRUSH_STRENGTH_SLIDER = "Brush Strength"
  private val MIN_NUM_STEPS = RDSimulator.DEFAULT_STEPS_PER_FRAME / 10.0
  private val MAX_NUM_STEPS = 10.0 * RDSimulator.DEFAULT_STEPS_PER_FRAME
  private val SPACER_HT = 10

  private val SLIDER_PROPS = Array(
    new SliderProperties(F_SLIDER, 0, 0.25, GrayScottModel.F0, 10000),
    new SliderProperties(K_SLIDER, 0, 0.25, GrayScottModel.K0, 10000),
    new SliderProperties(H_SLIDER, 0.5, 4.0, GrayScottModel.H0, 10000),
    new SliderProperties(BH_SLIDER, 0, 20.0, 0.0, 10),
    new SliderProperties(SH_SLIDER, 0, 1.0, 0.0, 100),
    new SliderProperties(NS_SLIDER, MIN_NUM_STEPS, MAX_NUM_STEPS, RDSimulator.DEFAULT_STEPS_PER_FRAME, 1),
    new SliderProperties(TIMESTEP_SLIDER, 0.1, 2.0, RDSimulator.INITIAL_TIME_STEP, 100)
  )

  private val BRUSH_SLIDER_PROPS = Array(
    new SliderProperties(BRUSH_RADIUS_SLIDER, 1, 30, GrayScottModel.DEFAULT_BRUSH_RADIUS),
    new SliderProperties(BRUSH_STRENGTH_SLIDER, 0.1, 1.0, GrayScottModel.DEFAULT_BRUSH_STRENGTH, 100)
  )
}

class RDDynamicOptions private[reactiondiffusion](var gs: GrayScottController, var simulator: RDSimulator)
  extends JPanel with ActionListener with ItemListener with SliderGroupChangeListener {

  private var showU: JCheckBox = _
  private var showV: JCheckBox = _
  private var useComputeConcurrency: JCheckBox = _
  private var useRenderingConcurrency: JCheckBox = _
  private var useFixedSize: JCheckBox = _
  private var sliderGroup: SliderGroup = _
  private var initialConditionsDroplist: JComboBox[Initializer] = _

  initialize()

  private def initialize(): Unit = {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
    setBorder(BorderFactory.createEtchedBorder)
    setPreferredSize(new Dimension(300, 300))

    sliderGroup = new SliderGroup(RDDynamicOptions.SLIDER_PROPS)
    sliderGroup.addSliderChangeListener(this)

    val checkBoxes: JPanel = createCheckBoxes
    val legend: ContinuousColorLegend = new ContinuousColorLegend(null, this.simulator.getColorMap, true)

    add(sliderGroup)
    add(Box.createVerticalStrut(SPACER_HT))
    add(createBrushControls)
    add(Box.createVerticalStrut(SPACER_HT))
    add(checkBoxes)
    add(Box.createVerticalStrut(SPACER_HT))
    add(legend)
    add(Box.createVerticalStrut(SPACER_HT))
    add(createInitialConditionsDroplist())

    val fill = new JPanel
    fill.setPreferredSize(new Dimension(SPACER_HT, 1000))
    add(fill)
  }

  private def createCheckBoxes = {
    val renderingOptions = simulator.getRenderingOptions
    showU = new JCheckBox("U Value", renderingOptions.isShowingU)
    showU.addActionListener(this)
    showV = new JCheckBox("V Value", renderingOptions.isShowingV)
    showV.addActionListener(this)
    useComputeConcurrency = createCheckBox("Parallel calculation",
      "Take advantage of multiple processors for RD calculation if checked.",
      gs.isParallelized)
    useRenderingConcurrency = createCheckBox("Parallel rendering",
      "Take advantage of multiple processors for rendering if checked.",
      renderingOptions.isParallelized)
    useFixedSize = createCheckBox("Fixed Size",
      "Use just a small fixed size area for rendering rather than the whole resizable area.",
      simulator.getUseFixedSize)
    val checkBoxes = new JPanel(new GridLayout(0, 2))
    checkBoxes.add(showU)
    checkBoxes.add(showV)
    checkBoxes.add(useComputeConcurrency)
    checkBoxes.add(useRenderingConcurrency)
    checkBoxes.add(useFixedSize)
    checkBoxes.setBorder(BorderFactory.createEtchedBorder)
    checkBoxes
  }

  /**
    * The dropdown menu at the top for selecting an initializer.
    * @return initializer dropdown/down component.
    */
  private def createInitialConditionsDroplist(): JPanel = {
    val choicePanel = new JPanel()
    val label = new JLabel("Initial conditions: ")
    initialConditionsDroplist = new JComboBox[Initializer]
    for (initializer <- Initializer.VALUES) {
      initialConditionsDroplist.addItem(initializer)
    }
    initialConditionsDroplist.setSelectedItem(Initializer.DEFAULT_INITIALIZER)
    initialConditionsDroplist.addItemListener(this)
    choicePanel.add(label)
    choicePanel.add(initialConditionsDroplist)
    choicePanel
  }

  private def createBrushControls = {
    val panel = new JPanel(new BorderLayout)
    panel.setBorder(createTitledBorder("Brush Parameters (left: add U; right: add V)"))
    val brushSliderGroup = new SliderGroup(BRUSH_SLIDER_PROPS)
    brushSliderGroup.addSliderChangeListener(this)
    panel.add(brushSliderGroup, BorderLayout.CENTER)
    panel
  }

  private def createTitledBorder(title: String) =
    BorderFactory.createCompoundBorder(
      BorderFactory.createTitledBorder(title),
      BorderFactory.createEmptyBorder(5, 5, 5, 5)
    )

  private def createCheckBox(label: String, tooltip: String, initiallyChecked: Boolean) = {
    val cb = new JCheckBox(label, initiallyChecked)
    cb.setToolTipText(tooltip)
    cb.addActionListener(this)
    cb
  }

  def reset(): Unit = {
    sliderGroup.reset()
  }

  override def itemStateChanged(e: ItemEvent): Unit = {
    gs.setInitializer(initialConditionsDroplist.getSelectedItem.asInstanceOf[Initializer])
  }

  /** One of the buttons was pressed. */
  override def actionPerformed(e: ActionEvent): Unit = {
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
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit = {
    sliderName match {
      case F_SLIDER => gs.getModel.setF(value)
      case K_SLIDER => gs.getModel.setK(value)
      case H_SLIDER => gs.setH(value)
      case BH_SLIDER =>
        simulator.getRenderingOptions.setHeightScale(value)
        sliderGroup.setEnabled(SH_SLIDER, value > 0)
      case SH_SLIDER => simulator.getRenderingOptions.setSpecular(value)
      case NS_SLIDER => simulator.setNumStepsPerFrame(value.toInt)
      case TIMESTEP_SLIDER => simulator.setTimeStep(value)
      case BRUSH_RADIUS_SLIDER => simulator.getInteractionHandler.setBrushRadius(value.toInt)
      case BRUSH_STRENGTH_SLIDER => simulator.getInteractionHandler.setBrushStrength(value)
    }
  }
}
