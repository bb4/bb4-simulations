// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.options.ui

import com.barrybecker4.simulation.common1.ui.Simulator
import com.barrybecker4.simulation.common1.ui.SimulatorOptionsDialog
import com.barrybecker4.simulation.trading.TradingSimulator
import javax.swing._
import java.awt._


/**
  * @author Barry Becker
  */
class OptionsDialog(parent: Component, simulator: Simulator) extends SimulatorOptionsDialog(parent, simulator) {

  private var stockGenerationOptionsPanel: StockGenerationOptionsPanel = _
  private var tradingOptionsPanel: TradingOptionsPanel = _
  private var graphingOptionsPanel: GraphingOptionsPanel = _

  override def getTitle = "Stock Simulation Configuration"

  override protected def createCustomParamPanel: JPanel = {
    val paramPanel = new JPanel
    paramPanel.setLayout(new BorderLayout)
    stockGenerationOptionsPanel = new StockGenerationOptionsPanel
    tradingOptionsPanel = new TradingOptionsPanel
    graphingOptionsPanel = new GraphingOptionsPanel
    paramPanel.add(stockGenerationOptionsPanel, BorderLayout.NORTH)
    paramPanel.add(tradingOptionsPanel, BorderLayout.CENTER)
    paramPanel.add(graphingOptionsPanel, BorderLayout.SOUTH)
    paramPanel
  }

  override protected def ok(): Unit = {
    super.ok()
    val simulator = getSimulator.asInstanceOf[TradingSimulator]
    simulator.setOptions(stockGenerationOptionsPanel.getOptions, tradingOptionsPanel.getOptions, graphingOptionsPanel.getOptions)
  }
}