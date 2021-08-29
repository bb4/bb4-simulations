/*
 * Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */
package com.barrybecker4.simulation.waveFunctionCollapse

import com.barrybecker4.simulation.waveFunctionCollapse.model.{Model, SimpleTiledModel}
import com.barrybecker4.simulation.waveFunctionCollapse.model.json.{CommonModel, Overlapping, SampleJson, Simpletiled}
import com.google.gson.Gson

import java.io.{BufferedReader, File, FileReader}
import javax.imageio.ImageIO
import scala.util.Random
import scala.util.control.Breaks.break



object BatchRun extends App {
  private val gson = new Gson()
  private val random = new Random()

  val startTime = System.currentTimeMillis()
  println(s"start time = $startTime")

  val currentDir = System.getProperty("user.dir").replace("\\", "/")
  val fileName = currentDir + "/scala-source/com/barrybecker4/simulation/waveFunctionCollapse/samples.json"
  val file = new File(fileName)
  if (file.exists()) {
    val bufferedReader = new BufferedReader(new FileReader(fileName))

    val data: SampleJson = gson.fromJson(bufferedReader, SampleJson.getClass)

    var model: Model = null
    var screenshots = 2
    var limit = 0
    var counter = 1
    var name = ""
    for (commonModel: CommonModel <- data.samples.all()) {
      commonModel match {
        case overlapping: Overlapping => // need defaults?
          model = new OverlappingModel(
            overlapping.name,
            overlapping.n.toInt,
            overlapping.width.toInt,
            overlapping.height.toInt,
            overlapping.periodicInput.toBoolean,
            overlapping.periodic.toBoolean,
            overlapping.symmetry.toInt,
            overlapping.ground.toInt)
          screenshots = overlapping.screenshots.toInt
          limit = overlapping.limit.toInt
          name = overlapping.name
        case simpleTiled: Simpletiled => // need defaults?
          model = new SimpleTiledModel(
            simpleTiled.width.toInt,
            simpleTiled.height.toInt,
            simpleTiled.name,
            simpleTiled.subset,
            simpleTiled.periodic.toBoolean,
            simpleTiled.black.toBoolean)
          screenshots = simpleTiled.screenshots.toInt
          limit = simpleTiled.limit.toInt
          name = simpleTiled.name
        case _ => throw new IllegalArgumentException("Unexpected type for " + commonModel)
      }

      for (i <- 0 until screenshots) {
        for(k <- 0 until 10) {
          val seed = random.nextInt()
          val finished = model.run(seed, limit)
          if (finished) {
            println("> DONE - $name")
            val image = model.graphics()
            val outputFile = new File(s"out/$counter $name $i.png")
            ImageIO.write(image, "png", outputFile)
            break()
          }
        }
      }

      counter += 1
    }

    println("time = ${System.currentTimeMillis() - startTime} milliseconds")
  }

}
