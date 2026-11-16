/* Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm

import java.awt.{BasicStroke, Color, Font, Graphics2D, RenderingHints}
import java.awt.image.BufferedImage
import com.barrybecker4.math.complex.ComplexNumber
import com.barrybecker4.simulation.complexmapping.algorithm.functions.ComplexFunction
import com.barrybecker4.simulation.complexmapping.algorithm.model.Box
import com.barrybecker4.ui.util.ColorMap
import DomainHeatmapRenderer._


object DomainHeatmapRenderer {
  private val AXIS_COLOR = new Color(200, 200, 210)
  private val CRITICAL_LINE_COLOR = new Color(255, 220, 80)
  private val RE_CONTOUR_COLOR = new Color(255, 120, 120, 200)
  private val IM_CONTOUR_COLOR = new Color(120, 180, 255, 200)
  private val ZERO_MARKER_COLOR = new Color(255, 255, 255)
  private val POLE_COLOR = new Color(255, 80, 80)
  private val LABEL_COLOR = new Color(240, 240, 245)
  private val MARGIN_PX = 36
  private val CRITICAL_RE = 0.5
  private val POLE_RE = 1.0
  private val POLE_IM = 0.0
  val LogMin = -2.0
  val LogMax = 2.0

  /** Map |f| to log10 scale used by [[MagnitudeColorMap]]. */
  def logMagnitude(z: ComplexNumber): Double = {
    val mag = z.abs
    if (!mag.isFinite || mag <= 0) LogMin
    else Math.max(LogMin, Math.min(LogMax, Math.log10(mag)))
  }

  def argumentValue(z: ComplexNumber): Double = {
    if (!z.real.isFinite || !z.imaginary.isFinite) 0.0
    else ComplexNumber.arg(z)
  }

  private[algorithm] def niceTicks(min: Double, max: Double, targetCount: Int): Seq[Double] = {
    if (max <= min) return Seq(min)
    val raw = (max - min) / Math.max(1, targetCount)
    val mag = Math.pow(10, Math.floor(Math.log10(raw)))
    val norm = raw / mag
    val step =
      if (norm < 1.5) mag
      else if (norm < 3.5) 2 * mag
      else if (norm < 7.5) 5 * mag
      else 10 * mag
    val start = Math.ceil(min / step) * step
    val ticks = scala.collection.mutable.ArrayBuffer.empty[Double]
    var v = start
    while (v <= max + step * 1e-9) {
      ticks += v
      v += step
    }
    ticks.toSeq
  }

  private def formatTick(v: Double): String =
    if (Math.abs(v - Math.rint(v)) < 1e-9) Math.rint(v).toInt.toString
    else f"$v%.1f"
}

/**
  * Renders a domain heatmap of a complex function over a viewport in the complex plane,
  * with Re/Im zero contours, critical-line marker, and labeled non-trivial zeros.
  * @author Barry Becker
  */
