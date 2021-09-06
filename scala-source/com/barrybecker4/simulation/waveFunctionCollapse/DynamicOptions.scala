// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse

import javax.swing._
import java.awt._
import java.awt.event._
import com.barrybecker4.common.app.AppContext
import com.barrybecker4.simulation.waveFunctionCollapse.model.json.CommonModel
import com.barrybecker4.simulation.waveFunctionCollapse.utils.FileUtil.getSampleData


/**
  * Dynamic controls for WFC that will show at the right
  * @author Barry Becker
  */
object DynamicOptions {
  private val PREFERRED_WIDTH = 300
  private val SPACING = 28
}

class DynamicOptions private[waveFunctionCollapse](var simulator: WaveFunctionCollapseExplorer)
  extends JPanel with ItemListener with ActionListener {

  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
  setBorder(BorderFactory.createEtchedBorder)
  setPreferredSize(new Dimension(DynamicOptions.PREFERRED_WIDTH, 900))
  //val generalPanel: JPanel = createGeneralControls

  add(createSampleDropdown)
  add(Box.createVerticalStrut(DynamicOptions.SPACING))
  add(createButtons)
  //add(Box.createVerticalStrut(DynamicOptions.SPACING))
  //add(generalPanel)
  //add(Box.createVerticalStrut(DynamicOptions.SPACING))

  val fill = new JPanel
  fill.setPreferredSize(new Dimension(1, 1000))
  add(fill)
  private var sampleCombo: JComboBox[CommonModel] = _
  private var nextButton: JButton = _
  private var resetButton: JButton = _

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
  private def createSampleDropdown = {
    val sampleComboPanel = new JPanel
    val label = new JLabel("Sample: ")
    label.setPreferredSize(new Dimension(50, 20))

    val sampleModel = new DefaultComboBoxModel[CommonModel]()
    val samples = getSampleData.samples.all()
    samples.foreach(s => sampleModel.addElement(s))

    sampleCombo = new JComboBox[CommonModel](sampleModel)
    sampleCombo.setToolTipText("Select a sample (either overlapping or simpleTiled)")

    sampleCombo.setSelectedItem(samples(0))
    sampleCombo.addItemListener(this)
    sampleComboPanel.add(label)
    sampleComboPanel.add(sampleCombo)
    sampleComboPanel
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
    val sample: CommonModel = sampleCombo.getSelectedItem.asInstanceOf[CommonModel]
    println("selected " + sample.getName)
  }

  override def actionPerformed(e: ActionEvent): Unit = {
    if (e.getSource == nextButton) {
      println("next step requested")
    }
    else if (e.getSource == resetButton) {
      println("reset requested")
    }
    else throw new IllegalStateException("Unexpected button " + e.getSource)
  }
}
