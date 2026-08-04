package com.barrybecker4.simulation.fractalexplorer.algorithm

import com.barrybecker4.common.app.AppContext

/**
  * Type of fractal generation algorithm to use.
  *
  * @author Barry Becker
  */
enum AlgorithmEnum:

  case MANDELBROT, JULIA

  def getLabel: String = AppContext.getLabel(toString)

  def createInstance(): FractalAlgorithm =
    if this == AlgorithmEnum.MANDELBROT then new MandelbrotAlgorithm()
    else new JuliaAlgorithm()


object AlgorithmEnum:
  val VALUES: Array[AlgorithmEnum] = values
