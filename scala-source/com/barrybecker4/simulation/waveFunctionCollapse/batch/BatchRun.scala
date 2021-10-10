// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT

package com.barrybecker4.simulation.waveFunctionCollapse.batch

import com.barrybecker4.simulation.waveFunctionCollapse.model.json.{CommonModel, Overlapping, SimpleTiled}
import com.barrybecker4.simulation.waveFunctionCollapse.model.{OverlappingModel, SimpleTiledModel, WfcModel}
import com.barrybecker4.simulation.waveFunctionCollapse.utils.FileUtil.{getSampleData, writeImage}

import scala.util.control.Breaks.{break, breakable}


object BatchRun extends App {

  doBatchRun()

  private def doBatchRun(): Unit = {
    val startTime = System.currentTimeMillis()
    var counter = 1

    for (commonModel: CommonModel <- getSampleData("batch/samples.json").samples.all()) {
      process(commonModel, counter)
      counter += 1
    }

    println(s"Total time = ${(System.currentTimeMillis() - startTime) / 1000.0} seconds")
  }

  private def process(commonModel: CommonModel, counter: Int): Unit = {
    val screenshots = commonModel.getScreenshots
    val model = commonModel match {
      case overlapping: Overlapping => new OverlappingModel(overlapping)
      case simpleTiled: SimpleTiled => new SimpleTiledModel(simpleTiled)
      case _ => throw new IllegalArgumentException("Unexpected type for " + commonModel)
    }

    createScreenshots(model, screenshots, counter)
  }

  private def createScreenshots(model: WfcModel, screenshots: Int, counter: Int): Unit = {
    val name = model.getName
    println("Now processing " + name + " screenshots = " + screenshots)
    for (i <- 0 until screenshots) {
      breakable {
        for (k <- 0 until 10) {
          val seed = counter * 100 + i * 10 + k
          val finished = model.runWithLimit(seed)
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
