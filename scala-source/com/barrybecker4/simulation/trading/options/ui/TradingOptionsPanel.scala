// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.options.ui

import com.barrybecker4.simulation.trading.model.plugin.StrategyPlugins
import com.barrybecker4.simulation.trading.model.tradingstrategy._
import com.barrybecker4.simulation.trading.options.TradingOptions
import com.barrybecker4.ui.components.NumberInput
import java.awt.{BorderLayout, Component, FlowLayout}
import java.awt.event.{ItemEvent, ItemListener}
import javax.swing.{BorderFactory, BoxLayout, JComboBox, JLabel, JPanel}

import scala.compiletime.uninitialized


/**
  * There are basic trading options, and then there is the trading strategy.
  * @author Barry Becker
  */
class TradingOptionsPanel() extends JPanel with ItemListener {

  setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
  private var tradingOptions = new TradingOptions
  /** Starting total in dollars */
  private var startingTotalField = new NumberInput("Starting Total Funds (1000 - 1000000): ",
    tradingOptions.startingTotal,
    "The total amount of money that you start with. you will choose to invest some portion of it initially.",
    1000, 1000000, false)
  /** The amount of money to use to purchase stock(s) at the starting price.  */
  private var startingInvestmentPercentField = new NumberInput("Starting investment percent: ",
    100 * tradingOptions.startingInvestmentPercent, "The percent of total funds to invest initially.", 0, 100, false)
  private var theoreticalMaxGainField = new NumberInput("Theoretical max gain: ", tradingOptions.theoreticalMaxGain,
    "Enter value for the biggest profit you could hope to get from this model. " +
      "Used only to determine the max extent of the x axis.", 0, 100000000, false)
  // needed to get the field to extend to the left.
  startingTotalField.setAlignmentX(Component.CENTER_ALIGNMENT)
  startingInvestmentPercentField.setAlignmentX(Component.CENTER_ALIGNMENT)
  theoreticalMaxGainField.setAlignmentX(Component.CENTER_ALIGNMENT)

  private var strategyCombo: JComboBox[String] = uninitialized
  private val tradingPlugins = new StrategyPlugins[TradingStrategy](
    "com.barrybecker4.simulation.trading.model.tradingstrategy",
    classOf[TradingStrategy],
    Seq(
      new BuyAndHoldStrategy,
      new BuyPercentOfInvestmentStrategy,
      new BuyPercentOfReserveStrategy,
      new SellWhatWasBoughtStrategy
    )
  )

  val strategyDropDownElement: JPanel = createStrategyDropDown
  private var strategyOptionsPanel = new JPanel(new BorderLayout)
  strategyOptionsPanel.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, this.getBackground))
  strategyOptionsPanel.add(tradingOptions.tradingStrategy.getOptionsUI, BorderLayout.CENTER)
  // these are common to all strategies
  add(startingTotalField)
  add(startingInvestmentPercentField)
  add(theoreticalMaxGainField)
  add(strategyDropDownElement)
  add(strategyOptionsPanel)
  setBorder(Section.createBorder("Trading Options"))

  private def createStrategyDropDown = {
    val panel = new JPanel
    panel.setLayout(new FlowLayout(FlowLayout.LEADING))
    val label = new JLabel("Trading strategy : ")
    val choices = tradingPlugins.getStrategies

    strategyCombo = new JComboBox[String](choices.toArray)
    strategyCombo.setSelectedItem(TradingOptions.DEFAULT_TRADING_STRATEGY.name)
    tradingOptions.tradingStrategy = getCurrentlySelectedStrategy
    strategyCombo.addItemListener(this)
    setStrategyTooltip()
    panel.add(label)
    panel.add(strategyCombo)
    panel
  }

  private[ui] def getOptions = {
    tradingOptions.startingTotal = startingTotalField.getValue
    tradingOptions.startingInvestmentPercent = startingInvestmentPercentField.getValue / 100.0
    tradingOptions.theoreticalMaxGain = theoreticalMaxGainField.getValue
    tradingOptions.tradingStrategy.acceptSelectedOptions()
    tradingOptions
  }

  override def itemStateChanged(e: ItemEvent): Unit = {
    setStrategyTooltip()
    tradingOptions.tradingStrategy = getCurrentlySelectedStrategy
    strategyOptionsPanel.removeAll()
    strategyOptionsPanel.add(tradingOptions.tradingStrategy.getOptionsUI)
    StrategyPanelUi.repackAncestorDialog(this)
  }

  private def getCurrentlySelectedStrategy =
    tradingPlugins.getStrategy(strategyCombo.getSelectedItem.asInstanceOf[String])

  private def setStrategyTooltip(): Unit = {
    strategyCombo.setToolTipText(getCurrentlySelectedStrategy.description)
  }
}
