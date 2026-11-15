/*
 * Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */
package com.barrybecker4.simulation.waveFunctionCollapse

import com.barrybecker4.simulation.waveFunctionCollapse.MainTestSuite.SEED
import com.barrybecker4.simulation.waveFunctionCollapse.model.{OverlappingImageParams, OverlappingModel, SimpleTiledModel}
import org.scalatest.funsuite.AnyFunSuite

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.net.URLDecoder
import scala.collection.mutable
import scala.io.Source


object MainTestSuite {
  val SEED = 1236548748
}

class MainTestSuite extends AnyFunSuite {

  test("overlappingModelTestConsistency") {
    val image = generateOverlappingImage()

    val actualHexes = histogram(image)
    val expectedHexes = loadExpectedHistogram("overlapping_hex.txt")

    assertResult(expectedHexes.keys.size) { actualHexes.keys.size }
    for (h <- expectedHexes.keys) {
      assertResult(expectedHexes(h), "did not match for " + h) { actualHexes(h) }
    }
  }

  private def generateOverlappingImage(): BufferedImage = {
    val model = new OverlappingModel(
      name = "Skyline",
      periodicOutput = true,
      width = 48,
      height = 48,
      imageParams = OverlappingImageParams(3, 2, periodicInput = true, -1),
      limit = 0,
      scale = 1
    )
    assert(model.runWithLimit(SEED), s"Overlapping Skyline failed to collapse with fixed seed $SEED")
    model.graphics()
  }

  test("simpleTileModelTestConsistency") {
    val image = generateSimpleImage()

    val actualHexes = histogram(image)
    val expectedHexes = loadExpectedHistogram("simple.txt")

    assertResult(expectedHexes.keys.size,
      s"Unexpected number of keys. The keys(${actualHexes.keys}) - should have been (${expectedHexes.keys})") {
      actualHexes.keys.size
    }
    for (h <- expectedHexes.keys) {
      assertResult(expectedHexes(h)) { actualHexes(h) }
    }
  }

  private def generateSimpleImage(): BufferedImage = {
    val model = new SimpleTiledModel(
      name = "Knots",
      width = 24,
      height = 24,
      subsetName = null,
      isPeriodic = true,
      black = false,
      limit = 0,
      allowInconsistencies = false
    )
    assert(model.runWithLimit(SEED), s"SimpleTiled Knots failed to collapse with fixed seed $SEED")
    model.graphics()
  }

  private def histogram(image: BufferedImage): mutable.HashMap[String, Int] = {
    val actualHexes = new mutable.HashMap[String, Int]()
    for (x <- 0 until image.getWidth; y <- 0 until image.getHeight) {
      val color = new Color(image.getRGB(x, y))
      val hex = String.format("%02X%02X%02X", color.getRed, color.getGreen, color.getBlue)
      actualHexes(hex) = actualHexes.getOrElse(hex, 0) + 1
    }
    actualHexes
  }

  private def loadExpectedHistogram(fileName: String): mutable.HashMap[String, Int] = {
    val expectedHexes = new mutable.HashMap[String, Int]()
    val src = getResource(fileName)
    for (hex <- src.getLines()) {
      expectedHexes(hex) = expectedHexes.getOrElse(hex, 0) + 1
    }
    src.close()
    expectedHexes
  }

  private def getResource(fileName: String): Source = {
    val file = new File(URLDecoder.decode(getClass.getResource(fileName).getFile, "UTF-8"))
    Source.fromFile(file)
  }
}
