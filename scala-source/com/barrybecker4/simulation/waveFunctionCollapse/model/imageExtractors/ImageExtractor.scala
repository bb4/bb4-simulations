// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.imageExtractors

import com.barrybecker4.simulation.waveFunctionCollapse.model.wave.Wave

import java.awt.image.BufferedImage


trait ImageExtractor {

  def getImage(wave: Wave): BufferedImage
}
