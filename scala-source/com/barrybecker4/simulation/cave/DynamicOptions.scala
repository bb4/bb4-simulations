// Copyright by Barry G. Becker, 2016 - 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave

import com.barrybecker4.common.concurrency.ThreadUtil
import com.barrybecker4.simulation.cave.model.CaveProcessor
import com.barrybecker4.simulation.cave.model.CaveModel
import com.barrybecker4.ui.legend.ContinuousColorLegend
import com.barrybecker4.ui.sliders.SliderGroup
import com.barrybecker4.ui.sliders.SliderGroupChangeListener
import com.barrybecker4.ui.sliders.SliderProperties
import javax.swing._
import java.awt._
import java.awt.event._

import com.barrybecker4.common.app.AppContext


/**
  * Dynamic controls for the RD simulation that will show on the right.
  * They change the behavior of the simulation while it is running.
  *
  * @author Barry Becker
  */
object DynamicOptions {
  private val FLOOR_SLIDER = "Floor"
  private val CEILING_SLIDER = "Ceiling"
  private val LOSS_FACTOR_SLIDER = "Loss Factor"
  private val BRUSH_RADIUS_SLIDER = "Brush radius"
  private val BRUSH_STRENGTH_SLIDER = "Brush strength"
  private val EFFECT_FACTOR_SLIDER = "Effect Factor"
  private val BUMP_HEIGHT_SLIDER = "Height (for bumps)"
  private val SPECULAR_PCT_SLIDER = "Specular Highlight (for bumps)"
  private val LIGHT_SOURCE_ELEVATION_SLIDER = "Light source elevation angle (for bumps)"
  private val LIGHT_SOURCE_AZYMUTH_SLIDER = "Light azymuthal angle (for bumps)"
  private val NUM_STEPS_PER_FRAME_SLIDER = "Mum steps per frame"
  private val SCALE_SLIDER = "Scale"
  private val PI_D2 = Math.PI / 2.0
  private val PREFERRED_WIDTH = 300
  private val SPACING = 14

  private val GENERAL_SLIDER_PROPS = Array(
    new SliderProperties(FLOOR_SLIDER, 0, 1.0, CaveProcessor.DEFAULT_FLOOR_THRESH, 100),
    new SliderProperties(CEILING_SLIDER, 0, 1.0, CaveProcessor.DEFAULT_CEIL_THRESH, 100),
    new SliderProperties(LOSS_FACTOR_SLIDER, 0, 1.0, CaveProcessor.DEFAULT_LOSS_FACTOR, 100),
    new SliderProperties(EFFECT_FACTOR_SLIDER, 0, 1.0, CaveProcessor.DEFAULT_EFFECT_FACTOR, 100),
    new SliderProperties(NUM_STEPS_PER_FRAME_SLIDER, 1, 20, CaveModel.DEFAULT_NUM_STEPS_PER_FRAME),
    new SliderProperties(SCALE_SLIDER, 1, 20, CaveModel.DEFAULT_SCALE_FACTOR.toInt)
  )
  private val BUMP_SLIDER_PROPS = Array(
    new SliderProperties(BUMP_HEIGHT_SLIDER, 0.0, 10.0, CaveModel.DEFAULT_BUMP_HEIGHT, 100),
    new SliderProperties(SPECULAR_PCT_SLIDER, 0.0, 1.0, CaveModel.DEFAULT_SPECULAR_PCT, 100),
    new SliderProperties(LIGHT_SOURCE_ELEVATION_SLIDER, 0.0, Math.PI / 2.0, CaveModel.DEFAULT_LIGHT_SOURCE_ELEVATION, 100),
    new SliderProperties(LIGHT_SOURCE_AZYMUTH_SLIDER, 0.0, Math.PI, CaveModel.DEFAULT_LIGHT_SOURCE_AZYMUTH, 100)
  )
  private val BRUSH_SLIDER_PROPS = Array(
    new SliderProperties(BRUSH_RADIUS_SLIDER, 1, 50, CaveModel.DEFAULT_BRUSH_RADIUS),
    new SliderProperties(BRUSH_STRENGTH_SLIDER, 0.1, 2.0, CaveModel.DEFAULT_BRUSH_STRENGTH, 100)
  )
}

