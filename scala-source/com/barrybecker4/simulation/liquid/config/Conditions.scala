// Copyright by Barry G. Becker, 2016-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.config

import com.barrybecker4.common.geometry.IntLocation
import com.barrybecker4.common.geometry.Location
import com.barrybecker4.common.util.FileUtil
import com.barrybecker4.simulation.liquid.config.json.{EnvironmentJson, LiquidJson, RegionJson, SourceJson, WallJson}
import com.google.gson.Gson
import javax.vecmath.Vector2d

import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

import scala.compiletime.uninitialized


/**
  * Configuration constraints and initial conditions to use while running the simulation.
  * Loaded from a JSON config file.
  * @author Barry Becker
  */
object Conditions {
  private val GSON = new Gson()
}

/**
  * @param configFile file defines the constraints and initial conditions.
  */
class Conditions(val configFile: String) { // use a default if null passed in.
  private var walls: Seq[Wall] = uninitialized
  val file: String = if (configFile == null) ConfigurationEnum.BASIC.fileName else configFile
  private val url = FileUtil.getURL(file)
  private val env: EnvironmentJson = {
    val reader = new InputStreamReader(url.openStream(), StandardCharsets.UTF_8)
    try Conditions.GSON.fromJson(reader, classOf[EnvironmentJson])
    finally reader.close()
  }

  private var gridWidth = 0
  private var gridHeight = 0
  private var cellSize = .0
  private var gravity = .0
  /** keep the walls globally because we need to draw them each frame. */
  private var sources: List[Source] = uninitialized
  /** Parsed from config; reserved until sink physics is implemented. */
  private var sinks: List[Region] = uninitialized
  private var initialLiquidRegions: List[Region] = uninitialized
  parseFromJson(env)

  private def parseFromJson(env: EnvironmentJson): Unit = {
    gridWidth = env.gridWidth
    gridHeight = env.gridHeight
    cellSize = env.cellSize
    gravity = env.gravity
    walls = Option(env.walls).getOrElse(Array.empty[WallJson]).map(parseWall).toSeq
    val liquid = Option(env.liquid).getOrElse(LiquidJson(null, null, null))
    sources = Option(liquid.sources).getOrElse(Array.empty[SourceJson]).map(parseSource).toList
    sinks = Option(liquid.sinks).getOrElse(Array.empty[RegionJson]).map(parseRegion).toList
    initialLiquidRegions = Option(liquid.regions).getOrElse(Array.empty[RegionJson]).map(parseRegion).toList
  }

  private def parseWall(w: WallJson) =
    new Wall(parseLocation(w.start), parseLocation(w.stop))

  private def parseSource(s: SourceJson) = {
    val startTime = if (s.startTime == null) 0.0 else s.startTime.doubleValue
    val duration = if (s.duration == null) -1.0 else s.duration.doubleValue
    val repeatInterval = if (s.repeatInterval == null) -1.0 else s.repeatInterval.doubleValue
    new Source(parseLocation(s.start), parseLocation(s.stop), parseVector(s.velocity),
      startTime, duration, repeatInterval)
  }

  private def parseRegion(r: RegionJson) =
    new Region(parseLocation(r.start), parseLocation(r.stop))

  /**
    * @param coords `[x, y]` from JSON; stored as [[IntLocation]](y, x) to match prior XML parsing.
    */
  private def parseLocation(coords: Array[Int]): Location = {
    if (coords == null) return null
    assert(coords.length == 2, "location must be [x, y]")
    val xPos = coords(0)
    val yPos = coords(1)
    assert(xPos <= this.gridWidth && xPos > 0, "invalid xpos = " + xPos)
    assert(yPos <= this.gridHeight && yPos > 0, "invalid ypos = " + yPos)
    IntLocation(yPos, xPos)
  }

  private def parseVector(coords: Array[Double]) = {
    assert(coords != null && coords.length == 2, "velocity must be [vx, vy]")
    new Vector2d(coords(0), coords(1))
  }

  def getWalls: Seq[Wall] = walls
  def getSources: List[Source] = sources
  /** @return sink regions from config (not yet applied in the solver). */
  def getSinks: List[Region] = sinks
  def getInitialLiquidRegions: List[Region] = initialLiquidRegions

  def getGridWidth: Int = gridWidth
  def getGridHeight: Int = gridHeight
  def getCellSize: Double = cellSize
  def getGravity: Double = gravity
}
