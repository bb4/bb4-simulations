/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer.algorithm

import com.barrybecker4.simulation.common.RectangularModel

/**
  * Nothing but a big matrix to hold the resulting values.
  * @author Barry Becker
  */
object FractalModel {
  private val FIXED_SIZE: Int = 200
}

class FractalModel extends RectangularModel {
  private var values: Array[Array[Double]] = _
  private var lastRow: Int = 0
  private var currentRow: Int = 0

  initialize(FractalModel.FIXED_SIZE, FractalModel.FIXED_SIZE)

  /**
    * We can change the size of the model, but doing so will clear all current results.
    * We only resize if the new dimensions are different than we had to prevent clearing results unnecessarily.
    */
  def setSize(width: Int, height: Int) {
    if (width != getWidth || height != getHeight)
      initialize(width, height)
  }

  private def initialize(width: Int, height: Int) {
    values = Array.ofDim[Double](width, height) //new Array[Array[Double]](width, height)
  }

  def setValue(x: Int, y: Int, value: Double) {
    if (x < getWidth && y < getHeight) values(x)(y) = value
  }

  def getValue(x: Int, y: Int): Double = {
    if (x < 0 || x >= getWidth || y < 0 || y >= getHeight) 0
    else values(x)(y)
  }

  def getWidth: Int = values.length
  def getHeight: Int = values(0).length
  def getAspectRatio: Double = getWidth / getHeight
  def isDone: Boolean = currentRow >= getHeight

  /**
    * Set the row that we have calculated up to.
    * @param row new row
    */
  def setCurrentRow(row: Int) {
    lastRow = currentRow
    currentRow = row
    if (currentRow == 0)
      lastRow = 0
  }

  def getCurrentRow: Int = currentRow
  def getLastRow: Int = lastRow
}
