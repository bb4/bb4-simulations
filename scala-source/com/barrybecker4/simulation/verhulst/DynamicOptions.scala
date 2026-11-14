// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.verhulst

import com.barrybecker4.simulation.common.ui.CreaturePopulationOptionsPanel


/**
  * Dynamic controls for the Verhulst population simulation that will show on the right.
  * They change the behavior of the simulation while it is running.
  */
class DynamicOptions(simulator: VerhulstSimulator)
  extends CreaturePopulationOptionsPanel(
    simulator.getCreatures.map(new CreatureSliderGroup(_))
  )
