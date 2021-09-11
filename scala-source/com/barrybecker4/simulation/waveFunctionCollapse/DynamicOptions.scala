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
  private val SPACING = 28
  private val RND = new Random()
}

class DynamicOptions private[waveFunctionCollapse](var simulator: WaveFunctionCollapseExplorer)
  extends JPanel with ItemListener with ActionListener {

  private var sampleModel: CommonModel = _
  private var dimensions: Dimension = new Dimension(100, 100)

  private var tabbedPanel: JTabbedPane = _
  private var overlappingSampleCombo: JComboBox[CommonModel] = _
  private var tiledSampleCombo: JComboBox[CommonModel] = _
  private var fillPanelCB: JCheckBox = _
  private var nextButton: JButton = _
  private var resetButton: JButton = _
  private val samples: Samples = getSampleData("menu-samples.json").samples;

  createDialogContent()


  protected def createDialogContent(): Unit = {
    setLayout(new BorderLayout)

    tabbedPanel = new JTabbedPane
    tabbedPanel.add("Overlapping", createOverlappingOptionsPanel())
    tabbedPanel.setToolTipTextAt(0, "Parameters for Overlapping Model")
    tabbedPanel.add("Tiled", createTiledOptionsPanel())
    tabbedPanel.setToolTipTextAt(0, "Parameters for SimpleTiled model")

    add(tabbedPanel, BorderLayout.CENTER)
    add(createButtons, BorderLayout.SOUTH)
  }

  private def createOverlappingOptionsPanel(): JPanel = {
    val panel: JPanel = new JPanel()

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
    setBorder(BorderFactory.createEtchedBorder)
    setPreferredSize(new Dimension(DynamicOptions.PREFERRED_WIDTH, 900))

    overlappingSampleCombo = createSampleDropdown(samples.overlapping.toIndexedSeq)
    panel.add(createSampleDropdownPanel(overlappingSampleCombo))
    panel
  }

  private def createTiledOptionsPanel(): JPanel = {
    val panel: JPanel = new JPanel()

    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
    setBorder(BorderFactory.createEtchedBorder)
    setPreferredSize(new Dimension(DynamicOptions.PREFERRED_WIDTH, 900))

    tiledSampleCombo = createSampleDropdown(samples.simpletiled.toIndexedSeq)
    panel.add(createSampleDropdownPanel(tiledSampleCombo))
    panel
  }

  def setDimensions(dims: Dimension): Unit = {
    dimensions = dims
  }

  private def createGeneralControls = {
    val panel = new JPanel(new BorderLayout)
    panel.setBorder(createTitledBorder("General parameters"))
    //panel.add(createSampleDropdown, BorderLayout.CENTER)
    panel
  }

  private def createTitledBorder(title: String) =
    BorderFactory.createCompoundBorder(
      BorderFactory.createTitledBorder(title),
      BorderFactory.createEmptyBorder(5, 5, 5, 5)
    )

  /**
    * The dropdown menu at the top for selecting the sample to use (either overlapping or simple tiled).
    * @return a dropdown/down component.
    */
  private def createSampleDropdownPanel(dropdown: JComboBox[CommonModel]): JPanel = {
    val sampleComboPanel = new JPanel
    val label = new JLabel("Sample: ")
    label.setPreferredSize(new Dimension(50, 20))

    sampleComboPanel.add(label)
    sampleComboPanel.add(dropdown)
    sampleComboPanel
  }

  private def createSampleDropdown(elements: Seq[CommonModel]): JComboBox[CommonModel] = {
    val sampleComboPanel = new JPanel

    val sampleModel = new DefaultComboBoxModel[CommonModel]()
    elements.foreach(s => sampleModel.addElement(s))

    val sampleCombo = new JComboBox[CommonModel](sampleModel)
    sampleCombo.setToolTipText("Select a sample (either overlapping or simpleTiled)")

    sampleCombo.setSelectedItem(elements.head)
    //selectModel(samples.head)
    sampleCombo.addItemListener(this)
    sampleCombo
  }


  private def createCheckBoxes = {
    val checkBoxes = new JPanel(new GridLayout(0, 1))

    fillPanelCB = createCheckBox("Fill whole panel",
      "if checked, then the pattern will fill the whole window", true)
    checkBoxes.add(fillPanelCB)
    checkBoxes.setBorder(BorderFactory.createEtchedBorder)
    checkBoxes
  }

  private def createCheckBox(label: String, tooltip: String, initiallyChecked: Boolean) = {
    val cb = new JCheckBox(label, initiallyChecked)
    cb.setToolTipText(tooltip)
    cb.addActionListener(this)
    cb
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
    else if (e.getSource == fillPanelCB) {
      // do nothing
    }
    else throw new IllegalStateException("Unexpected button " + e.getSource)
  }
}
