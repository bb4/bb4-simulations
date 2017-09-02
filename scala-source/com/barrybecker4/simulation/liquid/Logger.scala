// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid

import com.barrybecker4.common.app.ILog
import com.barrybecker4.ui.dialogs.OutputWindow
import com.barrybecker4.ui.util.Log


/**
  * Singleton instance of logger.
  * @author Barry Becker
  */
object Logger {

  /** for debugging */
  val LOG_LEVEL = 0

  /** The singleton instance */
  private var logger: Log = null

  def getInstance: ILog = {
    if (logger == null) {
      logger = new Log(new OutputWindow("Log", null))
      logger.setDestination(Log.LOG_TO_WINDOW)
    }
    logger
  }

  def log(level: Int, msg: String): Unit = {
    getInstance.println(level, LOG_LEVEL, msg)
  }
}

class Logger private()
