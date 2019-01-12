/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer.algorithm

import java.awt.Image

import com.barrybecker4.simulation.common.RectangularModel
import com.barrybecker4.simulation.common.rendering.ModelImage
import com.barrybecker4.ui.util.ColorMap


object FractalModel {
  private val FIXED_SIZE: Int = 200
}

/**
  * A big matrix to hold the resulting values, and the image that gets rendered from it.
  * @author Barry Becker
  */
class FractalModel extends RectangularModel {
  private var image: ModelImage = _
  private var values: Array[Array[Double]] = _
  private val colorMap: ColorMap = new FractalColorMap()
  private var lastRow: Int = 0
  private var currentRow: Int = 0

  initialize(FractalModel.FIXED_SIZE, FractalModel.FIXED_SIZE)

  /** Changing the size of the model will clear all current results.
    * Only resize if the new dimensions are different to prevent clearing results unnecessarily.
    * @return true if the size changed
    */
  def setSize(width: Int, height: Int): Unit = {
    if (width != getWidth || height != getHeight)
      initialize(width, height)
  }

  def updateImage(): Unit = {
    image.updateImage(lastRow, currentRow)
  }

  private def initialize(width: Int, height: Int) {
    values = Array.ofDim[Double](width, height)
    image = new ModelImage(this, colorMap)
    setCurrentRow(0)
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
  def getCurrentRow: Int = currentRow
  def getLastRow: Int = lastRow
  def getImage: Image = image.getImage
  def getColorMap: ColorMap = colorMap

  /** Set the row that we have calculated up to.
    * @param row new row
    */
  def setCurrentRow(row: Int) {
    lastRow = currentRow
    currentRow = row
    if (currentRow == 0)
      lastRow = 0
  }
}
