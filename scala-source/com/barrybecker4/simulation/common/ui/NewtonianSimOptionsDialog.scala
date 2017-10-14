// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.common.ui

import com.barrybecker4.ui.components.NumberInput
import javax.swing._
import java.awt._


/**
  * Options for Newtoniam based simulations.
  * @author Barry Becker
  */
abstract class NewtonianSimOptionsDialog(parent: Component, simulator: Simulator)
  extends SimulatorOptionsDialog(parent, simulator) {

  // rendering option controls
  private var drawMeshCheckbox: JCheckBox = _
  private var showVelocitiesCheckbox: JCheckBox = _
  private var showForcesCheckbox: JCheckBox = _
  // physics param options controls
  private var staticFrictionField: NumberInput = _
  private var dynamicFrictionField: NumberInput = _

  override def getTitle = "Newtonian Simulation Configuration"

  override protected def addAdditionalToggles(togglesPanel: JPanel): Unit = {
    val sim = getSimulator.asInstanceOf[NewtonianSimulator]
    togglesPanel.add(createMeshCheckBox(sim))
    togglesPanel.add(createVelocitiesCheckBox(sim))
    togglesPanel.add(createForcesCheckBox(sim))
  }

  protected def createMeshCheckBox(sim: NewtonianSimulator): JCheckBox = {
    drawMeshCheckbox = createCheckBox("Show Wireframe", "draw showing the underlying wireframe mesh", sim.getDrawMesh)
    drawMeshCheckbox
  }

  protected def createVelocitiesCheckBox(sim: NewtonianSimulator): JCheckBox = {
    showVelocitiesCheckbox = createCheckBox("Show Velocity Vectors", "show lines representing velocity vectors on each partical mass", sim.getShowVelocityVectors)
    showVelocitiesCheckbox
  }

  protected def createForcesCheckBox(sim: NewtonianSimulator): JCheckBox = {
    showForcesCheckbox = createCheckBox("Show Force Vectors", "show lines representing force vectors on each partical mass", sim.getShowForceVectors)
    showForcesCheckbox
  }

  override protected def createGlobalPhysicalParamPanel: JPanel = {
    val globalParamPanel = new JPanel
    globalParamPanel.setLayout(new BorderLayout)
    val frictionPanel = new JPanel
    frictionPanel.setLayout(new BoxLayout(frictionPanel, BoxLayout.Y_AXIS))
    frictionPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder, "Friction"))
    val sim = getSimulator.asInstanceOf[NewtonianSimulator]
    staticFrictionField = new NumberInput("static Friction (.0 small - 0.4 large):  ", sim.getStaticFriction, "This controls amount of static surface friction.", 0.0, 0.4, false)
    dynamicFrictionField = new NumberInput("dynamic friction (.0 small - .4 large):  ", sim.getDynamicFriction, "This controls amount of dynamic surface friction.", 0.0, 0.4, false)
    frictionPanel.add(staticFrictionField)
    frictionPanel.add(dynamicFrictionField)
    globalParamPanel.add(frictionPanel, BorderLayout.NORTH)
    globalParamPanel
  }

  override protected def ok(): Unit = {
    super.ok()
    val simulator = getSimulator.asInstanceOf[NewtonianSimulator]
    // set the common rendering and global physics options
    simulator.setDrawMesh(drawMeshCheckbox.isSelected)
    simulator.setShowVelocityVectors(showVelocitiesCheckbox.isSelected)
    if (showForcesCheckbox != null) simulator.setShowForceVectors(showForcesCheckbox.isSelected)
    val staticFriction = staticFrictionField.getValue
    val dynamicFriction = dynamicFrictionField.getValue
    assert(staticFriction >= dynamicFriction)
    simulator.setStaticFriction(staticFriction)
    simulator.setDynamicFriction(dynamicFriction)
  }
}
