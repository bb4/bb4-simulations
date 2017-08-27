// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.config

import com.barrybecker4.simulation.liquid.config.ConfigurationEnum.FILE_BASE

/**
  * Different configurations to choose from.
  * @author Bary Becker
  */
object ConfigurationEnum {
  val FILE_BASE = "com/barrybecker4/simulation/liquid/data/"
  val DEFAULT_VALUE = PULSE_LARGE
  val values = Array(SPIGOT_RIGHT, SPIGOT_LEFT, BASIC, FALLING_BLOB, FALLING_BLOB_SMALL, WATER_WALL_LEFT,
    WATER_WALL_RIGHT, PULSE_LARGE, PULSE_SMALL, PULSE_SMALLEST)

  def toEnum(theName: String): ConfigurationEnum = ConfigurationEnum.values.find(_.name == theName).get
}

sealed abstract class ConfigurationEnum(val name: String, val description: String, val fileName: String) {
  override def toString: String = name
}

case object SPIGOT_RIGHT extends ConfigurationEnum(
  "Spigot to the Right", "A spigot aimed to the right", FILE_BASE + "spigotRight.xml")
case object SPIGOT_LEFT extends ConfigurationEnum(
  "Spigot to the Left", "A spigot aimed to the left", FILE_BASE + "spigotLeft.xml")
case object BASIC extends ConfigurationEnum(
  "Basic", "A stream of water into a pool",  FILE_BASE + "config1.xml")
case object FALLING_BLOB extends ConfigurationEnum(
  "Falling water", "A falling blob of water onto the floor",  FILE_BASE + "fallingWater.xml")
case object FALLING_BLOB_SMALL extends ConfigurationEnum(
  "Falling water small", "A small falling blob of water onto the floor", FILE_BASE + "fallingWaterSmall.xml")
case object WATER_WALL_LEFT extends ConfigurationEnum(
  "Water wall (left)", "An initial wall of water on the left", FILE_BASE + "wallOfWaterLeft.xml")
case object WATER_WALL_RIGHT extends ConfigurationEnum(
  "Water wall (right)", "An initial wall of water on the right", FILE_BASE + "wallOfWaterRight.xml")
case object PULSE_LARGE extends ConfigurationEnum(
  "Pulsing spigot", "Water pulsing out of the spigot", FILE_BASE + "pulse_large.xml")
case object PULSE_SMALL extends ConfigurationEnum(
  "Pulsing spigot (3x3)", "Water pulsing out of the spigot (3x3)", FILE_BASE + "pulse_small.xml")
case object PULSE_SMALLEST extends ConfigurationEnum(
  "Pulsing spigot (2x2)", "Water pulsing out of the spigot (2x2)", FILE_BASE + "pulse_smallest.xml")

