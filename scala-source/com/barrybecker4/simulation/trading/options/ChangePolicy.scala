// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.options

/**
  * When the market changes by "changePercent" %,
  * then buy or sell (determined by sign) "transactPercent" % of current invested dollars.
  * @param changePercent the percent increase or decrease in the market.  (0 - 1.0)
  * @param transactPercent Either the percent of current investment to sell, or percent of current reserve to buy more with. (0 - 1.0)
  * @author Barry Becker
  */
class ChangePolicy(val changePercent: Double, val transactPercent: Double)
