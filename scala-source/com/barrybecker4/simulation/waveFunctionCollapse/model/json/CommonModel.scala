// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.waveFunctionCollapse.model.json


trait CommonModel {
  def getName: String
  def getLimit: Int
  def getScreenshots: Int

  override def toString: String = getName
}

