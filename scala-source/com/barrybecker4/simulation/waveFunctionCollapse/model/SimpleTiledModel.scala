// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model

import com.google.gson.Gson
import com.barrybecker4.simulation.waveFunctionCollapse.model.data.SampleData
import com.barrybecker4.simulation.waveFunctionCollapse.model.imageExtractors.SimpleTileImageExtractor
import com.barrybecker4.simulation.waveFunctionCollapse.model.json.SimpleTiled
import com.barrybecker4.simulation.waveFunctionCollapse.model.propagators.SimpleTiledPropagator
import com.barrybecker4.simulation.waveFunctionCollapse.utils.FileUtil.{getFileReader, readImage}

import java.awt.Color
import java.awt.image.BufferedImage
import scala.collection.mutable


class SimpleTiledModel(
  var width: Int, var height: Int,
  val name: String, subsetName: String,
  var isPeriodic: Boolean, var black: Boolean) extends WfcModel(name, width, height) {

  private var tiles: Seq[Array[Color]] = _
  private var tilenames: Seq[String] = _
  private var tilesize: Int = 0
  private val useSubset = subsetName != null
  val gson = new Gson()

  this.periodic = isPeriodic
  processFile(s"samples/$name/data.json")

  def this(simpleTiled: SimpleTiled) = {
    this(
      simpleTiled.getWidth,
      simpleTiled.getHeight,
      simpleTiled.getName,
      simpleTiled.getSubset,
      simpleTiled.getPeriodic,
      simpleTiled.getBlack)
  }

  def processFile(fname: String): Unit = {

    val bufferedReader = getFileReader(fname)
    val data: SampleData = gson.fromJson(bufferedReader, classOf[SampleData])

    val dataset = data.set
    tilesize = if (dataset.size != null) dataset.size.toInt else 16
    val unique = if (dataset.unique != null) dataset.unique.toBoolean else false

    var subset: Seq[String] = null
    if (subsetName != null) {
      val xSubSet = dataset.subsets(0).name
      if (xSubSet == null) {
        println(s"ERROR SUBSET $subsetName not found")
      }
      else {
        for (tile <- dataset.tiles) {
          if (subset == null) {
            subset = Seq()
          }
          subset :+= tile.name
        }
      }
    }

    def tileFun(passedInFunc: (Int, Int) => Color): Array[Color] = {
      val result: Array[Color] = Array.fill(tilesize * tilesize)(null)
      for (y <- 0 until tilesize)
        for (x <- 0 until tilesize)
          result(x + y * tilesize) = passedInFunc(x, y)
      result
    }

    def rotateFun(array: Array[Color]): Array[Color] = {
      tileFun((x: Int, y: Int) => {
        val idx = tilesize - 1 - y + x * tilesize
        if (array(idx) != null) array(idx) else Color.BLACK
      })
    }

    tiles = Seq()
    tilenames = Seq()
    var tempStationary: Seq[Double] = Seq()
    var action: Seq[IntArray] = Seq()

    val firstOccurrence = new mutable.HashMap[String, Int]()

    for (tile <- dataset.tiles) {
      val tileName = tile.name
      if (!useSubset || subset.contains(tileName)) {
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

    propagator = new SimpleTiledPropagator(tCounter, action, dataset.neighbors, firstOccurrence, subset)
    imageExtractor = new SimpleTileImageExtractor(FMX, FMY, tCounter, tilesize, tiles, weights, black)
  }

  def onBoundary(x: Int, y: Int): Boolean = {
    !periodic && (x < 0 || y < 0 || x >= FMX || y >= FMY)
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