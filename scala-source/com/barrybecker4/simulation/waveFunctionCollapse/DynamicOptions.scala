// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse

import javax.swing._
import java.awt._
import java.awt.event._
import com.barrybecker4.common.app.AppContext
import com.barrybecker4.simulation.waveFunctionCollapse.DynamicOptions.{DEFAULT_NUM_STEPS_PER_FRAME, RND}
import com.barrybecker4.simulation.waveFunctionCollapse.model.{OverlappingModel, SimpleTiledModel, WfcModel}
import com.barrybecker4.simulation.waveFunctionCollapse.model.json.{CommonModel, Overlapping, Samples, SimpleTiled}
import com.barrybecker4.simulation.waveFunctionCollapse.utils.FileUtil.{getSampleData, getSampleTiledData}
import com.barrybecker4.ui.sliders.{SliderGroup, SliderGroupChangeListener, SliderProperties}

import javax.swing.event.{ChangeEvent, ChangeListener}
import scala.util.Random


/**
  * Dynamic controls for WFC that will show at the right
  * @author Barry Becker
  */
object DynamicOptions {
  private val PREFERRED_WIDTH = 300
  private val RND = new Random()

  private val DEFAULT_NUM_STEPS_PER_FRAME = 100
  private val STEPS_PER_FRAME_SLIDER = "Num steps per frame"
  private val SLIDER_PROPS = Array(
    new SliderProperties(STEPS_PER_FRAME_SLIDER, 1, 500, DEFAULT_NUM_STEPS_PER_FRAME, 1.0)
  )
}

