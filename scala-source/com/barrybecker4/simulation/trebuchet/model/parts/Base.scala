// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.model.parts

import com.barrybecker4.simulation.trebuchet.model.parts.Base.{BASE_HEIGHT, BASE_RAMP_HEIGHT, BASE_WIDTH, BASE_X, BASE_Y, STRUT_BASE_HALF_WIDTH, STRUT_BASE_X}

import java.awt.*


object Base {
  private val BASE_X = 8.0
  // Y as measured from bottom
  private val BASE_Y = 15.0
  private val BASE_WIDTH = 40.0
  private val BASE_HEIGHT = 1.0
  private val BASE_RAMP_HEIGHT = 0.4
  private val STRUT_BASE_HALF_WIDTH = 5.0
  private val STRUT_BASE_X = 30.0
}

class Base()  {
  def getBaseX: Double = BASE_X
  def getBaseY: Double = BASE_Y
  def getWidth: Double = BASE_WIDTH
  def getHeight: Double = BASE_HEIGHT
  def getRampHeight: Double = BASE_RAMP_HEIGHT
  def getStrutBaseHalfWidth: Double = STRUT_BASE_HALF_WIDTH
  def getStrutBaseX: Double = STRUT_BASE_X
}
