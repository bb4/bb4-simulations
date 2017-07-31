/** Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion.rendering

/**
  * Rendering specific options
  *
  * @author Barry Becker
  */
class RDRenderingOptions() {
  setParallelized(false)
  private var showingU: Boolean = false
  private var showingV:Boolean = true
  /** used for scaling the bump height. if 0, then no bumpiness. */
  private var heightScale: Double = 0
  /** Specular highlight degree. */
  private var specularConst: Double = 0
  private var runParallelized = false

  def setParallelized(useParallelization: Boolean) { runParallelized = useParallelization}
  def isParallelized: Boolean = runParallelized

  def setHeightScale(h: Double) { heightScale = h }
  def getHeightScale: Double = heightScale

  def setSpecular(s: Double) { specularConst = s}
  def getSpecular: Double = specularConst

  def isShowingU: Boolean = this.showingU
  def setShowingU(showingU: Boolean) {this.showingU = showingU}

  def isShowingV: Boolean = showingV
  def setShowingV(showingV: Boolean) { this.showingV = showingV}
}