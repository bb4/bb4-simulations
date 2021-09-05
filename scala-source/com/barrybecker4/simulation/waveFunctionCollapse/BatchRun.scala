// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse

import com.barrybecker4.simulation.waveFunctionCollapse.model.{Model, OverlappingModel, SimpleTiledModel}
import com.barrybecker4.simulation.waveFunctionCollapse.model.json.{CommonModel, Overlapping, SampleJson, Simpletiled}
import com.barrybecker4.simulation.waveFunctionCollapse.utils.FileUtil.{getFileReader, writeImage}
import com.google.gson.Gson

import java.io.{BufferedReader, File, FileReader}
import javax.imageio.ImageIO
import scala.util.Random
import scala.util.control.Breaks.{break, breakable}


object BatchRun extends App {

  val startTime = System.currentTimeMillis()
  var counter = 1

  for (commonModel: CommonModel <- getSampleData.samples.all()) {
    process(commonModel, counter)
    counter += 1
  }

  println(s"Total time = ${(System.currentTimeMillis() - startTime)/1000.0} seconds")


  private def getSampleData: SampleJson = {
    val bufferedReader = getFileReader("samples.json")
    val content = bufferedReader.lines().toArray().mkString("")
    val gson = new Gson()
    gson.fromJson(content, classOf[SampleJson])
  }

  def process(commonModel: CommonModel, counter: Int): Unit = {
    var model: Model = null
    val limit = commonModel.getLimit
    val screenshots = commonModel.getScreenshots

    commonModel match {
      case overlapping: Overlapping =>
        model = new OverlappingModel(
          overlapping.getName,
          overlapping.getN,
          overlapping.getWidth,
          overlapping.getHeight,
          overlapping.getPeriodicInput,
          overlapping.getPeriodic,
          overlapping.getSymmetry,
          overlapping.getGround)
      case simpleTiled: Simpletiled =>

        model = new SimpleTiledModel(
          simpleTiled.getWidth,
          simpleTiled.getHeight,
          simpleTiled.getName,
          simpleTiled.getSubset,
          simpleTiled.getPeriodic,
          simpleTiled.getBlack)
      case _ => throw new IllegalArgumentException("Unexpected type for " + commonModel)
    }

    createScreenshots(model, limit, screenshots)
  }

  def createScreenshots(model: Model, limit: Int, screenshots: Int): Unit = {
    val name = model.getName
    println("Now processing " + name + "screenshots = " + screenshots)
    for (i <- 0 until screenshots) {
      breakable {
        for (k <- 0 until 10) {
          val seed = i * 10 + k
          val finished = model.run(seed, limit)
          if (finished) {
            println(s"> DONE - $name")
            val image = model.graphics()
            writeImage(image, s"out/$counter $name $i.png")
            break()
          }
          else println(s"$name didn't finish ${model.getName} for $i $k")
        }
      }
    }
  }
}
