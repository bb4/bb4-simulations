// Copyright by Barry G. Becker, 2016-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.predprey.options

import com.barrybecker4.simulation.common.ui.CreaturePopulationOptionsPanel
import com.barrybecker4.simulation.predprey.PredPreySimulator


/**
  * Dynamic controls for the predator–prey simulation (sliders on the right).
  * They change the behavior of the simulation while it is running.
  * @author Barry Becker
  */
class DynamicOptions(simulator: PredPreySimulator)
  extends CreaturePopulationOptionsPanel(
    simulator.getCreatures.map(new CreatureSliderGroup(_))
  )
