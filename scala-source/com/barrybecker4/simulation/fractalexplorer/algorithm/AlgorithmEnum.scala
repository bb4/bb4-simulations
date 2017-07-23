package com.barrybecker4.simulation.fractalexplorer.algorithm

import com.barrybecker4.common.app.AppContext

case object MANDELBROT extends AlgorithmEnum
case object JULIA extends AlgorithmEnum

/**
  * Type of fractal generation algorithm to use.
  *
  * @author Barry Becker
  */
sealed trait AlgorithmEnum  {

  def getLabel: String = AppContext.getLabel(toString)

  /**
    * Create an instance of the algorithm given the controller and a refreshable.
    */
  def createInstance(model: FractalModel): FractalAlgorithm = {
    this match {
      case MANDELBROT => new MandelbrotAlgorithm(model)
      case JULIA => new JuliaAlgorithm(model)
    }
  }

  def ordinal(): Int = AlgorithmEnum.VALUES.indexOf(this)
}

object AlgorithmEnum {
  val VALUES: Array[AlgorithmEnum] = Array(MANDELBROT, JULIA)
}

