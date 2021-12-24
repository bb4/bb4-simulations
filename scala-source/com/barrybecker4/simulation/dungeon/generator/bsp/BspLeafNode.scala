// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.bsp

import com.barrybecker4.simulation.dungeon.model.Room
import scala.collection.immutable.HashSet


case class BspLeafNode[T](data: T) extends BspNode[T] {

  def getChildren: Set[T] = HashSet(data)

}
