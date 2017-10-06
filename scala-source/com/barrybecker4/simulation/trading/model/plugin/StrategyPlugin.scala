// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.model.plugin

/**
  * @author Barry Becker
  */
trait StrategyPlugin {

  def name: String
  def description: String
}