case class DomainHeatmapRenderer(
  function: ComplexFunction,
  n: Int,
  viewport: Box,
  sampleInc: Double,
  colorMode: DomainColorMode,
  colorMap: ColorMap,
  showKnownZeros: Boolean
) {

  private var xScale: Double = 1.0
  private var yScale: Double = 1.0
  private var plotH: Int = 1

  def render(pixelWidth: Int, pixelHeight: Int): BufferedImage = {
    val w = Math.max(1, pixelWidth)
    val h = Math.max(1, pixelHeight)
    val buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
    val g = buf.createGraphics()
    try {
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g.setColor(Color.BLACK)
      g.fillRect(0, 0, w, h)

      val plotW = Math.max(1, w - 2 * MARGIN_PX)
      plotH = Math.max(1, h - 2 * MARGIN_PX)
      xScale = plotW / viewport.width
      yScale = plotH / viewport.height

      val samples = sampleField()
      paintHeatmap(g, samples)
      paintContours(g, samples)
      paintAxes(g, plotW, plotH)
      paintCriticalLine(g)
      if (showKnownZeros) paintKnownZeros(g)
      paintPole(g)
      paintPaneTitle(g)
    } finally {
      g.dispose()
    }
    buf
  }

  private case class SampleField(
    xs: Array[Double],
    ys: Array[Double],
    re: Array[Array[Double]],
    im: Array[Array[Double]],
    colorValues: Array[Array[Double]]
  )

  private def sampleField(): SampleField = {
    val inc = Math.max(0.01, sampleInc)
    val nx = Math.max(2, Math.ceil(viewport.width / inc).toInt + 1)
    val ny = Math.max(2, Math.ceil(viewport.height / inc).toInt + 1)
    val xs = Array.tabulate(nx)(i => viewport.leftX + i * (viewport.width / (nx - 1)))
    val ys = Array.tabulate(ny)(j => viewport.bottomY + j * (viewport.height / (ny - 1)))
    val re = Array.ofDim[Double](nx, ny)
    val im = Array.ofDim[Double](nx, ny)
    val colorValues = Array.ofDim[Double](nx, ny)
    for (i <- 0 until nx; j <- 0 until ny) {
      val z = function.compute(ComplexNumber(xs(i), ys(j)), n)
      re(i)(j) = z.real
      im(i)(j) = z.imaginary
      colorValues(i)(j) = colorMode match {
        case DomainColorMode.Magnitude => logMagnitude(z)
        case DomainColorMode.Argument => argumentValue(z)
      }
    }
    SampleField(xs, ys, re, im, colorValues)
  }

  private def paintHeatmap(g: Graphics2D, samples: SampleField): Unit = {
    val nx = samples.xs.length
    val ny = samples.ys.length
    for (i <- 0 until nx - 1; j <- 0 until ny - 1) {
      val x0 = convertX(samples.xs(i))
      val x1 = convertX(samples.xs(i + 1))
      val yTop = convertY(samples.ys(j + 1))
      val yBot = convertY(samples.ys(j))
      val avg =
        (samples.colorValues(i)(j) + samples.colorValues(i + 1)(j) +
          samples.colorValues(i + 1)(j + 1) + samples.colorValues(i)(j + 1)) / 4.0
      g.setColor(colorMap.getColorForValue(avg))
      g.fillRect(x0, yTop, Math.max(1, x1 - x0 + 1), Math.max(1, yBot - yTop + 1))
    }
  }

  private def paintContours(g: Graphics2D, samples: SampleField): Unit = {
    g.setStroke(new BasicStroke(1.2f))
    g.setColor(RE_CONTOUR_COLOR)
    DomainContour.zeroContours(samples.re, samples.xs, samples.ys).foreach(drawSegment(g, _))
    g.setColor(IM_CONTOUR_COLOR)
    DomainContour.zeroContours(samples.im, samples.xs, samples.ys).foreach(drawSegment(g, _))
  }

  private def drawSegment(g: Graphics2D, seg: DomainContour.Segment): Unit =
    g.drawLine(convertX(seg.a.x), convertY(seg.a.y), convertX(seg.b.x), convertY(seg.b.y))

  private def paintAxes(g: Graphics2D, plotW: Int, plotHeight: Int): Unit = {
    g.setColor(AXIS_COLOR)
    g.setStroke(new BasicStroke(1f))
    g.drawRect(MARGIN_PX, MARGIN_PX, plotW, plotHeight)

    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11))
    for (t <- niceTicks(viewport.bottomY, viewport.topY, 8)) {
      val py = convertY(t)
      g.setColor(AXIS_COLOR)
      g.drawLine(MARGIN_PX - 4, py, MARGIN_PX, py)
      g.setColor(LABEL_COLOR)
      g.drawString(formatTick(t), 2, py + 4)
    }
    for (t <- niceTicks(viewport.leftX, viewport.rightX, 5)) {
      val px = convertX(t)
      g.setColor(AXIS_COLOR)
      g.drawLine(px, MARGIN_PX + plotHeight, px, MARGIN_PX + plotHeight + 4)
      g.setColor(LABEL_COLOR)
      g.drawString(formatTick(t), px - 8, MARGIN_PX + plotHeight + 16)
    }
    g.setColor(LABEL_COLOR)
    g.drawString("Re", MARGIN_PX + plotW - 18, MARGIN_PX + plotHeight + 28)
    g.drawString("Im", 4, MARGIN_PX - 8)
  }

  private def paintCriticalLine(g: Graphics2D): Unit = {
    if (CRITICAL_RE < viewport.leftX || CRITICAL_RE > viewport.rightX) return
    val x = convertX(CRITICAL_RE)
    g.setColor(CRITICAL_LINE_COLOR)
    g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f,
      Array(6f, 4f), 0f))
    g.drawLine(x, MARGIN_PX, x, MARGIN_PX + plotH)
    g.setStroke(new BasicStroke(1f))
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11))
    g.drawString("σ = 1/2", x + 4, MARGIN_PX + 14)
  }

  private def paintKnownZeros(g: Graphics2D): Unit = {
    g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11))
    for (z <- KnownNonTrivialZeros.inViewport(viewport)) {
      val px = convertX(z.real)
      val py = convertY(z.imag)
      g.setColor(ZERO_MARKER_COLOR)
      g.fillOval(px - 3, py - 3, 7, 7)
      g.setColor(Color.BLACK)
      g.drawOval(px - 3, py - 3, 7, 7)
      g.setColor(LABEL_COLOR)
      g.drawString(z.label, px + 6, py - 4)
    }
  }

  private def paintPole(g: Graphics2D): Unit = {
    if (POLE_RE < viewport.leftX || POLE_RE > viewport.rightX) return
    if (POLE_IM < viewport.bottomY || POLE_IM > viewport.topY) return
    val px = convertX(POLE_RE)
    val py = convertY(POLE_IM)
    g.setColor(POLE_COLOR)
    g.setStroke(new BasicStroke(2f))
    g.drawLine(px - 5, py - 5, px + 5, py + 5)
    g.drawLine(px - 5, py + 5, px + 5, py - 5)
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11))
    g.drawString("pole s=1", px + 6, py + 12)
  }

  private def paintPaneTitle(g: Graphics2D): Unit = {
    g.setColor(LABEL_COLOR)
    g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12))
    val title = colorMode match {
      case DomainColorMode.Magnitude => "Domain: log |f(s)|"
      case DomainColorMode.Argument => "Domain: arg(f(s))"
    }
    g.drawString(title, MARGIN_PX, 16)
  }

  private def convertX(x: Double): Int = (MARGIN_PX + (x - viewport.leftX) * xScale).toInt
  private def convertY(y: Double): Int = (MARGIN_PX + (viewport.topY - y) * yScale).toInt
}
