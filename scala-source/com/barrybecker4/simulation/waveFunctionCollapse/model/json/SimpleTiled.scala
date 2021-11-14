// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.json


case class SimpleTiled(
  width: String,
  height: String,
  black: String,
  limit: String,
  name: String,
  periodic: String,
  screenshots: String,
  subset: String
) extends CommonModel {

  override def getName: String = 
    if (name == null) "" else name
    
  def getHeight: Int = 
    if (height == null) 400 else height.toInt
    
  def getWidth: Int = 
    if (width == null) 400 else width.toInt
    
  override def getLimit: Int = 
    if (limit == null) 0 else limit.toInt
    
  def getPeriodic: Boolean = if (periodic == null) true else periodic.toBoolean
  override def getScreenshots: Int = if (screenshots == null) 1 else screenshots.toInt
  def getBlack: Boolean = if (black == null) false else black.toBoolean
  def getSubset: String = subset
}
