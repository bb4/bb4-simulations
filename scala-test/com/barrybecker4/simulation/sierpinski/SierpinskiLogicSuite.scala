/*
 * Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */
package com.barrybecker4.simulation.sierpinski

import org.scalatest.funsuite.AnyFunSuite

import java.awt.{BasicStroke, Color, Point}
import java.awt.image.BufferedImage


class SierpinskiLogicSuite extends AnyFunSuite {

  test("Triangle.midpoint averages coordinates") {
    val p = Triangle.midpoint(new Point(0, 0), new Point(10, 10))
    assert(p.x === 5)
    assert(p.y === 5)
  }

  test("Triangle.getPoly preserves vertices") {
    val a = new Point(1, 2)
    val b = new Point(3, 4)
    val c = new Point(5, 6)
    val poly = Triangle(a, b, c).getPoly
    assert(poly.npoints === 3)
    assert(poly.xpoints(0) === 1 && poly.ypoints(0) === 2)
    assert(poly.xpoints(1) === 3 && poly.ypoints(1) === 4)
    assert(poly.xpoints(2) === 5 && poly.ypoints(2) === 6)
  }

  test("GraphicsStyler.clampedColorIndex caps at palette size") {
    assert(GraphicsStyler.clampedColorIndex(0) === 0)
    assert(GraphicsStyler.clampedColorIndex(7) === 7)
    assert(GraphicsStyler.clampedColorIndex(100) === 7)
  }

  test("GraphicsStyler.strokeWidthForDepth matches line width formula") {
    assert(GraphicsStyler.strokeWidthForDepth(23f, 0) === 23f / 1f)
    assert(GraphicsStyler.strokeWidthForDepth(23f, 1) === 23f / 4f)
    assert(GraphicsStyler.strokeWidthForDepth(12f, 2) === 12f / 7f)
  }

  test("GraphicsStyler.setStyle applies stroke width and color from palette") {
    val g2 = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB).createGraphics()
    try
      val styler = new GraphicsStyler(23f)
      styler.setStyle(depth = 1, g2)
      val stroke = g2.getStroke.asInstanceOf[BasicStroke]
      assert(stroke.getLineWidth === GraphicsStyler.strokeWidthForDepth(23f, 1))
      assert(g2.getColor === expectedColorAtIndex(GraphicsStyler.clampedColorIndex(1)))
    finally g2.dispose()
  }

  test("TriangleRenderer.setDepth rejects invalid depth") {
    val tr = new TriangleRenderer(new GraphicsStyler(10f))
    assertThrows[IllegalArgumentException](tr.setDepth(0))
    assertThrows[IllegalArgumentException](tr.setDepth(20))
    tr.setDepth(1)
    tr.setDepth(19)
  }

  test("TriangleRenderer.render completes headlessly at depth 1") {
    val img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB)
    val g2 = img.createGraphics()
    try
      val tr = new TriangleRenderer(new GraphicsStyler(5f))
      tr.setDepth(1)
      val tri = Triangle(new Point(50, 10), new Point(10, 90), new Point(90, 90))
      tr.render(tri, g2)
    finally g2.dispose()
  }
}

/** Mirrors palette in [[GraphicsStyler]] companion (same order) for assertions. */
private def expectedColorAtIndex(index: Int): Color =
  val colors = Array(
    new Color(0, 0, 80, 100),
    new Color(0, 10, 210, 200),
    new Color(0, 200, 90, 255),
    new Color(80, 255, 0, 160),
    new Color(250, 200, 0, 150),
    new Color(255, 0, 0, 100),
    new Color(255, 0, 100, 70),
    new Color(250, 0, 255, 40)
  )
  colors(index)
