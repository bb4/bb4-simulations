// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.json

import com.google.gson.annotations.SerializedName

case class SimpleTiled(
  @(SerializedName @scala.annotation.meta.field)("-width") width: String,
  @(SerializedName @scala.annotation.meta.field)("-height") height: String,
  @(SerializedName @scala.annotation.meta.field)("-black") black: String,
  @(SerializedName @scala.annotation.meta.field)("-limit") limit: String,
  @(SerializedName @scala.annotation.meta.field)("-name") name: String,
  @(SerializedName @scala.annotation.meta.field)("-periodic") periodic: String,
  @(SerializedName @scala.annotation.meta.field)("-screenshots") screenshots: String,
  @(SerializedName @scala.annotation.meta.field)("-subset") subset: String
) extends CommonModel {

  override def getName: String = if (name == null) "" else name
  def getHeight: Int = if (height == null) 10 else height.toInt
  def getWidth: Int = if (width == null) 10 else width.toInt
  override def getLimit: Int = if (limit == null) 0 else limit.toInt
  def getPeriodic: Boolean = if (periodic == null) true else periodic.toBoolean
  override def getScreenshots: Int = if (screenshots == null) 2 else screenshots.toInt
  def getBlack: Boolean = if (black == null) false else black.toBoolean
  def getSubset: String = subset

  override def toString: String = {
    val per = if (getPeriodic) "periodic" else ""
    val black = if (getBlack) "black" else ""
    val sub = if (subset == null) "" else subset.substring(0, Math.min(subset.length, 14))
    val lim = if (getLimit > 0) "lim=" + limit else ""
    s"$getName $per $black $sub $lim"
  }
}
