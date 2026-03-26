// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.options.ui

import com.barrybecker4.simulation.trading.model.generationstrategy._
import com.barrybecker4.simulation.trading.model.plugin.StrategyPlugins
import com.barrybecker4.simulation.trading.options.StockGenerationOptions
import com.barrybecker4.ui.components.NumberInput
import java.awt.{BorderLayout, Component, FlowLayout}
import java.awt.event.{ItemEvent, ItemListener}
import javax.swing.{BorderFactory, BoxLayout, JComboBox, JLabel, JPanel}

import scala.compiletime.uninitialized


/**
  * @author Barry Becker
  */
class StockGenerationOptionsPanel() extends JPanel with ItemListener {

  private val generationOptions = new StockGenerationOptions
  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))

  /** number of dice to use.  */
  private val numStocksField = new NumberInput("Number of stocks in each sample (1 - 1000): ",
    generationOptions.numStocks,
    "The number of stocks in each trial. The average value of which will be one data point.",
    1, 1000, true)

  /** Number of time periods (for example months or years)  */
  private val numTimePeriodsField  = new NumberInput("Number of time periods (1 - 1000): ",
    generationOptions.numTimePeriods, "Number of time periods (for example months or years).",
    1, 1000, true)
  /** Starting value of each stock in dollars  */
  private val startingValueField = new NumberInput("Starting stock value : ", generationOptions.startingValue,
    "Starting value of each stock in the sample (in dollars). For simplicity, they are all the same.",
    1, 1000000, false)

  // needed to get the field to extend to the left.
  numStocksField.setAlignmentX(Component.CENTER_ALIGNMENT)
  numTimePeriodsField.setAlignmentX(Component.CENTER_ALIGNMENT)
  startingValueField.setAlignmentX(Component.CENTER_ALIGNMENT)

  private val generationStrategies =
    new StrategyPlugins[GenerationStrategy](
      "com.barrybecker4.simulation.trading.model.generationstrategy",
      classOf[GenerationStrategy],
      Seq(
        new FlatStrategy,
        new GaussianStrategy,
        new SineStrategy,
        new RandomUpAndDownStrategy,
        new RandomWithAdditiveMomentumStrategy))
  private var generationStrategyCombo: JComboBox[String] = uninitialized

  val strategyDropDownElement: JPanel = createStrategyDropDown
  private val strategyOptionsPanel = new JPanel(new BorderLayout)
  strategyOptionsPanel.setBorder(
    BorderFactory.createMatteBorder(0, 5, 0, 0, this.getBackground))
  strategyOptionsPanel.add(generationOptions.generationStrategy.getOptionsUI, BorderLayout.CENTER)
  add(numStocksField)
  add(numTimePeriodsField)
  add(startingValueField)
  add(strategyDropDownElement)
  add(strategyOptionsPanel)
  setBorder(Section.createBorder("Stock Generation Options"))


  private def createStrategyDropDown = {
    val panel = new JPanel
    panel.setLayout(new FlowLayout(FlowLayout.LEADING))
    val label = new JLabel("Stock generation strategy : ")

    val choices = generationStrategies.getStrategies
    generationStrategyCombo = new JComboBox[String](choices.toArray)

    generationStrategyCombo.setSelectedItem(StockGenerationOptions.DEFAULT_GENERATION_STRATEGY.name)
    generationOptions.generationStrategy = getCurrentlySelectedStrategy
    generationStrategyCombo.addItemListener(this)
    setStrategyTooltip()
    panel.add(label)
    panel.add(generationStrategyCombo)
    panel
  }

  override def itemStateChanged(e: ItemEvent): Unit = {
    setStrategyTooltip()
    generationOptions.generationStrategy = getCurrentlySelectedStrategy
    strategyOptionsPanel.removeAll()
    strategyOptionsPanel.add(generationOptions.generationStrategy.getOptionsUI)
    StrategyPanelUi.repackAncestorDialog(this)
  }

  private def getCurrentlySelectedStrategy =
    generationStrategies.getStrategy(generationStrategyCombo.getSelectedItem.asInstanceOf[String])

  private def setStrategyTooltip(): Unit = {
    generationStrategyCombo.setToolTipText(getCurrentlySelectedStrategy.description)
  }

  private[ui] def getOptions = {
    generationOptions.numStocks = numStocksField.getIntValue
    generationOptions.numTimePeriods = numTimePeriodsField.getIntValue
    generationOptions.startingValue = startingValueField.getValue
    generationOptions.generationStrategy.acceptSelectedOptions()
    generationOptions
  }
}
