// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.model.parts

import com.barrybecker4.simulation.trebuchet.model.Variables
import com.barrybecker4.simulation.trebuchet.model.parts.Base.{BASE_WIDTH, BASE_X, BASE_Y, STRUT_BASE_HALF_WIDTH, STRUT_BASE_X}

import java.awt.*


object Base {
  private val BASE_X = 80
  private val BASE_Y = 150
  private val BASE_WIDTH = 400
  private val STRUT_BASE_HALF_WIDTH = 50
  private val STRUT_BASE_X = 300
}

class Base(variables: Variables)  {
  def getBaseX: Int = BASE_X
  def getBaseY: Int = BASE_Y
  def getWidth: Int = BASE_WIDTH
  def getStrutBaseHalfWidth: Int = STRUT_BASE_HALF_WIDTH
  def getStrutBaseX: Int = STRUT_BASE_X
}
