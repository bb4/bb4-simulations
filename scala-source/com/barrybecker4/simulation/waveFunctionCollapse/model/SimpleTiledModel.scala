// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model

import com.barrybecker4.simulation.waveFunctionCollapse.model.imageExtractors.SimpleTiledImageExtractor
import com.google.gson.Gson
import com.barrybecker4.simulation.waveFunctionCollapse.model.json.tiled.{SampleSet, SampleTiledData}
import com.barrybecker4.simulation.waveFunctionCollapse.model.json.SimpleTiled
import com.barrybecker4.simulation.waveFunctionCollapse.model.propagation.SimpleTiledPropagatorState
import com.barrybecker4.simulation.waveFunctionCollapse.utils.FileUtil.{getSampleTiledData, readImage}

import java.awt.{Color, Dimension}
import java.awt.image.BufferedImage
import scala.collection.mutable


class SimpleTiledModel(
  var width: Int, var height: Int,
  val name: String, subsetName: String,
  var isPeriodic: Boolean, var black: Boolean,
  val limit: Int = 0, val allowInconsistencies: Boolean
) extends WfcModel(name, width, height, limit) {

  private var tiles: Seq[Array[Color]] = _
  private var tilenames: Seq[String] = _
  private var tilesize: Int = 0
  private val useSubset = subsetName != null
  val gson = new Gson()

  this.periodic = isPeriodic
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
    tilesize = if (dataset.size != null) dataset.size.toInt else 16
    dimensions = new Dimension(Math.max(1, width / tilesize), Math.max(1, height / tilesize))

    val subsets: Seq[String] = getSubsets(subsetName, dataset)
    processTiles(dataset, subsets)
  }

  private def getSubsets(subsetName: String, dataset: SampleSet): Seq[String] = {
    var subsets: Seq[String] = null
    if (subsetName != null) {
      val xSubSet = dataset.subsets(0).name
      if (xSubSet == null) {
        println(s"ERROR SUBSET $subsetName not found among " + dataset.subsets.mkString(", "))
      }
      else {
        for (tile <- dataset.tiles) {
          if (subsets == null) {
            subsets = Seq()
          }
          subsets :+= tile.name
        }
      }
    }
    subsets
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

  private def processTiles(dataset: SampleSet, subsets: Seq[String]): Unit = {
    tiles = Seq()
    tilenames = Seq()
    val unique = if (dataset.unique != null) dataset.unique.toBoolean else false
    var tempStationary: Seq[Double] = Seq()
    var action: Seq[IntArray] = Seq()

    val firstOccurrence = new mutable.HashMap[String, Int]()

    for (tile <- dataset.tiles) {
      val tileName = tile.name
      if (!useSubset || subsets.contains(tileName)) {
        var a: Int => Int = null
        var b: Int => Int = null
        var cardinality: Int = 0

        val sym = if (tile.symmetry != null) tile.symmetry else "X"
        sym match {
          case "L" =>
            cardinality = 4
            a = i => (i + 1) % 4
            b = i => if (i % 2 == 0) i + 1 else i - 1
          case "T" =>
            cardinality = 4
            a = i => (i + 1) % 4
            b = i => if (i % 2 == 0) i else 4 - i
          case "I" =>
            cardinality = 2
            a = i => 1 - i
            b = i => i
          case "\\" =>
            cardinality = 2
            a = i => 1 - i
            b = i => 1 - i
          case _ =>
            cardinality = 1
            a = i => i
            b = i => i
        }

        tCounter = action.size
        firstOccurrence(tileName) = tCounter

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
            map(t)(s) = map(t)(s) + tCounter

          action :+= map(t)
        }

        if (unique) {
          for (t <- 0 until cardinality) {
            val bufferedImage: BufferedImage = readImage(s"samples/$name/$tileName $t.png")
            tiles :+= tileFun((x, y) => new Color(bufferedImage.getRGB(x, y)))
            tilenames :+= s"$tileName $t"
          }
        }
        else {
          val bufferedImage: BufferedImage = readImage(s"samples/$name/$tileName.png")
          tiles :+= tileFun((x, y) => new Color(bufferedImage.getRGB(x, y)))
          tilenames :+= s"$tileName ${0}"

          for (t <- 1 until cardinality) {
            tiles :+= rotateFun(tiles(tCounter + t - 1))
            tilenames :+= s"$tileName $t"
          }
        }

        for (t <- 0 until cardinality) {
          tempStationary :+= tile.getWeight
        }
      }
    }

    tCounter = action.size
    weights = tempStationary.toArray

    propagator = new SimpleTiledPropagatorState(tCounter, action, dataset.neighbors, firstOccurrence, subsets)
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