/** Copyright by Barry G. Becker, 2000-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.spirograph

import com.barrybecker4.simulation.spirograph.model.GraphState
import com.barrybecker4.common.concurrency.ThreadUtil
import com.barrybecker4.ui.renderers.OfflineGraphics
import java.awt._


/**
  * Renders the SpiroGraph curve. May be interrupted.
  * @param state the spirograph state
  * @param graphPanel UI component to show the offline rendered image in when done.
  * @author Barry Becker
  */
class GraphRenderer(var state: GraphState, var graphPanel: GraphPanel) {

  /** offline rendering is faster */
  private var offlineGraphics: OfflineGraphics = _
  private var aborted = false

  /** Draws the graph into the offline image. */
  def startDrawingGraph(): Unit = {
    var count = 0
    state.initialize(graphPanel.getWidth, graphPanel.getHeight)
    state.setRendering(true)
    val r2 = state.params.getR2
    val p = state.params.getPos
    // avoid degenerate (divide by 0 case) curves.
    if (r2 == 0) return
    val revs = state.getNumRevolutions
    val n = 1.0f + state.getNumSegmentsPerRev * Math.abs(p / r2)

    while ( {
      {
        count += 1; count - 1
      } < (n * revs + 0.5).toInt && !aborted
    }) drawSegment(count, revs, n)

    state.setRendering(false)
  }

  /** Renders the current offline image into the g2 object.
    * @param g2 graphics to render image into.
    */
  def renderCurrentGraph(g2: Graphics2D): Unit = {
    val xpos = (graphPanel.getSize().width - graphPanel.getWidth) >> 1
    val ypos = (graphPanel.getSize().height - graphPanel.getHeight) >> 1
    g2.drawImage(getOfflineGraphics.getOfflineImage.get, xpos, ypos, graphPanel)
  }

  /** Sets the center point. */
  def setPoint(pos: Float, phi: Float): Unit = {
    val center = state.params.getCenter(graphPanel.getWidth, graphPanel.getHeight)
    state.params.setX((center.getX + pos * Math.cos(phi)).toFloat)
    state.params.setY((center.getY - pos * Math.sin(phi)).toFloat)
  }

  /** Stop the rendering as quickly as possible */
  def abort(): Unit =
    aborted = true

  def clear(): Unit = getOfflineGraphics.clear()

  /** Draw a small line segment that makes up the larger spiral curve.
    * Drawn into the offline image.
    */
  private def drawSegment(count: Int, revs: Int, n: Float): Unit = {
    var r1 = .0f
    var r2 = .0f
    var p = .0f
    r1 = state.params.getR1
    r2 = state.params.getR2
    p = state.params.getPos
    getOfflineGraphics.setColor(state.getColor)
    if (count == (n * revs + 0.5).toInt) state.params.setTheta(0.0f)
    else state.params.setTheta((2.0f * Math.PI * count / n).toFloat)
    val theta = state.params.getTheta
    state.params.setPhi(theta * (1.0f + r1 / r2))
    val phi = state.params.getPhi
    setPoint(p, phi)
    graphPanel.waitIfPaused()
    val stroke = new BasicStroke(state.getWidth.toFloat / GraphState.INITIAL_LINE_WIDTH.toFloat)
    getOfflineGraphics.setStroke(stroke)
    getOfflineGraphics.drawLine(state.oldParams.getX.toInt, state.oldParams.getY.toInt,
                                state.params.getX.toInt, state.params.getY.toInt)
    if (!state.isMaxVelocity) {
      graphPanel.repaint()
      doSmallDelay()
    }
    state.recordValues()
  }

  private def doSmallDelay(): Unit = ThreadUtil.sleep(state.getDelayMillis)

  /** @return the offline graphics instance. Creates the it if needed before returning.*/
  private def getOfflineGraphics = {
    if (offlineGraphics == null) offlineGraphics = new OfflineGraphics(graphPanel.getSize, graphPanel.getBackground)
    offlineGraphics
  }
}