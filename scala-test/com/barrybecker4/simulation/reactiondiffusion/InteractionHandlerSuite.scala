/** Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.reactiondiffusion

import com.barrybecker4.simulation.reactiondiffusion.algorithm.GrayScottController
import java.awt.event.MouseEvent
import javax.swing.JPanel
import org.scalatest.funsuite.AnyFunSuite

/**
  * With dt = 0 the Jacobi step copies the read buffer into the write buffer.
  * If the brush only updates the display buffer, a zero step reverts the ink.
  */
class InteractionHandlerSuite extends AnyFunSuite {

  test("left brush persists across a zero timeStep") {
    val controller = new GrayScottController(30, 30)
    controller.reset()
    controller.setParallelized(false)
    val model = controller.model
    val x = 2
    val y = 2
    val uBefore = model.u(x)(y)

    brush(model, x, y, MouseEvent.BUTTON1)
    val uBrushed = model.u(x)(y)
    assert(uBrushed != uBefore)

    controller.timeStep(0.0)
    assert(model.u(x)(y) == uBrushed)
  }

  test("right brush persists across a zero timeStep") {
    val controller = new GrayScottController(30, 30)
    controller.reset()
    controller.setParallelized(false)
    val model = controller.model
    val x = 2
    val y = 2
    val vBefore = model.v(x)(y)

    brush(model, x, y, MouseEvent.BUTTON3)
    val vBrushed = model.v(x)(y)
    assert(vBrushed != vBefore)

    controller.timeStep(0.0)
    assert(model.v(x)(y) == vBrushed)
  }

  private def brush(model: com.barrybecker4.simulation.reactiondiffusion.algorithm.GrayScottModel,
                    x: Int, y: Int, button: Int): Unit = {
    val handler = new InteractionHandler(model, 1.0)
    handler.setBrushRadius(2)
    handler.setBrushStrength(0.5)
    val panel = new JPanel()
    handler.mousePressed(mouseEvent(panel, MouseEvent.MOUSE_PRESSED, x, y, button))
    handler.mouseDragged(mouseEvent(panel, MouseEvent.MOUSE_DRAGGED, x, y, button))
  }

  private def mouseEvent(panel: JPanel, id: Int, x: Int, y: Int, button: Int): MouseEvent =
    new MouseEvent(panel, id, System.currentTimeMillis(), 0, x, y, 1, false, button)
}
