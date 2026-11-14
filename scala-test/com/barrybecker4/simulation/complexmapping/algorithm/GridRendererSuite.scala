/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.simulation.complexmapping.algorithm

import com.barrybecker4.simulation.complexmapping.algorithm.model.{Grid, MeshPoint}
import org.scalatest.funsuite.AnyFunSuite

class GridRendererSuite extends AnyFunSuite {

  private val cmap = new MeshColorMap()

  test("render returns image with requested size") {
    val mesh = Array(
      Array(new MeshPoint(0.0, 0.0, 0.1), new MeshPoint(0.0, 1.0, 0.2)),
      Array(new MeshPoint(1.0, 0.0, 0.3), new MeshPoint(1.0, 1.0, 0.4))
    )
    val img = GridRenderer(Grid(mesh), cmap).render(64, 48)
    assert(img.getWidth == 64)
    assert(img.getHeight == 48)
  }

  test("auto viewport includes an interior extreme point") {
    // Edge points near the origin; only the center is far away.
    val mesh = Array(
      Array(new MeshPoint(0.0, 0.0, 0.1), new MeshPoint(0.0, 0.1, 0.1), new MeshPoint(0.0, 0.2, 0.1)),
      Array(new MeshPoint(0.1, 0.0, 0.1), new MeshPoint(100.0, 50.0, 0.5), new MeshPoint(0.1, 0.2, 0.1)),
      Array(new MeshPoint(0.2, 0.0, 0.1), new MeshPoint(0.2, 0.1, 0.1), new MeshPoint(0.2, 0.2, 0.1))
    )
    val viewport = GridRenderer(Grid(mesh), cmap).findViewport()
    assert(viewport.rightX >= 100.0)
    assert(viewport.topY >= 50.0)
  }

  test("non-finite vertices do not make the viewport NaN") {
    val mesh = Array(
      Array(new MeshPoint(Double.NaN, Double.NaN, 0.1), new MeshPoint(1.0, 2.0, 0.2)),
      Array(new MeshPoint(3.0, 4.0, 0.3), new MeshPoint(Double.PositiveInfinity, 0.0, 0.4))
    )
    val viewport = GridRenderer(Grid(mesh), cmap).findViewport()
    assert(viewport.leftX.isFinite)
    assert(viewport.rightX.isFinite)
    assert(viewport.topY.isFinite)
    assert(viewport.bottomY.isFinite)
    assert(viewport.width.isFinite && viewport.width > 0)
    assert(viewport.height.isFinite && viewport.height > 0)
  }
}
