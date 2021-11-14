// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.json


case class Samples(
  overlapping: Array[Overlapping],
  simpletiled: Array[SimpleTiled]) {

  def all(): Seq[CommonModel] = {
    var toReturn = Seq[CommonModel]()

    if (overlapping != null) {
      for (it <- overlapping)
        toReturn :+= it
    }

    if (simpletiled != null) {
      for (it <- simpletiled)
        toReturn :+= it
    }

    toReturn
  }
}
