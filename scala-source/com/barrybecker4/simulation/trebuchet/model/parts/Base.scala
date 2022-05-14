// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.model.parts

import com.barrybecker4.simulation.trebuchet.model.parts.Base.{BASE_HEIGHT, BASE_RAMP_HEIGHT, BASE_WIDTH, BASE_X, BASE_Y, STRUT_BASE_HALF_WIDTH, STRUT_BASE_X}

import java.awt.*


object Base {
  private val BASE_X = 80
  // Y as measured from bottom
  private val BASE_Y = 150
  private val BASE_WIDTH = 400
  private val BASE_HEIGHT = 10
  private val BASE_RAMP_HEIGHT = 4
  private val STRUT_BASE_HALF_WIDTH = 50
  private val STRUT_BASE_X = 300
}

class Base()  {
  def getBaseX: Int = BASE_X
  def getBaseY: Int = BASE_Y
  def getWidth: Int = BASE_WIDTH
  def getHeight: Int = BASE_HEIGHT
  def getRampHeight: Int = BASE_RAMP_HEIGHT
  def getStrutBaseHalfWidth: Int = STRUT_BASE_HALF_WIDTH
  def getStrutBaseX: Int = STRUT_BASE_X
}
