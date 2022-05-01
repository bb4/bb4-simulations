// Copyright by Barry G. Becker, 2022. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.rendering

import com.barrybecker4.simulation.trebuchet.model.TrebuchetConstants.{DEFAULT_COUNTER_WEIGHT_MASS, DEFAULT_CW_LEVER_LENGTH, DEFAULT_PROJECTILE_MASS, DEFAULT_SLING_LENGTH, DEFAULT_SLING_LEVER_LENGTH, DEFAULT_SLING_RELEASE_ANGLE, HEIGHT}
import com.barrybecker4.simulation.trebuchet.model.parts.{Base, CounterWeight, Lever, Projectile, Sling}

import java.awt.{Color, Graphics2D}
import java.lang.Math.{PI, asin}


class TrebuchetRenderer(base: Base, lever: Lever, counterWeight: CounterWeight, sling: Sling, projectile: Projectile) 
  extends AbstractPartRenderer {

  private var partRenderers:Seq[AbstractPartRenderer] = Seq()

  partRenderers :+= new BaseRenderer(base)
  partRenderers :+= new LeverRenderer(lever)
  partRenderers :+= new CounterWeightRenderer(counterWeight)
  partRenderers :+= new SlingRenderer(sling)
  partRenderers :+= new ProjectileRenderer(projectile)

  def render(g: Graphics2D, scale: Double, viewHeight: Int): Unit = {
    partRenderers.foreach(renderer => renderer.render(g, scale, viewHeight))
  }
}
