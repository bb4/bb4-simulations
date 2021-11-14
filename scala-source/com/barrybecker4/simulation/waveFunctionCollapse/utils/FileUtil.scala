// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.waveFunctionCollapse.utils

import com.barrybecker4.simulation.waveFunctionCollapse.model.json.SampleJson
import com.barrybecker4.simulation.waveFunctionCollapse.model.json.tiled.SampleTiledData
import com.google.gson.Gson

import java.awt.image.BufferedImage
import java.io.{BufferedReader, File, InputStream, InputStreamReader}
import java.nio.charset.StandardCharsets
import javax.imageio.ImageIO



object FileUtil {
  val CURRENT_DIR: String = System.getProperty("user.dir").replace("\\", "/") + "/scala-source/"
  private val RELATIVE_DIR = "com/barrybecker4/simulation/waveFunctionCollapse/"
  val BASE_DIR: String = CURRENT_DIR + RELATIVE_DIR
  val GSON: Gson = new Gson()

  def readImage(fileName: String): BufferedImage = {
    val is: InputStream = getClass.getClassLoader.getResourceAsStream(RELATIVE_DIR + fileName)
    ImageIO.read(is)
  }

  def writeImage(image: BufferedImage, fileName: String): Unit = {
    val outputFile = new File(CURRENT_DIR + fileName)
    ImageIO.write(image, "png", outputFile)
  }

  def getSampleData(filename: String = "samples.json"): SampleJson = {
    val bufferedReader = getReader(filename)
    val content = bufferedReader.lines().toArray().mkString("")
    GSON.fromJson(content, classOf[SampleJson])
  }

  def getSampleTiledData(name: String): SampleTiledData = {
    val bufferedReader = getReader(s"samples/$name/data.json")
    GSON.fromJson(bufferedReader, classOf[SampleTiledData])
  }

  private def getReader(resourceName: String): BufferedReader = {
    // reading as a resource instead of file allows it to work from deployed version
    println("reading from " + RELATIVE_DIR + resourceName)
    val is: InputStream = getClass.getClassLoader.getResourceAsStream(RELATIVE_DIR + resourceName)
    new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
  }
}
