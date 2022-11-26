// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.voronoi.algorithm.model.voronoi

import com.barrybecker4.simulation.voronoi.algorithm.model.voronoi.Point


abstract class ArcKey extends Comparable[ArcKey] {
  
  protected def getLeft: Point
  protected def getRight: Point

  override def compareTo(that: ArcKey): Int = {
    val myLeft = this.getLeft
    val myRight = this.getRight
    val yourLeft = that.getLeft
    val yourRight = that.getRight
    
    // If one arc contains the query then we'll say that they're the same
    if (((that.getClass eq classOf[ArcQuery]) || (this.getClass eq classOf[ArcQuery])) && 
       ((myLeft.x <= yourLeft.x && myRight.x >= yourRight.x) || (yourLeft.x <= myLeft.x && yourRight.x >= myRight.x))) return 0
    if (myLeft.x == yourLeft.x && myRight.x == yourRight.x) return 0
    if (myLeft.x >= yourRight.x) return 1
    if (myRight.x <= yourLeft.x) return -1
    Point.midpoint(myLeft, myRight).compareTo(Point.midpoint(yourLeft, yourRight))
  }
}
