// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.json

import com.google.gson.annotations.SerializedName


case class Overlapping(
  @(SerializedName @scala.annotation.meta.field)("-N") n: String,
  @(SerializedName @scala.annotation.meta.field)("-ground") ground: String,
  @(SerializedName @scala.annotation.meta.field)("-height") height: String,
  @(SerializedName @scala.annotation.meta.field)("-limit") limit: String,
  @(SerializedName @scala.annotation.meta.field)("-name") name: String,
  @(SerializedName @scala.annotation.meta.field)("-periodic") periodic: String,
  @(SerializedName @scala.annotation.meta.field)("-periodicInput") periodicInput: String,
  @(SerializedName @scala.annotation.meta.field)("-screenshots") screenshots: String,
  @(SerializedName @scala.annotation.meta.field)("-symmetry") symmetry: String,
  @(SerializedName @scala.annotation.meta.field)("-width") width: String) extends CommonModel {

  override def getName: String = if (name == null) "" else name
  def getN: Int = if (n == null) 2 else n.toInt
  def getHeight: Int = if (height == null) 48 else height.toInt
  def getWidth: Int = if (width == null) 48 else width.toInt
  override def getLimit: Int = if (limit == null) 0 else limit.toInt
  def getPeriodic: Boolean = if (periodic == null) true else periodic.toBoolean
  def getPeriodicInput: Boolean = if (periodicInput == null) true else periodicInput.toBoolean
  override def getScreenshots: Int = if (screenshots == null) 1 else screenshots.toInt
  def getSymmetry: Int = if (symmetry == null) 8 else symmetry.toInt
  def getGround: Int = if (ground == null) 0 else ground.toInt
}
