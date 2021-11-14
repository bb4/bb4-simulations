// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.json

import com.barrybecker4.simulation.waveFunctionCollapse.model.OverlappingImageParams


case class Overlapping(
  n: String,
  ground: String,
  height: String,
  limit: String,
  name: String,
  periodic: String,
  periodicInput: String,
  screenshots: String,
  symmetry: String,
  width: String) extends CommonModel {

  override def getName: String = if (name == null) "" else name
  def getHeight: Int = if (height == null) 48 else height.toInt
  def getWidth: Int = if (width == null) 48 else width.toInt
  override def getLimit: Int = if (limit == null) 0 else limit.toInt
  def getPeriodic: Boolean = if (periodic == null) true else periodic.toBoolean
  override def getScreenshots: Int = if (screenshots == null) 1 else screenshots.toInt
  def getImageParams: OverlappingImageParams = OverlappingImageParams(
    if (n == null) 2 else n.toInt,
    if (symmetry == null) 8 else symmetry.toInt,
    if (periodicInput == null) true else periodicInput.toBoolean,
    if (ground == null) 0 else ground.toInt
  )
}
