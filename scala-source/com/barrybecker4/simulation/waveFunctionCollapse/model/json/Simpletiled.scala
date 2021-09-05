/* Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.waveFunctionCollapse.model.json

import com.google.gson.annotations.SerializedName

case class Simpletiled(
  @(SerializedName @scala.annotation.meta.field)("-height") height: String,
  @(SerializedName @scala.annotation.meta.field)("-black") black: String,
  @(SerializedName @scala.annotation.meta.field)("-limit") limit: String,
  @(SerializedName @scala.annotation.meta.field)("-name") name: String,
  @(SerializedName @scala.annotation.meta.field)("-periodic") periodic: String,
  @(SerializedName @scala.annotation.meta.field)("-screenshots") screenshots: String,
  @(SerializedName @scala.annotation.meta.field)("-subset") subset: String,
  @(SerializedName @scala.annotation.meta.field)("-width") width: String
) extends CommonModel {

  def getName: String = if (name == null) "" else name
  def getHeight: Int = if (height == null) 10 else height.toInt
  def getWidth: Int = if (width == null) 10 else width.toInt
  def getLimit: Int = if (limit == null) 0 else limit.toInt
  def getPeriodic: Boolean = if (periodic == null) true else periodic.toBoolean
  def getScreenshots: Int = if (screenshots == null) 2 else screenshots.toInt
  def getBlack: Boolean = if (black == null) false else black.toBoolean
  def getSubset: String = subset //if (subset == null) "Knots" else subset
}
