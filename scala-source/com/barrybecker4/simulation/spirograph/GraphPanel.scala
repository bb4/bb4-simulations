/** Copyright by Barry G. Becker, 2000-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.spirograph

import com.barrybecker4.common.concurrency.ThreadUtil
import javax.swing._
import java.awt._
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import com.barrybecker4.simulation.spirograph.model.GraphState


object GraphPanel {
  private val BACKGROUND_COLOR = Color.WHITE
}

/**
  * Panel to contain the SpiroGraph curve and control its rendering.
  * Adapted from David Little's original work.
  * Rendering happens in a separate thread. Note use of monitor for locking.
  * @author David Little
  * @author Barry Becker
  */
class GraphPanel(var state: GraphState) extends JPanel with Runnable {

  setBackground(GraphPanel.BACKGROUND_COLOR)
  this.state.initialize(getWidth, getHeight)
  private var thread = new Thread(this)
  private val decorRenderer = new DecorationRenderer(this.state.params)
  private var graphRenderer = new GraphRenderer(this.state, this)

  this.addComponentListener(new ComponentAdapter() {
    override def componentResized(ce: ComponentEvent): Unit = graphRenderer.clear()
  })

  private var paused: Boolean = false

  def setPaused(newPauseState: Boolean): Unit = synchronized {
    if (paused != newPauseState)
      paused = newPauseState
  }

  def reset(): Unit = {
    stopCurrentThread()
    thread = new Thread(this)
    state.reset()
    graphRenderer = new GraphRenderer(state, this)
    paused = true
    repaint()
  }

  def drawCompleteGraph(): Unit = {
    clear()
    state.reset()
    startDrawingGraph()
    waitUntilDoneRendering()
    repaint()
  }

  /** If we are just going to draw the graph as quickly as possible, and block until its done,
    * then don't mess with trying to draw it in a separate thread.
    */
  def startDrawingGraph(): Unit = {
    if (paused) paused = false
    if (state.isMaxVelocity) graphRenderer.startDrawingGraph()
    else thread.start()
  }

  private def stopCurrentThread(): Unit = {
    paused = false
    graphRenderer.abort()
    waitUntilDoneRendering()
    thread = new Thread(this)
  }

  private def waitUntilDoneRendering(): Unit =
    while (state.isRendering) ThreadUtil.sleep(100)

  /** Starts the rendering thread. */
  override def run(): Unit = {
    graphRenderer.startDrawingGraph()
    thread = new Thread(this)
  }

  /** Does nothing if not paused.
    * If paused, it will discontinue processing on this thread until pauseLock is released.
    */
  def waitIfPaused(): Unit =
      while (paused) ThreadUtil.sleep(200)

  def clear(): Unit = {
    graphRenderer.clear()
    repaint()
  }

  override def paint(g: Graphics): Unit = {
    val g2 = g.asInstanceOf[Graphics2D]
    graphRenderer.renderCurrentGraph(g2)
    if (state.showDecoration) decorRenderer.drawDecoration(g2, getWidth, getHeight)
  }
}
