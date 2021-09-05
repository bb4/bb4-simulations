// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.waveFunctionCollapse.utils

object FileUtil {
  val CURRENT_DIR: String = System.getProperty("user.dir").replace("\\", "/") + "/"
  private val RELATIVE_DIR = "scala-source/com/barrybecker4/simulation/waveFunctionCollapse/"
  val BASE_DIR: String = CURRENT_DIR + RELATIVE_DIR
}
