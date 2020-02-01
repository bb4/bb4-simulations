// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.model.runner

import com.barrybecker4.math.function.Function
import scala.collection.mutable.ArrayBuffer


/**
  * A collection of stock market series to show.
  * Just keeps track of the last N series.
  * @author Barry Becker
  */
class StockSeries(var maxNum: Int) extends ArrayBuffer[Function] {

  def appendSeries(func: Function): Unit = {
    super.append(func)
    if (size > maxNum) remove(0)
  }

  def clearSeries(newNumeriesToKeep: Int): Unit = {
    maxNum = newNumeriesToKeep
    super.clear()
  }
}
