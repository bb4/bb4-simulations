// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.snake.rendering

import com.barrybecker4.simulation.snake.Snake
import java.awt._


/**
  * Responsible for drawing the snake onscreen.
  * @author Barry Becker
  */
class SnakeRenderer(val params: RenderingParameters) {
  private var segmentRenderer = new SegmentRenderer(params)

  /** render each segment of the snake */
  def render(snake: Snake, g: Graphics2D) {
    g.setColor(Color.black)
    for (i <- 0 until snake.getNumSegments) {
      segmentRenderer.render(snake.getSegment(i), g)
    }
  }
}
