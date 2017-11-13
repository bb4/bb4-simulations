// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.model.runner

import com.barrybecker4.common.math.function.Function
import java.util

import scala.collection.mutable.{ArrayBuffer, ListBuffer}


/**
  * A collection of stock market series to show.
  * Just keeps track of the last N series.
  * @author Barry Becker
  */
class StockSeries(var maxNum: Int) extends ArrayBuffer[Function] {

  def append(func: Function): Unit = {
    super.append(func)
    if (size > maxNum) remove(0)
  }

  def clear(newNumeriesToKeep: Int): Unit = {
    maxNum = newNumeriesToKeep
    super.clear()
  }
}
