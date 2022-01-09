// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.ui

import com.barrybecker4.common.app.AppContext
import com.barrybecker4.simulation.waveFunctionCollapse.WaveFunctionCollapseExplorer
import com.barrybecker4.simulation.waveFunctionCollapse.model.json.{CommonModel, Overlapping, Samples, SimpleTiled}
import com.barrybecker4.simulation.waveFunctionCollapse.model.{OverlappingImageParams, OverlappingModel, SimpleTiledModel, WfcModel}
import com.barrybecker4.simulation.waveFunctionCollapse.ui.DynamicOptions.{DEFAULT_NUM_STEPS_PER_FRAME, RND}
import com.barrybecker4.simulation.waveFunctionCollapse.ui.dropdown.{BoundsPopupMenuListener, ComboBoxRenderer}
import com.barrybecker4.simulation.waveFunctionCollapse.utils.FileUtil.{getSampleData, getSampleTiledData, readImage}
import com.barrybecker4.ui.sliders.{SliderGroup, SliderGroupChangeListener, SliderProperties}

import java.awt._
import java.awt.event._
import javax.swing._
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
  private val samples: Samples = getSampleData("ui/menu-samples.json").samples
  private var model: WfcModel = _
  private var stepsPerFrame: Int = DEFAULT_NUM_STEPS_PER_FRAME

  private var tabbedPane: JTabbedPane = _
  private var overlappingSampleCombo: JComboBox[Integer] = _
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

  private var allowInconsistenciesCB: JCheckBox = _

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
    tabbedPane.setSelectedIndex(0)

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

    panel.add(createComboPanel("Sample", "image to use as basis for pattern", overlappingSampleCombo))
    panel.add(createComboPanel("N", "radius of effect", nCombo))
    panel.add(createComboPanel("Symmetry", "n-fold symmetry (typically 8)", symmetryCombo ))
    panel.add(createComboPanel("Ground", "0 if no ground", groundCombo))
    panel.add(createLeftAlignedPanel(overlappingPeriodicOutputCB))
    panel.add(createLeftAlignedPanel(periodicInputCB))

    createTabbedPanel(panel)
  }

  private def createTiledOptionsPanel(): JPanel = {
    val panel: JPanel = new JPanel()

    val tiledSamples = samples.simpletiled.toIndexedSeq
    tiledSampleCombo = createTiledSampleDropdown(tiledSamples)
    subsetCombo = createCombo(IndexedSeq("-"))
    subsetComboPanel = createComboPanel("Subset", "ruleset to apply to images", subsetCombo)
    updateSubsetCombo(tiledSamples.head)

    tiledPeriodicOutputCB = createCheckBox("Periodic Output",
      "if checked, then output pattern repeats", initiallyChecked = true)
    blackCB = createCheckBox("Black",
      "Uses black for uncollapsed regions", initiallyChecked = false)

    panel.add(createComboPanel("Sample", "set of tiles to forn the bases for the pattern", tiledSampleCombo))
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

  private def createComboPanel(labelText: String, ttip: String, combo: Component): JPanel =  {
    val comboPanel = new JPanel(new FlowLayout(FlowLayout.LEADING))
    val label = new JLabel(labelText + ":")
    label.setToolTipText(ttip)
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

  private def createTiledSampleDropdown(elements: Seq[CommonModel]): JComboBox[CommonModel] = {
    val sampleModel = new DefaultComboBoxModel[CommonModel]()
    elements.foreach(s => sampleModel.addElement(s))

    val sampleCombo = new JComboBox[CommonModel](sampleModel)
    sampleCombo.setPreferredSize(new Dimension(180, 20))
    sampleCombo.setMaximumSize(new Dimension(180, 20))

    sampleCombo.setToolTipText("Select a sample")
    sampleCombo.addPopupMenuListener(new BoundsPopupMenuListener(800))

    sampleCombo.setSelectedItem(elements.head)
    sampleCombo.addItemListener(this)
    sampleCombo
  }

  private def createSampleDropdown(elements: Seq[CommonModel]): JComboBox[Integer] = {

    val images: Array[ImageIcon] = Array.ofDim(elements.length)
    val intArray: Array[Integer] = Array.ofDim(elements.length)
    val labels: Array[String] = Array.ofDim(elements.length)
    var i: Int = 0
    while (i < elements.length) {
      intArray(i) = i
      labels(i) = elements(i).getName
      images(i) = new ImageIcon(readImage(s"samples/${elements(i).getName}.png"))
      if (images(i) != null) images(i).setDescription(elements(i).getName)
      i += 1
    }

    val sampleCombo = new JComboBox(intArray)
    val renderer: ComboBoxRenderer = new ComboBoxRenderer(images.toIndexedSeq, labels.toIndexedSeq)
    renderer.setPreferredSize(new Dimension(200, 130))
    sampleCombo.setRenderer(renderer)

    sampleCombo.setPreferredSize(new Dimension(200, 20))
    sampleCombo.setMaximumSize(new Dimension(200, 20))

    sampleCombo.setToolTipText("Select a sample")
    sampleCombo.addPopupMenuListener(new BoundsPopupMenuListener(600))

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

    allowInconsistenciesCB = createCheckBox("Allow inconsistencies",
      "if checked, then performance is much better and it won't fail, but there may be flaws in the result",
      initiallyChecked = true)

    subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS))
    subPanel.add(createButtons())
    subPanel.add(allowInconsistenciesCB)
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
    sliderGroup.setSliderListener(this)
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
      subsetCombo = createCombo(values.toIndexedSeq)
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
      else samples.overlapping(overlappingSampleCombo.getSelectedItem.asInstanceOf[Int])

    println("getModel dims = " + dimensions)

    val wfcModel: WfcModel = sampleModel match {
      case overlapping: Overlapping => new OverlappingModel(
        overlapping.getName,
        dimensions.width,
        dimensions.height,
        overlappingPeriodicOutputCB.isSelected,
        OverlappingImageParams(
          nCombo.getSelectedItem.asInstanceOf[Int],
          symmetryCombo.getSelectedItem.asInstanceOf[Int],
          periodicInputCB.isSelected,
          groundCombo.getSelectedItem.asInstanceOf[Int]
        ),
        100,
        allowInconsistenciesCB.isSelected)
      case simpleTiled: SimpleTiled => new SimpleTiledModel(
        dimensions.width,
        dimensions.height,
        simpleTiled.getName,
        subsetCombo.getSelectedItem.asInstanceOf[String],
        tiledPeriodicOutputCB.isSelected,
        blackCB.isSelected,
        100,
        allowInconsistenciesCB.isSelected
      )
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

  // Tab changed
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
