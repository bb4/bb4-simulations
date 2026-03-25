// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model

import com.barrybecker4.simulation.waveFunctionCollapse.model.imageExtractors.SimpleTiledImageExtractor
import com.barrybecker4.simulation.waveFunctionCollapse.model.json.SimpleTiled
import com.barrybecker4.simulation.waveFunctionCollapse.model.json.tiled.{SampleSet, SampleTiledData, Tile}
import com.barrybecker4.simulation.waveFunctionCollapse.model.propagation.SimpleTiledPropagatorState
import com.barrybecker4.simulation.waveFunctionCollapse.utils.FileUtil.{getSampleTiledData, readImage}

import java.awt.{Color, Dimension}
import java.awt.image.BufferedImage
import scala.collection.mutable
import scala.compiletime.uninitialized


object SimpleTiledModel {

  /**
    * @return `null` when `subsetName` is null (caller uses all tiles).
    *         Otherwise the base tile names belonging to the named subset from `data.json`.
    */
  def resolveSubsetTileNames(subsetName: String, dataset: SampleSet): Seq[String] = {
    if (subsetName == null) return null
    if (dataset.subsets == null || dataset.subsets.isEmpty)
      throw new IllegalArgumentException("Subsets are required in data.json when a subset name is selected.")

    dataset.subsets.find(_.name == subsetName) match {
      case None =>
        val available = dataset.subsets.map(_.name).mkString(", ")
        throw new IllegalArgumentException(
          s"Subset '$subsetName' not found. Available: $available")
      case Some(subset) =>
        if (subset.tiles == null || subset.tiles.isEmpty)
          throw new IllegalArgumentException(s"Subset '$subsetName' has no tiles in data.json.")
        subset.tiles.map(_.name).toSeq
    }
  }

  private def symmetryCardinalityAndOps(sym: String): (Int, Int => Int, Int => Int) =
    sym match {
      case "L" =>
        (4, i => (i + 1) % 4, i => if (i % 2 == 0) i + 1 else i - 1)
      case "T" =>
        (4, i => (i + 1) % 4, i => if (i % 2 == 0) i else 4 - i)
      case "I" =>
        (2, i => 1 - i, i => i)
      case "\\" =>
        (2, i => 1 - i, i => 1 - i)
      case _ =>
        (1, i => i, i => i)
    }
}

