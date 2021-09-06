// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.waveFunctionCollapse.utils

import java.awt.image.BufferedImage
import java.io.{BufferedReader, File, FileReader, IOException}
import javax.imageio.ImageIO

object FileUtil {
  val CURRENT_DIR: String = System.getProperty("user.dir").replace("\\", "/") + "/"
  private val RELATIVE_DIR = "scala-source/com/barrybecker4/simulation/waveFunctionCollapse/"
  val BASE_DIR: String = CURRENT_DIR + RELATIVE_DIR

  def readImage(fileName: String): BufferedImage = {
    ImageIO.read(new File(BASE_DIR + fileName))
  }

  def writeImage(image: BufferedImage, fileName: String): Unit = {
    val outputFile = new File(CURRENT_DIR + fileName)
    ImageIO.write(image, "png", outputFile)
  }

  def getFileReader(fileName: String): BufferedReader = {
    val file = new File(BASE_DIR + fileName)
    if (!file.exists()) {
      throw new IOException("File not found: " + file.getAbsoluteFile)
    }
    new BufferedReader(new FileReader(file))
  }
}
