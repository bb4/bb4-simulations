// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.options.ui

import com.barrybecker4.simulation.trading.model.generationstrategy._
import com.barrybecker4.simulation.trading.model.plugin.StrategyPlugins
import com.barrybecker4.simulation.trading.options.StockGenerationOptions
import com.barrybecker4.ui.components.NumberInput
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JComboBox
import java.awt.Component._
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingUtilities
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import java.util


/**
  * @author Barry Becker
  */
class StockGenerationOptionsPanel() extends JPanel with ItemListener {

  private val generationOptions = new StockGenerationOptions
  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))

  /** number of dice to use.  */
  private val numStocksField = new NumberInput("Number of stocks in each sample (1 - 1000): ",
    generationOptions.numStocks,
    "The number of stocks in each trial. The average value of which will be one data point.", 1, 1000, true)

  /** Number of time periods (for example months or years)  */
  private val numTimePeriodsField  = new NumberInput("Number of time periods (1 - 1000): ",
    generationOptions.numTimePeriods, "Number of time periods (for example months or years).", 1, 1000, true)
  /** Starting value of each stock in dollars  */
  private val startingValueField = new NumberInput("Starting stock value : ", generationOptions.startingValue,
    "Starting value of each stock in the sample (in dollars). For simplicity, they are all the same.",
    1, 1000000, false)

  // needed to get the field to extend to the left.
  numStocksField.setAlignmentX(CENTER_ALIGNMENT)
  numTimePeriodsField.setAlignmentX(CENTER_ALIGNMENT)
  startingValueField.setAlignmentX(CENTER_ALIGNMENT)
  val strategyDropDownElement: JPanel = createStrategyDropDown
  private val strategyOptionsPanel = new JPanel(new BorderLayout)
  strategyOptionsPanel.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, this.getBackground))
  strategyOptionsPanel.add(generationOptions.generationStrategy.getOptionsUI, BorderLayout.CENTER)
  add(numStocksField)
  add(numTimePeriodsField)
  add(startingValueField)
  add(strategyDropDownElement)
  add(strategyOptionsPanel)
  setBorder(Section.createBorder("Stock Generation Options"))
  private val generationStrategies =
    new StrategyPlugins[GenerationStrategy](
      "com.barrybecker4.simulation.trading.model.generationstrategy",
      classOf[GenerationStrategy],
      util.Arrays.asList(
        new FlatStrategy, new GaussianStrategy, new SineStrategy, new RandomUpAndDownStrategy,
        new RandomWithAdditiveMomentumStrategy))

  val choices = generationStrategies.getStrategies
  System.out.println("generation strategies: " + choices)
  private var strategyCombo = new JComboBox[String](choices.toArray(new Array[String](choices.size)))


  private def createStrategyDropDown = {
    val panel = new JPanel
    panel.setLayout(new FlowLayout(FlowLayout.LEADING))
    val label = new JLabel("Stock generation strategy : ")

    println("Default generation strategy = " + StockGenerationOptions.DEFAULT_GENERATION_STRATEGY)
    strategyCombo.setSelectedItem(StockGenerationOptions.DEFAULT_GENERATION_STRATEGY.name)
    println("currently selected strategy = " + StockGenerationOptions.DEFAULT_GENERATION_STRATEGY.name)
    generationOptions.generationStrategy = getCurrentlySelectedStrategy
    strategyCombo.addItemListener(this)
    setStrategyTooltip()
    panel.add(label)
    panel.add(strategyCombo)
    panel
  }

  override def itemStateChanged(e: ItemEvent): Unit = {
    setStrategyTooltip()
    generationOptions.generationStrategy = getCurrentlySelectedStrategy
    strategyOptionsPanel.removeAll()
    strategyOptionsPanel.add(generationOptions.generationStrategy.getOptionsUI)
    // This will allow the dialog to resize appropriately given the new content.
    val dlg = SwingUtilities.getAncestorOfClass(classOf[JDialog], this)
    if (dlg != null) dlg.asInstanceOf[JDialog].pack()
  }

  private def getCurrentlySelectedStrategy = {
    System.out.println("Currently selected = " + strategyCombo.getSelectedItem + " out of " + strategyCombo.getItemCount)
    generationStrategies.getStrategy(strategyCombo.getSelectedItem.asInstanceOf[String])
  }

  private def setStrategyTooltip(): Unit = {
    strategyCombo.setToolTipText(getCurrentlySelectedStrategy.description)
  }

  private[ui] def getOptions = {
    generationOptions.numStocks = numStocksField.getIntValue
    generationOptions.numTimePeriods = numTimePeriodsField.getIntValue
    generationOptions.startingValue = startingValueField.getValue
    generationOptions.generationStrategy.acceptSelectedOptions()
    generationOptions
  }
}