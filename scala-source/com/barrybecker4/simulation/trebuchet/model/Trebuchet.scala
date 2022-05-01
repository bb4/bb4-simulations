// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet.model

import com.barrybecker4.ui.util.Log

import javax.vecmath.Vector2d
import java.awt.Color
import java.awt.Graphics2D
import com.barrybecker4.simulation.common.PhysicsConstants.GRAVITY
import com.barrybecker4.simulation.trebuchet.model.TrebuchetConstants.*
import com.barrybecker4.simulation.trebuchet.model.parts.Lever
import com.barrybecker4.simulation.trebuchet.model.parts.CounterWeight
import com.barrybecker4.simulation.trebuchet.model.parts.Sling
import com.barrybecker4.simulation.trebuchet.model.parts.Projectile
import com.barrybecker4.simulation.trebuchet.model.parts.Base
import com.barrybecker4.simulation.trebuchet.rendering.{AbstractPartRenderer, *}

import java.lang.Math.PI
import java.lang.Math.asin
import java.lang.Math.cos
import java.lang.Math.sin


/**
  * Data structure and methods for representing a dynamic trebuchet
  * The geometry of the trebuchet is defined by constants in TebuchetConstants
  */
class Trebuchet(var showVelocityVectors: Boolean = false, var showForceVectors: Boolean = false) {

  // scales the geometry of the trebuchet
  private var scale = SCALE
  
  private var lever: Lever = _
  private var counterWeight: CounterWeight = _
  private var sling: Sling = _
  private var projectile: Projectile = _
  private var trebuchetRenderer: TrebuchetRenderer = _
  private var trebuchetProcessor: TrebuchetProcessor = _
  
  commonInit()

  def reset(): Unit = {
    lever.setAngularVelocity(0)
    commonInit()
  }

  private def commonInit(): Unit = {
    val base = Base()
    lever = Lever(base, DEFAULT_CW_LEVER_LENGTH, DEFAULT_SLING_LEVER_LENGTH)
    lever.setAngle(PI / 2.0 - asin(HEIGHT / DEFAULT_SLING_LEVER_LENGTH))
    counterWeight = new CounterWeight(lever, DEFAULT_COUNTER_WEIGHT_MASS)
    projectile = new Projectile(base, DEFAULT_PROJECTILE_MASS)
    sling = Sling(lever, projectile, DEFAULT_SLING_LENGTH, DEFAULT_SLING_RELEASE_ANGLE)
    trebuchetRenderer = new TrebuchetRenderer(base, lever, counterWeight, sling, projectile)
    trebuchetProcessor = new TrebuchetProcessor(lever, counterWeight, sling, projectile)
  }

  def stepForward(timeStep: Double): Double = {
    trebuchetProcessor.stepForward(timeStep)
  }

  def setScale(scale: Double): Unit = {
    this.scale = scale
  }

  def getScale: Double = scale

  def getCounterWeightLeverLength: Double = lever.getCounterWeightLeverLength

  def setCounterWeightLeverLength(counterWeightLeverLength: Double): Unit = {
    lever.setCounterWeightLeverLength(counterWeightLeverLength)
  }

  def getSlingLeverLength: Double = lever.getSlingLeverLength

  def setSlingLeverLength(slingLeverLength: Double): Unit = {
    lever.setSlingLeverLength(slingLeverLength)
  }

  def getCounterWeightMass: Double = counterWeight.getMass

  def setCounterWeightMass(counterWeightMass: Double): Unit = {
    this.counterWeight.setMass(counterWeightMass)
  }

  def getSlingLength: Double = sling.length

  def setSlingLength(slingLength: Double): Unit = {
    this.sling.length = slingLength
  }

  def getProjectileMass: Double = projectile.getMass
  def setProjectileMass(projectileMass: Double): Unit = {
    this.projectile.mass = projectileMass
  }

  def getSlingReleaseAngle: Double = sling.releaseAngle
  def setSlingReleaseAngle(slingReleaseAngle: Double): Unit = {
    this.sling.releaseAngle = slingReleaseAngle
  }

  /**
    * Render the Environment on the screen
    */
  def render(g: Graphics2D, viewHeight: Int): Unit = {
    g.setColor(Color.black)
    trebuchetRenderer.render(g, getScale, viewHeight)
  }
}
