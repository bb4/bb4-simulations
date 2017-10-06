// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.model.runner

import com.barrybecker4.common.math.function.Function
import java.util


/**
  * A collection of stock market series to show.
  * Just keeps track of the last N series.
  * @author Barry Becker
  */
class StockSeries(var maxNum: Int) extends util.LinkedList[Function] {
  override def add(func: Function): Boolean = {
    val success = super.add(func)
    if (size > maxNum) remove(0)
    success
  }

  def clear(newNumeriesToKeep: Int): Unit = {
    maxNum = newNumeriesToKeep
    super.clear()
  }
}
