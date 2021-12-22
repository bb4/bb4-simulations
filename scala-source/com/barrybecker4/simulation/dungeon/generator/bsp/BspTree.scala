// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.dungeon.generator.bsp



case class BspTree[T](root: BspNode[T]) {

  def getChildren: Set[T] = root.getChildren
  
}
