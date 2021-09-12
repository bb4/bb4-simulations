// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse

import javax.swing._
import java.awt._
import java.awt.event._
import com.barrybecker4.common.app.AppContext
import com.barrybecker4.simulation.waveFunctionCollapse.DynamicOptions.RND
import com.barrybecker4.simulation.waveFunctionCollapse.model.{OverlappingModel, SimpleTiledModel, WfcModel}
import com.barrybecker4.simulation.waveFunctionCollapse.model.json.{CommonModel, Overlapping, Samples, SimpleTiled}
import com.barrybecker4.simulation.waveFunctionCollapse.utils.FileUtil.getSampleData

import scala.util.Random


/**
  * Dynamic controls for WFC that will show at the right
  * @author Barry Becker
  */
object DynamicOptions {
  private val PREFERRED_WIDTH = 300
  private val RND = new Random()
}

class DynamicOptions private[waveFunctionCollapse](var simulator: WaveFunctionCollapseExplorer)
  extends JPanel with ItemListener with ActionListener {

  private var sampleModel: CommonModel = _
  private var dimensions: Dimension = new Dimension(100, 100)
  private val samples: Samples = getSampleData("menu-samples.json").samples;

  private var tabbedPanel: JTabbedPane = _
  private var overlappingSampleCombo: JComboBox[CommonModel] = _
  private var tiledSampleCombo: JComboBox[CommonModel] = _


  // overlapping params
  private var nCombo: JComboBox[Int] = _
  private var periodicInputCB: JCheckBox = _
  private var overlappingPeriodicOutputCB: JCheckBox = _
  private var symmetryCombo: JComboBox[Int] = _
  private var groundCombo: JComboBox[Int] = _ // 0, -1, 02, -3, -4
  private var limitCombo: JComboBox[Int] = _ // 0, 1, 10, 50

  // tiled params
  private var subsetCombo: JComboBox[String] = _
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

    tabbedPanel = new JTabbedPane
    tabbedPanel.add("Overlapping", createOverlappingOptionsPanel())
    tabbedPanel.setToolTipTextAt(0, "Parameters for Overlapping Model")
    tabbedPanel.add("Tiled", createTiledOptionsPanel())
    tabbedPanel.setToolTipTextAt(0, "Parameters for SimpleTiled model")

    add(tabbedPanel, BorderLayout.CENTER)
    add(createButtons, BorderLayout.SOUTH)
  }

  private def createOverlappingOptionsPanel(): JPanel = {
    val rootPanel: JPanel = new JPanel(new BorderLayout())

    val panel: JPanel = new JPanel()
    panel.setLayout(new BoxLayout (panel, BoxLayout.Y_AXIS) )
    panel.setBorder(BorderFactory.createEtchedBorder)

    nCombo = createCombo(IndexedSeq(2, 3))
    overlappingSampleCombo = createSampleDropdown(samples.overlapping.toIndexedSeq)
    symmetryCombo = createCombo(IndexedSeq(1, 2, 4, 8))
    periodicInputCB = createCheckBox("Periodic Input",
      "if checked, then the input pattern repeats", initiallyChecked = false)
    overlappingPeriodicOutputCB = createCheckBox("Periodic Output",
      "if checked, then output pattern repeats", initiallyChecked = true)
    groundCombo = createCombo(IndexedSeq(0, -1, -2, -3, -4))
    limitCombo = createCombo(IndexedSeq(0, 1, 10, 50))

    panel.add(createComboPanel("Sample", overlappingSampleCombo))
    panel.add(createComboPanel("N", nCombo))
    panel.add(createComboPanel("Symmetry", symmetryCombo ))
    panel.add(createLeftAlignedPanel(overlappingPeriodicOutputCB))
    panel.add(createLeftAlignedPanel(periodicInputCB))
    panel.add(createComboPanel("Ground", groundCombo))
    panel.add(createComboPanel("limit", limitCombo))

    rootPanel.add(panel, BorderLayout.NORTH)
    rootPanel
  }

  private def createTiledOptionsPanel(): JPanel = {
    val rootPanel: JPanel = new JPanel(new BorderLayout())

    val panel: JPanel = new JPanel()
    panel.setLayout(new BoxLayout (panel, BoxLayout.Y_AXIS) )
    panel.setBorder(BorderFactory.createEtchedBorder)
    //panel.setPreferredSize(new Dimension(DynamicOptions.PREFERRED_WIDTH, 900))

    tiledSampleCombo = createSampleDropdown(samples.simpletiled.toIndexedSeq)
    tiledPeriodicOutputCB = createCheckBox("Periodic Output",
      "if checked, then output pattern repeats", initiallyChecked = true)
    subsetCombo = createCombo(IndexedSeq("a", "b"))
    blackCB = createCheckBox("Black",
      "uses only color black?", initiallyChecked = false)

    panel.add(createComboPanel("Sample", tiledSampleCombo))
    panel.add(createLeftAlignedPanel(tiledPeriodicOutputCB))
    panel.add(createLeftAlignedPanel(blackCB))

    rootPanel.add(panel, BorderLayout.NORTH)
    rootPanel
  }

  def setDimensions(dims: Dimension): Unit = {
    dimensions = dims
  }

  private def createTitledBorder(title: String) =
    BorderFactory.createCompoundBorder(
      BorderFactory.createTitledBorder(title),
      BorderFactory.createEmptyBorder(5, 5, 5, 5)
    )

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
    //selectModel(samples.head)
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

  private def createButtons = {
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

  def reset(): Unit = {
  }


  override def itemStateChanged(e: ItemEvent): Unit = {
    var sample: CommonModel = null

    if (e.getSource == overlappingSampleCombo)
      sample = overlappingSampleCombo.getSelectedItem.asInstanceOf[CommonModel]
    else if (e.getSource == tiledSampleCombo)
      sample = tiledSampleCombo.getSelectedItem.asInstanceOf[CommonModel]
    selectModel(sample)
  }

  def selectModel(sample: CommonModel): Unit = {
    sampleModel = sample

    //set default values for UI controls
    sampleModel match {
      case overlapping: Overlapping =>
//        overlapping.getName,
//        overlapping.getN,
//        overlapping.getPeriodicInput,
//        overlapping.getPeriodic,
//        overlapping.getSymmetry,
//        overlapping.getGround,
//        overlapping.getLimit
      case simpleTiled: SimpleTiled =>
//        simpleTiled.getName,
//        simpleTiled.getSubset,
//        simpleTiled.getPeriodic,
//        simpleTiled.getBlack,
//        simpleTiled.getLimit
      case _ => throw new IllegalArgumentException("Unexpected type for " + sampleModel)
    }

    runModel()
  }

  def runModel(): Unit = {
    //if (dimensions == null) return;
    val model: WfcModel = sampleModel match {
      case overlapping: Overlapping => new OverlappingModel(
        overlapping.getName,
        overlapping.getN,
        dimensions.width,
        dimensions.height,
        overlapping.getPeriodicInput,
        overlapping.getPeriodic,
        overlapping.getSymmetry,
        overlapping.getGround,
        overlapping.getLimit)
      case simpleTiled: SimpleTiled => new SimpleTiledModel(
        dimensions.width,
        dimensions.height,
        simpleTiled.getName,
        simpleTiled.getSubset,
        simpleTiled.getPeriodic,
        simpleTiled.getBlack,
        simpleTiled.getLimit)
      case _ => throw new IllegalArgumentException("Unexpected type for " + sampleModel)
    }

    simulator.setModel(model)

    println("selected " + model.getName + " screenshots:" + sampleModel.getScreenshots )
    var success = false
    val MAX_TRIES = 20
    var ct = 1
    while (!success && ct <= MAX_TRIES) {
      success = model.run(RND.nextInt())
      if (!success) println(s"\nfailed to create image on try $ct out of $MAX_TRIES")
      ct += 1
    }
  }

  override def actionPerformed(e: ActionEvent): Unit = {
    if (e.getSource == nextButton) {
      println("next step requested")
      runModel()
    }
    else if (e.getSource == resetButton) {
      println("reset requested")
      runModel()
    }
    else if (e.getSource == overlappingPeriodicOutputCB) {
      // do nothing
    }
    else throw new IllegalStateException("Unexpected button " + e.getSource)
  }
}
