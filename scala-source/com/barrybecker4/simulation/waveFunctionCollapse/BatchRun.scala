// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse

import com.barrybecker4.simulation.waveFunctionCollapse.model.{WfcModel, OverlappingModel, SimpleTiledModel}
import com.barrybecker4.simulation.waveFunctionCollapse.model.json.{CommonModel, Overlapping, SampleJson, SimpleTiled}
import com.barrybecker4.simulation.waveFunctionCollapse.utils.FileUtil.{getFileReader, getSampleData, writeImage}
import com.google.gson.Gson

import scala.util.control.Breaks.{break, breakable}


object BatchRun extends App {

  val startTime = System.currentTimeMillis()
  var counter = 1

  for (commonModel: CommonModel <- getSampleData.samples.all()) {
    process(commonModel, counter)
    counter += 1
  }

  println(s"Total time = ${(System.currentTimeMillis() - startTime)/1000.0} seconds")


  def process(commonModel: CommonModel, counter: Int): Unit = {
    val limit = commonModel.getLimit
    val screenshots = commonModel.getScreenshots
    val model = commonModel match {
      case overlapping: Overlapping => new OverlappingModel(overlapping)
      case simpleTiled: SimpleTiled => new SimpleTiledModel(simpleTiled)
      case _ => throw new IllegalArgumentException("Unexpected type for " + commonModel)
    }

    createScreenshots(model, limit, screenshots, counter)
  }

  def createScreenshots(model: WfcModel, limit: Int, screenshots: Int, counter: Int): Unit = {
    val name = model.getName
    println("Now processing " + name + "screenshots = " + screenshots)
    for (i <- 0 until screenshots) {
      breakable {
        for (k <- 0 until 10) {
          val seed = i * 10 + k
          val finished = model.run(seed)
          if (finished) {
            println(s"> DONE - $name")
            val image = model.graphics()
            writeImage(image, s"out/$counter $name $i.png")
            break()
          }
          else {
            println(s"$name didn't finish ${model.getName} for $i $k")
          }
        }
      }
    }
  }
}
