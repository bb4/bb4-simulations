// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.config

import com.barrybecker4.common.geometry.IntLocation
import com.barrybecker4.common.geometry.Location
import com.barrybecker4.common.util.FileUtil
import com.barrybecker4.common.xml.DomUtil
import org.w3c.dom.Document
import org.w3c.dom.Node
import javax.vecmath.Vector2d
import java.net.URL


/**
  * Configuration constraints and initial conditions to use while running the simulation.
  * Loaded from a config file.
  * @author Barry Becker
  */
object Conditions {
  private val START = "start"
  private val STOP = "stop"
}

/**
  * @param configFile file defines the constraints and initial conditions.
  */
class Conditions(val configFile: String) { // use a default if null passed in.
  private var walls: Seq[Wall] = _
  val file: String = if (configFile == null) BASIC.fileName else configFile
  val url: URL = FileUtil.getURL(file)
  val document: Document = DomUtil.parseXML(url)
  DomUtil.printTree(document, 0)

  private var gridWidth = 0
  private var gridHeight = 0
  private var cellSize = .0
  private var gravity = .0
  /** keep the walls globally because we need to draw them each frame. */
  private var sources: List[Source] = _
  private var sinks: List[Region] = _
  private var initialLiquidRegions: List[Region]= _
  parseFromDocument(document)

  private def parseFromDocument(document: Document) = {
    val envRoot = document.getDocumentElement
    // environment element
    val children = envRoot.getChildNodes
    gridWidth = DomUtil.getAttribute(envRoot, "gridWidth").toInt
    gridHeight = DomUtil.getAttribute(envRoot, "gridHeight").toInt
    cellSize = DomUtil.getAttribute(envRoot, "cellSize").toDouble
    gravity = DomUtil.getAttribute(envRoot, "gravity").toDouble
    val num = children.getLength
    // #comment nodes are skipped
    for (i <- 0 until num)  {
      val n = children.item(i)
      val name = n.getNodeName
      //String name = DomUtil.getAttribute(n, "name");
      if ("walls" == name) parseWalls(n) else if ("liquid" == name) parseLiquidRegions(n)
    }
  }

  /**
    * parses xml of this form
    * <pre>
    * <walls>
    * <wall start="15,10" stop="15,15" />
    * ...
    * </walls>
    * </pre>
    */
  private def parseWalls(wallsNode: Node) = {

    val children = wallsNode.getChildNodes
    val num = children.getLength

    walls = for (i <- 0 until num) yield {
       val n = children.item(i)
       new Wall(parseLocation(n, Conditions.START), parseLocation(n, Conditions.STOP))
     }
  }

  /**
    * Parses xml of this form
    * <pre>
    * <liquid>
    * <source start="5, 6" velocity="30.0,0.0" />
    * <region start="1,25" stop="20, 31" />
    * </liquid>
    * </pre>
    */
  private def parseLiquidRegions(wallsNode: Node) = {
    sources = List[Source]()
    sinks = List[Region]()
    initialLiquidRegions = List[Region]()
    val children = wallsNode.getChildNodes
    val num = children.getLength
    for (i <- 0 until num) {
      val n = children.item(i)
      val name = n.getNodeName
      if ("source" == name) {
        val source = parseSource(n)
        sources +:= source
      }
      else if ("sink" == name) {
        val sink = parseRegion(n)
        sinks +:= sink
      }
      else if ("region" == name) {
        val region = parseRegion(n)
        initialLiquidRegions +:= region
      }
    }
  }

  private def parseSource(sourceNode: Node) = {
    val startTime = DomUtil.getAttribute(sourceNode, "startTime", "0").toDouble
    val duration = DomUtil.getAttribute(sourceNode, "duration", "-1").toDouble
    val repeatInterval = DomUtil.getAttribute(sourceNode, "repeatInterval", "-1").toDouble
    new Source(parseLocation(sourceNode, Conditions.START),
      parseLocation(sourceNode, Conditions.STOP), parseVector(sourceNode, "velocity"),
      startTime, duration, repeatInterval)
  }

  private def parseRegion(node: Node) = new Region(parseLocation(node, Conditions.START), parseLocation(node, Conditions.STOP))

  private def parseLocation(n: Node, locationAttribute: String): Location = {
    val locationString = DomUtil.getAttribute(n, locationAttribute, null)
    if (locationString == null) return null
    val commaPos = locationString.indexOf(",")
    assert(commaPos != -1)
    val xPos = locationString.substring(0, commaPos).toInt
    val yPos = locationString.substring(commaPos + 1).toInt
    // verify that it is within the bounds of the grid
    assert(xPos <= this.gridWidth && xPos > 0, "invalid xpos = " + xPos)
    assert(yPos <= this.gridHeight && yPos > 0, "invalid ypos = " + yPos)
    IntLocation(yPos, xPos)
  }

  private def parseVector(n: Node, vecAttribute: String) = {
    val vecString = DomUtil.getAttribute(n, vecAttribute)
    val commaPos = vecString.indexOf(",")
    assert(commaPos != -1)
    val x = vecString.substring(0, commaPos).toDouble
    val y = vecString.substring(commaPos + 1).toDouble
    new Vector2d(x, y)
  }

  def getWalls: Seq[Wall] = walls
  def getSources: List[Source] = sources
  def getSinks: List[Region] = sinks
  def getInitialLiquidRegions: List[Region] = initialLiquidRegions

  def getGridWidth: Int = gridWidth
  def getGridHeight: Int = gridHeight
  def getCellSize: Double = cellSize
  def getGravity: Double = gravity
}
