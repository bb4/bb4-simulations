// Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT 

package com.barrybecker4.simulation.dungeon.ui

import com.barrybecker4.simulation.dungeon.model.DungeonOptions

trait DungeonOptionsChangedListener {
  def optionsChanged(options: DungeonOptions): Unit
}