class SimpleTiledModel(
  val width: Int, val height: Int,
  val name: String, val subsetName: String,
  var isPeriodic: Boolean, var black: Boolean,
  val limit: Int = 0,
  val allowInconsistencies: Boolean = false
) extends WfcModel(name, width, height, limit) {

  private var tiles: Seq[Array[Color]] = uninitialized
  private var tilenames: Seq[String] = uninitialized
  private var tilesize: Int = 0
  private val useSubset = subsetName != null


  processFile(getSampleTiledData(name))

  def this(simpleTiled: SimpleTiled) = {
    this(
      simpleTiled.getWidth,
      simpleTiled.getHeight,
      simpleTiled.getName,
      simpleTiled.getSubset,
      simpleTiled.getPeriodic,
      simpleTiled.getBlack,
      simpleTiled.getLimit,
      true)
  }

  def processFile(data: SampleTiledData): Unit = {
    val dataset = data.set
    this.periodic = isPeriodic
    tilesize = if (dataset.size != null) dataset.size.toInt else 16
    dimensions = new Dimension(Math.max(1, width / tilesize), Math.max(1, height / tilesize))

    val subsetTileNames: Seq[String] = SimpleTiledModel.resolveSubsetTileNames(subsetName, dataset)
    processTiles(dataset, subsetTileNames)
  }

  private def tileFun(passedInFunc: (Int, Int) => Color): Array[Color] = {
    val result: Array[Color] = Array.fill(tilesize * tilesize)(null)
    for (y <- 0 until tilesize)
      for (x <- 0 until tilesize)
        result(x + y * tilesize) = passedInFunc(x, y)
    result
  }

  private def rotateFun(array: Array[Color]): Array[Color] = {
    tileFun((x: Int, y: Int) => {
      val idx = tilesize - 1 - y + x * tilesize
      if (array(idx) != null) array(idx) else Color.BLACK
    })
  }

  private def appendRotatedActionMaps(
      cardinality: Int,
      a: Int => Int,
      b: Int => Int,
      tCounterBase: Int,
      action: mutable.ArrayBuffer[IntArray]): Unit = {
    val map: Array[IntArray] = Array.fill(cardinality)(null)
    for (t <- 0 until cardinality) {
      map(t) = new IntArray(8)
      val mapt = map(t)

      mapt(0) = t
      mapt(1) = a(t)
      mapt(2) = a(a(t))
      mapt(3) = a(a(a(t)))
      mapt(4) = b(t)
      mapt(5) = b(a(t))
      mapt(6) = b(a(a(t)))
      mapt(7) = b(a(a(a(t))))

      for (s <- 0 to 7)
        map(t)(s) = map(t)(s) + tCounterBase

      action += map(t)
    }
  }

  private def loadTilesForCardinality(
      tile: Tile,
      tileName: String,
      unique: Boolean,
      cardinality: Int): Unit = {
    if (unique) {
      for (t <- 0 until cardinality) {
        val bufferedImage: BufferedImage = readImage(s"samples/$name/$tileName $t.png")
        tiles :+= tileFun((x, y) => new Color(bufferedImage.getRGB(x, y)))
        tilenames :+= s"$tileName $t"
      }
    } else {
      val bufferedImage: BufferedImage = readImage(s"samples/$name/$tileName.png")
      tiles :+= tileFun((x, y) => new Color(bufferedImage.getRGB(x, y)))
      tilenames :+= s"$tileName ${0}"

      for (t <- 1 until cardinality) {
        tiles :+= rotateFun(tiles(tCounter + t - 1))
        tilenames :+= s"$tileName $t"
      }
    }
  }

  private def processTiles(dataset: SampleSet, subsetTileNames: Seq[String]): Unit = {
    tiles = Seq()
    tilenames = Seq()
    val unique = if (dataset.unique != null) dataset.unique.toBoolean else false
    var tempStationary: Seq[Double] = Seq()
    val action = mutable.ArrayBuffer[IntArray]()

    val firstOccurrence = new mutable.HashMap[String, Int]()

    for (tile <- dataset.tiles) {
      val tileName = tile.name
      if (!useSubset || subsetTileNames.contains(tileName)) {
        val sym = if (tile.symmetry != null) tile.symmetry else "X"
        val (cardinality, a, b) = SimpleTiledModel.symmetryCardinalityAndOps(sym)

        tCounter = action.size
        firstOccurrence(tileName) = tCounter

        appendRotatedActionMaps(cardinality, a, b, tCounter, action)

        loadTilesForCardinality(tile, tileName, unique, cardinality)

        for (_ <- 0 until cardinality) {
          tempStationary :+= tile.getWeight
        }
      }
    }

    tCounter = action.size
    weights = tempStationary.toArray

    propagator = new SimpleTiledPropagatorState(tCounter, action.toSeq, dataset.neighbors, firstOccurrence, subsetTileNames)
  }

  def onBoundary(x: Int, y: Int): Boolean = {
    !periodic && (x < 0 || y < 0 || x >= FMX || y >= FMY)
  }

  def graphics(): BufferedImage  = {
    assert(this.isReady)
    val imageExtractor = new SimpleTiledImageExtractor(dimensions, tCounter, tilesize, tiles, weights, black)
    imageExtractor.getImage(wave)
  }

  def textOutput(): String = {
    val result = new StringBuilder()
    for (x <- 0 until FMX) {
      for (y <- 0 until FMY) {
        val name = tilenames(wave.get(x + y * FMX).observed)
        result.append (name).append ("\n")
      }
    }
    result.toString ()
  }
}
