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
  private val gson = new Gson()

  val startTime = System.currentTimeMillis()
  println(s"start time = $startTime")

  val bufferedReader = getFileReader("samples.json")
  val content = bufferedReader.lines().toArray().mkString("")
  val data: SampleJson = gson.fromJson(content, classOf[SampleJson])

  var model: Model = _
  var screenshots = 2
  var limit = 0
  var counter = 1
  var name = ""
  println("num samples = " + data.samples.all().size)
  for (commonModel: CommonModel <- data.samples.all()) {
    commonModel match {
      case overlapping: Overlapping => // need defaults?
        model = new OverlappingModel(
          overlapping.getName,
          overlapping.getN,
          overlapping.getWidth,
          overlapping.getHeight,
          overlapping.getPeriodicInput,
          overlapping.getPeriodic,
          overlapping.getSymmetry,
          overlapping.getGround)
        screenshots = overlapping.getScreenshots
        limit = overlapping.getLimit
        name = overlapping.getName
      case simpleTiled: Simpletiled => // need defaults?
        model = new SimpleTiledModel(
          simpleTiled.getWidth,
          simpleTiled.getHeight,
          simpleTiled.getName,
          simpleTiled.getSubset,
          simpleTiled.getPeriodic,
          simpleTiled.getBlack)
        screenshots = simpleTiled.getScreenshots
        limit = simpleTiled.getLimit
        name = simpleTiled.getName
      case _ => throw new IllegalArgumentException("Unexpected type for " + commonModel)
    }

    println("Now processing " + name + "screenshots = " + screenshots)
    for (i <- 0 until screenshots) {
      breakable {
        for (k <- 0 until 10) {
          val seed = i * k
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

    counter += 1
  }

  println(s"time = ${System.currentTimeMillis() - startTime} milliseconds")
}
