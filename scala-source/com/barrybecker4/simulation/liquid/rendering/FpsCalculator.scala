// Copyright by Barry G. Becker, 2025. Licensed under MIT License: http://www.opensource.org/licenses/MIT 

package com.barrybecker4.simulation.liquid.rendering

class FpsCalculator() {
  private var lastTime: Long = System.currentTimeMillis()
  private var frameCount: Int = 0
  private var fps: Double = 0.0

  def update(): Double = {
    frameCount += 1
    val currentTime = System.currentTimeMillis()
    if (currentTime - lastTime >= 1000) {
      fps = frameCount.toDouble / ((currentTime - lastTime) / 1000.0)
      lastTime = currentTime
      frameCount = 0
    }
    fps
  }

  def getFps: Double = fps


//  private def calculateFPS(): Int = {
//    frameCount += 1
//    val currentTime = System.currentTimeMillis()
//    val timeElapsed = currentTime - lastFpsTime
//
//    if (timeElapsed > 1000) { // Update FPS calculation once per second
//      currentFps = (frameCount * 1000 / timeElapsed).toInt
//      frameCount = 0
//      lastFpsTime = currentTime
//    }
//
//    currentFps
//  }
}
