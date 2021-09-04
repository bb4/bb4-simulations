/*
 * Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */
package com.barrybecker4.simulation.waveFunctionCollapse.model


import com.google.gson.Gson
import com.barrybecker4.simulation.waveFunctionCollapse.model.data.SampleData
import com.barrybecker4.simulation.waveFunctionCollapse.utils.Utils.trimToColor

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import javax.imageio.ImageIO
import scala.collection.mutable
import scala.collection.mutable.ListBuffer


class SimpleTiledModel(
  var width: Int, var height: Int,
  val name: String, subsetName: String,
  var isPeriodic: Boolean, var black: Boolean) extends Model(width, height) {

  private var tiles: Seq[Array[Color]] = _
  private var tilenames: Seq[String] = _
  private var tilesize: Int = 0
  private val useSubset = subsetName.nonEmpty
  val gson = new Gson()

  this.periodic = isPeriodic

  val fileName = "samples/$name/data.json"
  processFile(fileName)

  def processFile(str: String): Unit = {
    val file = new File(fileName)
    if (file.exists()) {
      val bufferedReader = new BufferedReader(new FileReader(fileName))
      val data: SampleData = gson.fromJson(bufferedReader, classOf[SampleData])

      val dataset = data.set
      tilesize = if (dataset.size != null) dataset.size.toInt else 16
      val unique = if (dataset.unique != null) dataset.unique.toBoolean else false

      var subset: Seq[String] = null
      if (subsetName.nonEmpty) {
        val xSubSet = dataset.subsets.subset.head.name
        if (xSubSet == null) {
          println(s"ERROR SUBSET $subsetName not found")
        }
        else {
          for (tile <- dataset.tiles.tile) {
            if (subset == null) {
              subset = Seq()
            }
            subset :+= tile.name // what if name is null?
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

      for (tile <- dataset.tiles.tile) {
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
              val bufferedImage: BufferedImage = ImageIO.read(new File(s"samples/$name/$tileName $t.png"))
              tiles :+= tileFun((x, y) => new Color(bufferedImage.getRGB(x, y)))
              tilenames :+= s"$tileName $t"
            }
          }
          else {
            val bufferedImage: BufferedImage = ImageIO.read(new File(s"samples/$name/$tileName.png"))
            tiles :+= tileFun((x, y) => new Color(bufferedImage.getRGB(x, y)))
            tilenames :+= s"$tileName ${0}"

            for (t <- 1 until cardinality) {
              tiles :+= rotateFun(tiles(tCounter + t - 1))
              tilenames :+= s"$tileName $t"
            }
          }

          for (t <- 0 until cardinality) {
            tempStationary :+= (if (tile.weight != null) tile.weight.toDouble else 1.0)
          }
        }

        tCounter = action.size
        weights = tempStationary.toArray

        propagator = Array.fill(4)(null) //arrayOfNulls<Array<IntArray?>?>(4)
        val tempPropagator: Array[Array[Array[Boolean]]] = Array.fill(4)(null) //arrayOfNulls<Array<Array<Boolean?>?>>(4)

        for (d <- 0 to 3) {
          tempPropagator(d) = Array.fill(tCounter)(null)
          propagator(d) = Array.fill(tCounter)(null)
          for (t <- 0 until tCounter)
            tempPropagator(d)(t) = Array.fill(tCounter)(false) // Option[Boolean] and None nere?
        }

        for (neighbor <- dataset.neighbors.neighbor) {
          val left = neighbor.left.split(" ", 0).filter(_.nonEmpty)
          val right = neighbor.right.split(" ", 0).filter(_.nonEmpty)

          if (!(subset == null || (subset.contains(left(0)) && subset.contains(right(0))))) {
            val leftPosition: Int = action(firstOccurrence(left(0)))(if (left.length == 1) 0 else left(1).toInt)
            val downPosition: Int = action(leftPosition)(1)
            val rightPosition: Int = action(firstOccurrence(right(0)))(if (right.length == 1) 0 else right(1).toInt)
            val upPosition: Int = action(rightPosition)(1)

            tempPropagator(0)(rightPosition)(leftPosition) = true
            tempPropagator(0)(action(rightPosition)(6))(action(leftPosition)(6)) = true
            tempPropagator(0)(action(leftPosition)(4))(action(rightPosition)(4)) = true
            tempPropagator(0)(action(leftPosition)(2))(action(rightPosition)(2)) = true

            tempPropagator(1)(upPosition)(downPosition) = true
            tempPropagator(1)(action(downPosition)(6))(action(upPosition)(6)) = true
            tempPropagator(1)(action(upPosition)(4))(action(downPosition)(4)) = true
            tempPropagator(1)(action(downPosition)(2))(action(upPosition)(2)) = true
          }
        }

        for (t2 <- 0 until tCounter) {
          for (t1 <- 0 until tCounter) {
            tempPropagator(2)(t2)(t1) = tempPropagator(0)(t1)(t2)
            tempPropagator(3)(t2)(t1) = tempPropagator(1)(t1)(t2)
          }
        }

        val sparsePropagator: Array[Array[ListBuffer[Int]]] = Array.fill(4)(null)

        for (d <- 0 to 3)
          sparsePropagator(d) = Array.fill(tCounter)(new ListBuffer[Int]())

        for (d <- 0 to 3) {
          for (t1 <- 0 until tCounter) {
            val sp: ListBuffer[Int] = sparsePropagator(d)(t1)
            val tp = tempPropagator(d)(t1)
            for (t2 <- 0 until tCounter)
              if (tp(t2)) sp.append(t2)

            val size = sp.size
            propagator(d)(t1) = new IntArray(size)
            for (st <- 0 until size)
              propagator(d)(t1)(st) = sp(st)
          }
        }
      }
    }
  }


  // should have override qualifier
  def onBoundary(x: Int, y: Int): Boolean = {
    !periodic && (x < 0 || y < 0 || x >= FMX || y >= FMY)
  }

  def graphics(): BufferedImage = {
    val result = new BufferedImage(FMX * tilesize, FMY * tilesize, BufferedImage.TYPE_4BYTE_ABGR)

    if (observed != null) {
      for (x <- 0 until FMX) {
        for (y <- 0 until FMY) {
          val tile: Array[Color] = tiles(observed(x + y * FMX))
          for (yt <- 0 until tilesize) {
            for (xt <- 0 until tilesize) {
              val c = tile(xt + yt * tilesize)
              result.setRGB(x * tilesize + xt, y * tilesize + yt, c.getRGB)
            }
          }
        }
      }
    }
    else {
      for (x <- 0 until FMX) {
        for (y <- 0 until FMY) {
          val a = wave(x + y * FMX)

          val amount = a.toSeq.map(a => if (a) 1 else 0).sum
          val lambda = 1.0 / (0 until tCounter).filter(t => a(t)).map(t => weights(t)).sum
          for (yt <- 0 until tilesize) {
            for (xt <- 0 until tilesize) {
              if (black && amount == tCounter) {
                val blackColor = Color.black
                result.setRGB(x * tilesize + xt, y * tilesize + yt, blackColor.getRGB)
              } else {
                var r = 0.0
                var g = 0.0
                var b = 0.0
                for (t <- 0 until tCounter) {
                  if (wave(x + y * FMX)(t)) {
                    val c = tiles(t)(xt + yt * tilesize)
                    r += c.getRed.toDouble * weights(t) * lambda
                    g += c.getGreen.toDouble * weights(t) * lambda
                    b += c.getBlue.toDouble * weights(t) * lambda
                  }
                }

                val color = new Color(trimToColor(r.toInt), trimToColor(g.toInt), trimToColor(b.toInt))
                val xCord = x * tilesize + xt
                val yCord = y * tilesize + yt
                result.setRGB(xCord, yCord, color.getRGB)
              }
            }
          }
        }
      }
    }
    result
  }

  def textOutput(): String = {
    val result = new StringBuilder()
    for (x <- 0 until FMX) {
      for (y <- 0 until FMY) {
        val name = tilenames(observed(x + y * FMX))
        result.append (name).append ("\n")
      }
    }
    result.toString ()
  }
}