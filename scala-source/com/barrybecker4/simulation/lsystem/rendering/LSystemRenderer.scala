// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem.rendering

import com.barrybecker4.common.expression.TreeNode
import com.barrybecker4.simulation.lsystem.model.expression.{LExpressionParser, LTreeSerializer}
import com.barrybecker4.ui.renderers.OfflineGraphics
import java.awt.Color
import java.awt.Dimension
import java.awt.image.BufferedImage

import com.barrybecker4.simulation.lsystem.model.expression.LToken._
import java.util

import scala.collection.JavaConverters._


/**
  * Everything we need to know to compute the l-System tree.
  * Should make the tree automatically center.
  * @author Barry Becker
  */
object LSystemRenderer {
  private val LENGTH = 1.0
  private val BG_COLOR = new Color(0, 30, 10)
}

class LSystemRenderer(val width: Int, val height: Int, val expression: String, var numIterations: Int,
                      val angleInc: Double, var scale: Double, var scaleFactor: Double) {
  private val cmap = new DepthColorMap
  private var root: TreeNode = _
  private var angleIncrement = angleInc * Math.PI / 180
  val parser = new LExpressionParser
  val serializer = new LTreeSerializer
  try
    root = parser.parse(expression)
  catch {
    case e: Exception =>
      throw new IllegalArgumentException(e.getMessage, e)
  }
  final private var offlineGraphics= new OfflineGraphics(new Dimension(width, height), LSystemRenderer.BG_COLOR)

  def getWidth: Int = width
  def getHeight: Int = height
  def reset() {}
  def getImage: BufferedImage = offlineGraphics.getOfflineImage
  def getSerializedExpression: String = serializer.serialize(root)

  /** draw the tree */
  def render(): Unit = {
    offlineGraphics.setColor(Color.RED)
    val initialPosition = new OrientedPosition(width / 2.0, height / 8.0 - height, Math.PI / 2.0)
    val length = LSystemRenderer.LENGTH * width / 10.0
    drawTree(initialPosition, length, root, numIterations, 0)
  }

  /**
    * Draw the tree recursively.
    * @param pos the position and angle in radians that the turtle graphics used when rotating '+' or '-'
    */
  private def drawTree(pos: OrientedPosition, length: Double, tree: TreeNode, numIterations: Int, depth: Int){
    val list = new util.LinkedList[TreeNode](tree.children)
    for (child <- list.asScala) {
      if (child.hasParens) drawTree(new OrientedPosition(pos), scaleFactor * length, child, numIterations, depth + 1)
      else {
        val baseExp = child.getData
        for (i <- 0 until baseExp.length) {
          processSymbol(length, numIterations, pos, baseExp.charAt(i), depth)
        }
      }
    }
  }

  /** note: current position is changed by the processing of the symbol */
  private def processSymbol(length: Double, numIterations: Int, currentPos: OrientedPosition, c: Char, depth: Int) {
    if (c == F.symbol) if (numIterations > 0) drawTree(currentPos, length, root, numIterations - 1, depth)
    else drawF(currentPos, length, depth)
    else if (c == MINUS.symbol) currentPos.angle -= angleIncrement
    else if (c == PLUS.symbol) currentPos.angle += angleIncrement
    else throw new IllegalStateException("Unexpected char: " + c)
  }

  private def drawF(pos: OrientedPosition, length: Double, num: Int) {
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
