/** Copyright by Barry G. Becker, 2000-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.algorithm

import org.scalatest.funsuite.AnyFunSuite

/**
  * Resize is deferred until timeStep; applying it must keep model dimensions
  * consistent with u/v so rendering cannot AIOOBE.
  */
class GrayScottControllerSuite extends AnyFunSuite {

  test("deferred setSize applied on timeStep keeps arrays in sync with dimensions") {
    val controller = new GrayScottController(10, 10)
    controller.setSize(20, 15)
    // Not applied yet
    assert(controller.model.getWidth == 10)
    assert(controller.model.u.length == 10)

    controller.timeStep(1.0)

    val m = controller.model
    assert(m.getWidth == 20)
    assert(m.getHeight == 15)
    assert(m.u.length == m.getWidth)
    assert(m.u(0).length == m.getHeight)
    assert(m.v.length == m.getWidth)
    assert(m.v(0).length == m.getHeight)
    val _ = m.u(m.getWidth - 1)(m.getHeight - 1) + m.v(m.getWidth - 1)(m.getHeight - 1)
  }

  /**
    * Reproduces the resize paint crash: EDT-style reads of u(width-1) while the
    * animation thread applies a deferred shrink via timeStep. Without locking,
    * width can still reflect the old size after u has already been replaced.
    */
  test("concurrent paint-style reads stay in bounds across resize timeSteps") {
    val controller = new GrayScottController(128, 128)
    @volatile var running = true
    @volatile var failure: Option[Throwable] = None

    val reader = new Thread(() => {
      try {
        while (running) {
          // Same access pattern as RDViewer.paint / RenderingColorModel during paint.
          controller.withModel { m =>
            val w = m.getWidth
            val h = m.getHeight
            if (w > 0 && h > 0) {
              val _ = m.u(w - 1)(h - 1) + m.v(w - 1)(h - 1)
            }
          }
        }
      } catch {
        case e: Exception => failure = Some(e)
      }
    }, "rd-paint-reader")
    reader.start()

    try {
      for (i <- 0 until 200) {
        val w = 80 + (i % 60)
        val h = 80 + ((i * 3) % 60)
        controller.setSize(w, h)
        controller.timeStep(1.0)
      }
    } finally {
      running = false
      reader.join(5000)
    }

    failure.foreach(t => fail(t))
  }

  /**
    * Brush strokes must survive into the next timeStep source buffer. Adding only
    * to u (the old InteractionHandler path) is overwritten when the step reads tmpU.
    */
  test("addU brush elevates concentration through a following timeStep") {
    val brushed = new GrayScottController(32, 32)
    val control = new GrayScottController(32, 32)
    brushed.timeStep(1.0)
    control.timeStep(1.0)

    val x = 16
    val y = 16
    brushed.model.addU(x, y, 0.8)

    brushed.timeStep(1.0)
    control.timeStep(1.0)

    val brushedConc = brushed.model.tmpU(x)(y) + brushed.model.u(x)(y)
    val controlConc = control.model.tmpU(x)(y) + control.model.u(x)(y)
    assert(brushedConc > controlConc + 0.1,
      s"expected brushed concentration ($brushedConc) to exceed control ($controlConc)")
  }
}
