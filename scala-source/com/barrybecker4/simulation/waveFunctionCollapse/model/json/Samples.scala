/*
 * Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */
package com.barrybecker4.simulation.waveFunctionCollapse.model.json

import com.google.gson.annotations.SerializedName


case class Samples(
  @SerializedName("overlapping")
  val overlapping: List[Overlapping],
  @SerializedName("simpletiled")
  val simpletiled: List[Simpletiled]
) {
  def all(): Seq[CommonModel] = {
    var toReturn = Seq[CommonModel]()

    for (it <- overlapping)
      toReturn :+= it

    for (it <- simpletiled)
      toReturn :+= it

    toReturn
  }
}