// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.rendering

import com.barrybecker4.expression.TreeNode
import com.barrybecker4.simulation.lsystem.model.expression.{LExpressionParser, LTreeSerializer}
import com.barrybecker4.ui.renderers.OfflineGraphics
import java.awt.Color
import java.awt.Dimension
import java.awt.image.BufferedImage

import com.barrybecker4.simulation.lsystem.model.expression.LToken._

import com.barrybecker4.common.geometry.{IntLocation, Location}
import com.barrybecker4.simulation.lsystem.Panable



/**
  * Everything we need to know to compute and draw the Lyndonmayer-System tree.
  * Should make the tree automatically center.
  * @author Barry Becker
  */
object LSystemRenderer {
  private val LENGTH = 1.0
  private val BG_COLOR = new Color(0, 30, 10)
}

class LSystemRenderer(initialWidth: Int, initialHeight: Int,
                      initialExpression: String, initialNumIterations: Int,
                      initialAngleInc: Double,
                      initialScale: Double, initialScaleFactor: Double) extends Panable {

  private var width = initialWidth
  private var height = initialHeight
  private var numIterations = initialNumIterations
  private var scale = initialScale
  private var scaleFactor = initialScaleFactor
  private var expression: String = _
  private val cmap = new DepthColorMap
  private var root: TreeNode = _
  private var angleIncrement: Double = _
  private var offset: Location = IntLocation(0, 0)
  private val parser = new LExpressionParser
  private val serializer = new LTreeSerializer
  private var needsRender: Boolean = true
  private var offlineGraphics: OfflineGraphics = _

  setExpression(initialExpression)
  setDimensions(initialWidth, initialHeight)
  setAngleInc(initialAngleInc)

  def getWidth: Int = width
  def getHeight: Int = height
  def getImage: BufferedImage = offlineGraphics.getOfflineImage.get
  def getSerializedExpression: String = serializer.serialize(root)

  def setDimensions(w: Int, h:Int): Unit = {
    width = w
    height = h
    offlineGraphics = new OfflineGraphics(new Dimension(width, height), LSystemRenderer.BG_COLOR)
    needsRender = true
  }

  def setExpression(exp: String): Unit = {
    expression = exp
    try root = parser.parseToTree(expression)
    catch {
      case e: Exception =>
        throw new IllegalArgumentException(e.getMessage, e)
    }
    needsRender = true
  }

  def setNumIterations(num: Int): Unit = {
    numIterations = num
    needsRender = true
  }

  def setAngleInc(ang: Double): Unit = {
    angleIncrement = ang * Math.PI / 180
    needsRender = true
  }

  def setScale(s: Double): Unit = {
    scale = s
    needsRender = true
  }

  def setScaleFactor(sf: Double): Unit = {
    scaleFactor = sf
    needsRender = true
  }

  def incrementOffset(incrementAmount: Location): Unit = {
    offset = offset.incrementOnCopy(incrementAmount)
    needsRender = true
    //render() ??
  }

  /** draw the tree */
  def render(): Unit = {
    if (needsRender) {
      offlineGraphics.clear()
      offlineGraphics.setColor(Color.RED)
      val initialPosition =
        new OrientedPosition(width / 2.0 + offset.getX, height / 8.0 - height + offset.getY, Math.PI / 2.0)
      val length = LSystemRenderer.LENGTH * width / 10.0
      drawTree(initialPosition, length, root, numIterations, 0)
      needsRender = false
    }
  }

  /**
    * Draw the tree recursively.
    * @param pos the position and angle in radians that the turtle graphics used when rotating '+' or '-'
    */
  private def drawTree(pos: OrientedPosition, length: Double, tree: TreeNode, numIterations: Int, depth: Int): Unit = {
    for (child <- tree.children) {
      if (child.hasParens) drawTree(new OrientedPosition(pos), scaleFactor * length, child, numIterations, depth + 1)
      else {
        val baseExp = child.getData
        for (i <- 0 until baseExp.length) {
          processSymbol(length, numIterations, pos, baseExp.substring(i, i + 1), depth)
        }
      }
    }
  }

  /** note: current position is changed by the processing of the symbol */
  private def processSymbol(length: Double, numIterations: Int, currentPos: OrientedPosition, c: String, depth: Int): Unit = {
    if (c == F.symbol) if (numIterations > 0) drawTree(currentPos, length, root, numIterations - 1, depth)
    else drawF(currentPos, length, depth)
    else if (c == MINUS.symbol) currentPos.angle -= angleIncrement
    else if (c == PLUS.symbol) currentPos.angle += angleIncrement
    else throw new IllegalStateException("Unexpected char: " + c)
  }

  private def drawF(pos: OrientedPosition, length: Double, num: Int): Unit = {
    val startX = pos.x.toInt
    val startY = -pos.y.toInt
    pos.x += scale * length * Math.cos(pos.angle)
    pos.y += scale * length * Math.sin(pos.angle)
    val stopX = pos.x.toInt
    val stopY = -pos.y.toInt
    offlineGraphics.setColor(cmap.getColorForValue(num))
    offlineGraphics.drawLine(startX, startY, stopX, stopY)
    val radius = (scale * (length - 0.4) / 10).toInt
    offlineGraphics.fillCircle(stopX, stopY, radius)
  }
}