class DynamicOptions private[cave](var caveModel: CaveModel, var simulator: CaveExplorer)
  extends JPanel with SliderGroupChangeListener with ItemListener with ActionListener {

  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
  setBorder(BorderFactory.createEtchedBorder)
  setPreferredSize(new Dimension(DynamicOptions.PREFERRED_WIDTH, 900))
  val generalPanel: JPanel = createGeneralControls
  val bumpPanel: JPanel = createBumpControls
  val brushPanel: JPanel = createBrushControls
  val legend = new ContinuousColorLegend(null, caveModel.getColormap, true)

  add(createKernalDropdown)
  add(createIncrementPanel)
  add(createButtons)
  add(legend)
  add(Box.createVerticalStrut(DynamicOptions.SPACING))
  add(generalPanel)
  add(Box.createVerticalStrut(DynamicOptions.SPACING))
  add(bumpPanel)
  add(Box.createVerticalStrut(DynamicOptions.SPACING))
  add(brushPanel)

  val fill = new JPanel
  fill.setPreferredSize(new Dimension(1, 1000))
  add(fill)
  private var kernelChoice: JComboBox[String] = _
  private var nextButton: JButton = _
  private var resetButton: JButton = _
  private var generalSliderGroup: SliderGroup = _
  private var bumpSliderGroup: SliderGroup = _
  private var useContinuousIteration: JCheckBox = _
  private var useParallelComputation: JCheckBox = _

  private def createGeneralControls = {
    val panel = new JPanel(new BorderLayout)
    panel.setBorder(createTitledBorder("General parameters"))
    generalSliderGroup = new SliderGroup(DynamicOptions.GENERAL_SLIDER_PROPS)
    generalSliderGroup.addSliderChangeListener(this)
    panel.add(generalSliderGroup, BorderLayout.CENTER)
    panel
  }

  private def createBumpControls = {
    val panel = new JPanel(new BorderLayout)
    panel.setBorder(createTitledBorder("Bump parameters"))
    bumpSliderGroup = new SliderGroup(DynamicOptions.BUMP_SLIDER_PROPS)
    bumpSliderGroup.addSliderChangeListener(this)
    panel.add(bumpSliderGroup, BorderLayout.CENTER)
    panel
  }

  private def createTitledBorder(title: String) =
    BorderFactory.createCompoundBorder(
      BorderFactory.createTitledBorder(title),
      BorderFactory.createEmptyBorder(5, 5, 5, 5)
    )

  private def createBrushControls = {
    val panel = new JPanel(new BorderLayout)
    panel.setBorder(createTitledBorder("Brush Parameters (left: raise; right: lower)"))
    val brushSliderGroup: SliderGroup= new SliderGroup(DynamicOptions.BRUSH_SLIDER_PROPS)
    brushSliderGroup.addSliderChangeListener(this)
    panel.add(brushSliderGroup, BorderLayout.CENTER)
    panel
  }

  /**
    * The dropdown menu at the top for selecting a kernel type.
    * @return a dropdown/down component.
    */
  private def createKernalDropdown = {
    val kernelChoicePanel = new JPanel
    val label = new JLabel("Kernal type: ")
    kernelChoice = new JComboBox[String]
    for (kernelType <- CaveProcessor.KernelType.values) {
      kernelChoice.addItem(kernelType.toString) //name)
    }
    kernelChoice.setSelectedItem(CaveProcessor.DEFAULT_KERNEL_TYPE.id)
    kernelChoice.addItemListener(this)
    kernelChoicePanel.add(label)
    kernelChoicePanel.add(kernelChoice)
    kernelChoicePanel
  }

  private def createIncrementPanel = {
    val panel = new JPanel(new BorderLayout)
    val label = new JLabel("Continuous iteration: ")
    useContinuousIteration = new JCheckBox
    useContinuousIteration.setSelected(CaveModel.DEFAULT_USE_CONTINUOUS_ITERATION)
    useContinuousIteration.addActionListener(this)
    nextButton = new JButton("Next")
    nextButton.addActionListener(this)
    nextButton.setEnabled(!useContinuousIteration.isSelected)
    panel.add(label, BorderLayout.WEST)
    panel.add(useContinuousIteration, BorderLayout.CENTER)
    panel.add(nextButton, BorderLayout.EAST)
    panel.add(createCheckboxPanel, BorderLayout.SOUTH)
    panel
  }

  /**
    * @return checkbox options.
    */
  private def createCheckboxPanel = {
    val panel = new JPanel
    val label = new JLabel("Parallel computation: ")
    useParallelComputation = new JCheckBox
    useParallelComputation.setSelected(CaveProcessor.DEFAULT_USE_PARALLEL)
    useParallelComputation.addActionListener(this)
    panel.add(label)
    panel.add(useParallelComputation)
    panel.add(Box.createHorizontalGlue)
    panel
  }

  private def createButtons = {
    val buttonsPanel = new JPanel
    resetButton = new JButton(AppContext.getLabel("RESET"))
    resetButton.addActionListener(this)
    buttonsPanel.add(resetButton)
    buttonsPanel
  }

  def reset(): Unit = {
    generalSliderGroup.reset()
  }

  /** One of the sliders was moved. */
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit = {
    sliderName match {
      case DynamicOptions.FLOOR_SLIDER => caveModel.setFloorThresh(value)
      case DynamicOptions.CEILING_SLIDER => caveModel.setCeilThresh(value)
      case DynamicOptions.LOSS_FACTOR_SLIDER => caveModel.setLossFactor(value)
      case DynamicOptions.EFFECT_FACTOR_SLIDER => caveModel.setEffectFactor(value)
      case DynamicOptions.BUMP_HEIGHT_SLIDER =>
        caveModel.setBumpHeight(value)
        // specular highlight does not apply if no bumps
        bumpSliderGroup.setEnabled(DynamicOptions.SPECULAR_PCT_SLIDER, value > 0)
        bumpSliderGroup.setEnabled(DynamicOptions.LIGHT_SOURCE_ELEVATION_SLIDER, value > 0)
      case DynamicOptions.SPECULAR_PCT_SLIDER => caveModel.setSpecularPercent(value)
      case DynamicOptions.LIGHT_SOURCE_ELEVATION_SLIDER =>
        caveModel.setLightSourceDescensionAngle(DynamicOptions.PI_D2 - value)
      case DynamicOptions.LIGHT_SOURCE_AZYMUTH_SLIDER =>
        caveModel.setLightSourceAzymuthAngle(value)
      case DynamicOptions.NUM_STEPS_PER_FRAME_SLIDER =>
        caveModel.setNumStepsPerFrame(value.toInt)
      case DynamicOptions.SCALE_SLIDER =>
        caveModel.setScale(value)
        simulator.getInteractionHandler.setScale(value)
      case DynamicOptions.BRUSH_RADIUS_SLIDER =>
        simulator.getInteractionHandler.setBrushRadius(value.toInt)
      case DynamicOptions.BRUSH_STRENGTH_SLIDER =>
        simulator.getInteractionHandler.setBrushStrength(value)
      case _ => throw new IllegalArgumentException("Unexpected slider: " + sliderName)
    }
  }

  override def itemStateChanged(e: ItemEvent) {
    val kType = CaveProcessor.KernelType.withName(kernelChoice.getSelectedItem.toString)
    caveModel.setKernelType(kType)
  }

  override def actionPerformed(e: ActionEvent) {
    if (e.getSource == nextButton) caveModel.requestNextStep()
    else if (e.getSource == resetButton) caveModel.requestRestart()
    else if (e.getSource == useContinuousIteration) {
      val useCont = useContinuousIteration.isSelected
      caveModel.setDefaultUseContinuousIteration(useCont)
      nextButton.setEnabled(!useCont)
      if (!useCont) { // do one last step in case the rendering was interrupted.
        ThreadUtil.sleep(100)
        caveModel.requestNextStep()
      }
    }
    else if (e.getSource == useParallelComputation)
      caveModel.setUseParallelComputation(useParallelComputation.isSelected)
    else throw new IllegalStateException("Unexpected button " + e.getSource)
  }
}
