/*
 * Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */
package com.barrybecker4.simulation.waveFunctionCollapse

import com.barrybecker4.simulation.waveFunctionCollapse.model.{Model, SimpleTiledModel}
import com.barrybecker4.simulation.waveFunctionCollapse.model.json.{CommonModel, Overlapping, SampleJson, Simpletiled}
import com.barrybecker4.simulation.waveFunctionCollapse.utils.FileUtil.BASE_DIR
import com.google.gson.Gson

import java.io.{BufferedReader, File, FileReader}
import java.util.stream.Collectors
import javax.imageio.ImageIO
import scala.util.Random
import scala.util.control.Breaks.break



object BatchRun extends App {
  private val gson = new Gson()
  private val random = new Random()

  val startTime = System.currentTimeMillis()
  println(s"start time = $startTime")

  val fileName = BASE_DIR + "samples.json"
  val file = new File(fileName)
  if (file.exists()) {
    val bufferedReader = new BufferedReader(new FileReader(fileName))

    val content = bufferedReader.lines().toArray().mkString("")
    println(s"$fileName content ------")
    println(content)
    println("-------")
    //val data: SampleJson = gson.fromJson(bufferedReader, classOf[SampleJson])
    val data: SampleJson = gson.fromJson(content, classOf[SampleJson])

    var model: Model = null
    var screenshots = 2
    var limit = 0
    var counter = 1
    var name = ""
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
