package com.barrybecker4.simulation.dungeon.ui

import com.barrybecker4.simulation.dungeon.generator.GeneratorStrategyEnum

import java.awt.Dimension
import javax.swing.{JComboBox, JLabel, JPanel}
import java.awt.event.ItemListener


class GeneratorChoice(listener: ItemListener) extends JPanel {

  private var generatorStrategyChoice: JComboBox[String] = _

  init()

  private def init(): Unit = {
    val label = new JLabel("Generator Strategy: ")
    setMinimumSize(Dimension(200, 50))

    generatorStrategyChoice = new JComboBox[String]

    GeneratorStrategyEnum.values.foreach(v => generatorStrategyChoice.addItem(v.toString))

    generatorStrategyChoice.setSelectedItem(GeneratorStrategyEnum.BinarySpacePartition.ordinal)
    generatorStrategyChoice.addItemListener(listener)
    add(label)
    add(generatorStrategyChoice)
  }

}
