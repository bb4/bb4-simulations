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
    if (image == null) {
      fail("Image was null")
    }

    val actualHexes = new mutable.HashMap[String, Int]()
    for (x <- 0 until image.getWidth()) {
      for (y <- 0 until image.getHeight()) {
        val color = new Color(image.getRGB(x, y))
        val hex = String.format("%02X%02X%02X", color.getRed, color.getGreen, color.getBlue)
        actualHexes(hex) = actualHexes.getOrElse(hex, 0) + 1
      }
    }

    val expectedHexes = new mutable.HashMap[String, Int]()
    val src = getResource("overlapping_hex.txt")

    for (hex <- src.getLines()) {
      expectedHexes(hex) = expectedHexes.getOrElse(hex, 0) + 1
    }
    src.close()

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

    for (i <- 0 until 2) {
      for (k <- 0 until 10) {
        val finished = model.runWithLimit(SEED)
        if (finished) {
          return model.graphics()
        }
      }
    }

    null
  }

  test("simpleTileModelTestConsistency") {
    val image = generateSimpleImage()
    if (image == null) {
      fail("Image was null")
    }

    val actualHexes = new mutable.HashMap[String, Int]()
    for (x <- 0 until image.getWidth) {
      for (y <- 0 until image.getHeight) {
        val color = new Color(image.getRGB(x, y))
        val hex = String.format("%02X%02X%02X", color.getRed, color.getGreen, color.getBlue)
        actualHexes(hex) = actualHexes.getOrElse(hex, 0) + 1
      }
    }

    val expectedHexes = new mutable.HashMap[String, Int]()
    val src = getResource("simple.txt")
    for (hex <- src.getLines()) {
      expectedHexes(hex) = expectedHexes.getOrElse(hex, 0) + 1
    }
    src.close()

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
      subsetName = "Dense Fabric",
      isPeriodic = true,
      black = false,
      limit = 0,
      allowInconsistencies = false
    )

    for (i <- 0 until 2) {
      for (k <- 0 until 10) {
        val finished = model.runWithLimit(SEED)
        if (finished) {
          return model.graphics()
        }
      }
    }

    null
  }

  private def getResource(fileName: String): Source = {
    // The extra URLDecoder is needed for Jenkins because it puts %20 in the path instead of spaces.
    val file = new File(URLDecoder.decode(getClass().getResource(fileName).getFile(), "UTF-8"))
    Source.fromFile(file)
  }
}