class DynamicOptions private[waveFunctionCollapse](var simulator: WaveFunctionCollapseExplorer)
  extends JPanel with ItemListener with ActionListener with ChangeListener with SliderGroupChangeListener  {

  private var dimensions: Dimension = new Dimension(100, 100)
  private val samples: Samples = getSampleData("menu-samples.json").samples;
  private var model: WfcModel = _
  private var stepsPerFrame: Int = DEFAULT_NUM_STEPS_PER_FRAME

  private var tabbedPane: JTabbedPane = _
  private var overlappingSampleCombo: JComboBox[CommonModel] = _
  private var tiledSampleCombo: JComboBox[CommonModel] = _

  // overlapping params
  private var nCombo: JComboBox[Int] = _
  private var periodicInputCB: JCheckBox = _
  private var overlappingPeriodicOutputCB: JCheckBox = _
  private var symmetryCombo: JComboBox[Int] = _
  private var groundCombo: JComboBox[Int] = _

  // tiled params
  private var subsetCombo: JComboBox[String] = _
  private var subsetComboPanel: JPanel = _
  private var tiledPeriodicOutputCB: JCheckBox = _
  private var blackCB: JCheckBox = _

  private var nextButton: JButton = _
  private var resetButton: JButton = _

  createDialogContent()


  protected def createDialogContent(): Unit = {
    setLayout (new BoxLayout (this, BoxLayout.Y_AXIS) )
    setBorder (BorderFactory.createEtchedBorder)
    setPreferredSize (new Dimension (DynamicOptions.PREFERRED_WIDTH, 900) )
    setAlignmentX(Component.LEFT_ALIGNMENT)

    tabbedPane = new JTabbedPane
    tabbedPane.add("Tiled", createTiledOptionsPanel())
    tabbedPane.setToolTipTextAt(0, "Parameters for SimpleTiled model")
    tabbedPane.add("Overlapping", createOverlappingOptionsPanel())
    tabbedPane.setToolTipTextAt(0, "Parameters for Overlapping Model")
    tabbedPane.addChangeListener(this)

    add(tabbedPane, BorderLayout.CENTER)
    add(createCommonOptions(), BorderLayout.SOUTH)
  }

  private def createOverlappingOptionsPanel(): JPanel = {
    val panel: JPanel = new JPanel()

    nCombo = createCombo(IndexedSeq(2, 3))
    overlappingSampleCombo = createSampleDropdown(samples.overlapping.toIndexedSeq)
    symmetryCombo = createCombo(IndexedSeq(1, 2, 4, 8))
    groundCombo = createCombo(IndexedSeq(0, -1, -2, -3, -4))
    periodicInputCB = createCheckBox("Periodic Input",
      "if checked, then the input pattern repeats", initiallyChecked = false)
    overlappingPeriodicOutputCB = createCheckBox("Periodic Output",
      "if checked, then output pattern repeats", initiallyChecked = true)

    panel.add(createComboPanel("Sample", overlappingSampleCombo))
    panel.add(createComboPanel("N", nCombo))
    panel.add(createComboPanel("Symmetry", symmetryCombo ))
    panel.add(createComboPanel("Ground", groundCombo))
    panel.add(createLeftAlignedPanel(overlappingPeriodicOutputCB))
    panel.add(createLeftAlignedPanel(periodicInputCB))

    createTabbedPanel(panel)
  }

  private def createTiledOptionsPanel(): JPanel = {
    val panel: JPanel = new JPanel()

    val tiledSamples = samples.simpletiled.toIndexedSeq
    tiledSampleCombo = createSampleDropdown(tiledSamples)
    subsetCombo = createCombo(IndexedSeq("-"))
    subsetComboPanel = createComboPanel("Subset", subsetCombo)
    updateSubsetCombo(tiledSamples.head)

    tiledPeriodicOutputCB = createCheckBox("Periodic Output",
      "if checked, then output pattern repeats", initiallyChecked = true)
    blackCB = createCheckBox("Black",
      "Uses black for uncollapsed regions", initiallyChecked = false)

    panel.add(createComboPanel("Sample", tiledSampleCombo))
    panel.add(subsetComboPanel)
    panel.add(createLeftAlignedPanel(tiledPeriodicOutputCB))
    panel.add(createLeftAlignedPanel(blackCB))

    createTabbedPanel(panel)
  }

  private def createTabbedPanel(childPanel: JPanel): JPanel = {
    val rootPanel: JPanel = new JPanel(new BorderLayout())
    childPanel.setLayout(new BoxLayout (childPanel, BoxLayout.Y_AXIS) )
    childPanel.setBorder(BorderFactory.createEtchedBorder)
    rootPanel.add(childPanel, BorderLayout.NORTH)
    rootPanel
  }

  def setDimensions(dims: Dimension): Unit = {
    dimensions = dims
    runModel()
  }

  private def createComboPanel(labelText: String, combo: Component): JPanel =  {
    val comboPanel = new JPanel(new FlowLayout(FlowLayout.LEADING))
    val label = new JLabel(labelText + ":")
    label.setPreferredSize(new Dimension(70, 20))
    comboPanel.add(label)
    comboPanel.add(combo)
    comboPanel
  }

  private def createCombo[A](elements: IndexedSeq[A]): JComboBox[A] = {
    val intModel = new DefaultComboBoxModel[A]()
    elements.foreach(s => intModel.addElement(s))
    val intCombo: JComboBox[A] = new JComboBox(intModel)
    intCombo.setSelectedItem(elements.head)
    intCombo.addItemListener(this)
    intCombo
  }

  private def createSampleDropdown(elements: Seq[CommonModel]): JComboBox[CommonModel] = {
    val sampleModel = new DefaultComboBoxModel[CommonModel]()
    elements.foreach(s => sampleModel.addElement(s))

    val sampleCombo = new JComboBox[CommonModel](sampleModel)
    sampleCombo.setToolTipText("Select a sample (either overlapping or simpleTiled)")

    sampleCombo.setSelectedItem(elements.head)
    sampleCombo.addItemListener(this)
    sampleCombo
  }

  private def createCheckBox(label: String, tooltip: String, initiallyChecked: Boolean) = {
    val cb = new JCheckBox(label, initiallyChecked)
    cb.setAlignmentX(Component.RIGHT_ALIGNMENT)
    cb.setToolTipText(tooltip)
    cb.addActionListener(this)
    cb
  }

  private def createLeftAlignedPanel(comp: Component): JPanel = {
    val panel = new JPanel(new FlowLayout(FlowLayout.LEADING))
    panel.add(comp)
    panel
  }

  private def createCommonOptions() = {
    val panel = new JPanel(new BorderLayout())
    val subPanel = new JPanel()
    subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS))
    subPanel.add(createButtons())
    subPanel.add(createSliders())
    panel.add(subPanel, BorderLayout.NORTH)
    panel
  }

  private def createButtons() = {
    val buttonsPanel = new JPanel
    nextButton = new JButton("Next")
    nextButton.setToolTipText("Advance the simulation by one time step")
    nextButton.addActionListener(this)

    resetButton = new JButton(AppContext.getLabel("RESET"))
    resetButton.setToolTipText("Restore the initial configuration")
    resetButton.addActionListener(this)

    buttonsPanel.add(nextButton)
    buttonsPanel.add(resetButton)
    buttonsPanel
  }

  private def createSliders(): JPanel = {
    val panel = new JPanel()
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS))

    val sliderGroup = new SliderGroup(DynamicOptions.SLIDER_PROPS)
    sliderGroup.addSliderChangeListener(this)
    panel.add(sliderGroup)
    panel
  }

  def reset(): Unit = {
    runModel()
  }

  private def updateSubsetCombo(sample: SimpleTiled): Unit = {
    val sampleTiledData = getSampleTiledData(sample.getName)

    if (sampleTiledData.set.subsets == null || sampleTiledData.set.subsets.isEmpty) {
      subsetCombo = new JComboBox[String]()
    } else {
      val values = sampleTiledData.set.subsets.map(s => s.name)
      println("SUBSET VALUES ======= " + values.mkString(", "))
      subsetCombo = createCombo(values)
      subsetCombo.setSelectedItem(values.head)
    }
    subsetComboPanel.remove(1)
    subsetComboPanel.add(subsetCombo)
    subsetComboPanel.invalidate()
    subsetComboPanel.revalidate()
  }

  private def getModel: WfcModel = {

    val sampleModel: CommonModel =
      if (tabbedPane.getSelectedIndex == 0) tiledSampleCombo.getSelectedItem.asInstanceOf[CommonModel]
      else overlappingSampleCombo.getSelectedItem.asInstanceOf[CommonModel]

    val wfcModel: WfcModel = sampleModel match {
      case overlapping: Overlapping => new OverlappingModel(
        overlapping.getName,
        nCombo.getSelectedItem.asInstanceOf[Int],
        dimensions.width,
        dimensions.height,
        periodicInputCB.isSelected,
        overlappingPeriodicOutputCB.isSelected,
        symmetryCombo.getSelectedItem.asInstanceOf[Int],
        groundCombo.getSelectedItem.asInstanceOf[Int],
        100)
      case simpleTiled: SimpleTiled => new SimpleTiledModel(
        dimensions.width,
        dimensions.height,
        simpleTiled.getName,
        subsetCombo.getSelectedItem.asInstanceOf[String],
        tiledPeriodicOutputCB.isSelected,
        blackCB.isSelected,
        100)
      case _ => throw new IllegalArgumentException("Unexpected type for " + sampleModel)
    }

    println("now running " + wfcModel)

    wfcModel
  }

  def runModel(): Unit = {
    model = getModel

    simulator.setModel(model)
    println("selected " + model.getName  )
    model.startRun(RND.nextInt())
  }

  /** Return None if not done, else true/false when done successful/failed */
  def advanceModel(): Option[Boolean] = {
    model.advance(stepsPerFrame)
  }

  override def itemStateChanged(e: ItemEvent): Unit = {
    if (e.getSource == tiledSampleCombo) {
      val sample: SimpleTiled = tiledSampleCombo.getSelectedItem.asInstanceOf[SimpleTiled]
      updateSubsetCombo(sample)
    }
    runModel()
  }

  override def actionPerformed(e: ActionEvent): Unit = {
    if (e.getSource == nextButton)
      advanceModel()
    else runModel()
  }

  override def stateChanged(e: ChangeEvent): Unit = {
    runModel()
  }

  /** One of the sliders was moved. */
  override def sliderChanged(sliderIndex: Int, sliderName: String, value: Double): Unit = {
    if (sliderName == DynamicOptions.STEPS_PER_FRAME_SLIDER) {
      stepsPerFrame = value.toInt
    }
  }
}
