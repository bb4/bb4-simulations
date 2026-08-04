/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.reactiondiffusion.algorithm.configuration


/**
  * Different sorts of initial conditions for the Gray-Scott grid.
  * @author Barry Becker
  */
enum Initializer(name: String, initialU: Double = Initializer.INITIAL_U, initialV: Double = Initializer.INITIAL_V):

  case CirclesInitializer extends Initializer("Circles", 0.5, 0.25)
  case InterlockedSquaresInitializer extends Initializer("Interlocking squares", 0.5, 0.25)
  case RingInitializer extends Initializer("Interlocking rings", 0.5, 0.25)

  def initialize(grid: InitializableGrid, width: Int, height: Int): Unit = this match
    case CirclesInitializer =>
      stampSquare(0, 0, width, height, 1.0, 0, grid)
      val h4 = height / 4
      stampCircle(h4 + 40, h4 + 30, h4, initialU, initialV, grid)
      val w7 = width / 7
      val h5 = height / 5
      stampCircle(4 * w7, 3 * h5, w7, initialU, initialV, grid)

    case InterlockedSquaresInitializer =>
      stampSquare(0, 0, width, height, 1.0, 0, grid)
      val w3 = width / 3
      val h3 = height / 3
      stampSquare(w3, h3, w3, h3, initialU, initialV, grid)
      val w7 = width / 7
      val h5 = height / 5
      stampSquare(5 * w7, 3 * h5, w7, h5, initialU, initialV, grid)

    case RingInitializer =>
      stampSquare(0, 0, width, height, 1.0, 0, grid)
      val w2 = width / 2
      val h3 = height / 3
      stampRing(w2, h3 + 10, w2 / 2, 14, initialU, initialV, grid)
      val w7 = width / 7
      val h5 = height / 5
      stampRing(4 * w7, 3 * h5, h5 / 2 + 10, 15, initialU, initialV, grid)

  override def toString: String = name

  /** Place a square of chemicals with the initial concentrations. */
  private def stampSquare(startX: Int, startY: Int,
                          width: Int, height: Int,
                          initialU: Double, initialV: Double,
                          grid: InitializableGrid): Unit =
    for x <- 0 until width do
      for y <- 0 until height do
        grid.initializePoint(startX + x, startY + y, initialU, initialV)

  /** Place an annulus of chemicals with the initial concentrations. */
  private def stampRing(centerX: Int, centerY: Int, radius: Int, thickness: Int,
                        initialU: Double, initialV: Double,
                        grid: InitializableGrid): Unit =
    val thicknessD2 = thickness / 2
    val rad = radius + thicknessD2
    val xMin = centerX - rad
    val xMax = centerX + rad
    val yMin = centerY - rad
    val yMax = centerY + rad

    for x <- xMin to xMax do
      for y <- yMin to yMax do
        val dx = x - centerX
        val dy = y - centerY
        val r = Math.sqrt(dx * dx + dy * dy)
        if r > radius - thicknessD2 && r <= radius + thicknessD2 then
          grid.initializePoint(x, y, initialU, initialV)

  /** Place a filled disk of chemicals with the initial concentrations. */
  private def stampCircle(centerX: Int, centerY: Int, radius: Int,
                          initialU: Double, initialV: Double,
                          grid: InitializableGrid): Unit =
    val xMin = centerX - radius
    val xMax = centerX + radius
    val yMin = centerY - radius
    val yMax = centerY + radius

    for x <- xMin to xMax do
      for y <- yMin to yMax do
        val dx = x - centerX
        val dy = y - centerY
        val r = Math.sqrt(dx * dx + dy * dy)
        if r <= radius then
          grid.initializePoint(x, y, initialU, initialV)


object Initializer:
  val INITIAL_U: Double = 0.5
  val INITIAL_V: Double = 0.25

  val DEFAULT_INITIALIZER: Initializer = CirclesInitializer
  val VALUES: Array[Initializer] = Array(CirclesInitializer, InterlockedSquaresInitializer, RingInitializer)
