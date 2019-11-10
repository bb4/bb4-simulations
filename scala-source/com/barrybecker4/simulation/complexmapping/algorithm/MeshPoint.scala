/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.complexmapping.algorithm

import com.barrybecker4.common.math.ComplexNumber
import com.barrybecker4.simulation.complexmapping.algorithm.functions.ComplexFunction
import javax.vecmath.Point2d

case class MeshPoint(pt: Point2d, value: Double) {

  def this(x: Double, y: Double, value: Double) {
    this(new Point2d(x, y), value)
  }

  def x: Double = pt.x
  def y: Double = pt.y

  def transform(func: ComplexFunction): MeshPoint = {
    val transformed = func.compute(ComplexNumber(pt.x, pt.y))
    MeshPoint(new Point2d(transformed.real, transformed.imaginary), value)
  }

}
