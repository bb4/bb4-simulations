/*
 * Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */

package com.barrybecker4.simulation.waveFunctionCollapse.model.json

import com.google.gson.annotations.SerializedName

case class Simpletiled(
  @SerializedName("-height")
  val height: String,
  @SerializedName("-black")
  val black: String,
  @SerializedName("-limit")
  val limit: String,
  @SerializedName("-name")
  val name: String,
  @SerializedName("-periodic")
  val periodic: String,
  @SerializedName("-screenshots")
  val screenshots: String,
  @SerializedName("-subset")
  val subset: String,
  @SerializedName("-width")
  val width: String
) extends CommonModel