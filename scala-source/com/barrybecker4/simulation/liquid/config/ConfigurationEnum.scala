// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.config


/**
  * Different configurations to choose from.
  * @author Barry Becker
  */
enum ConfigurationEnum(val name: String, val description: String, val fileName: String):

  case SPIGOT_RIGHT extends ConfigurationEnum(
    "Spigot to the Right", "A spigot aimed to the right", ConfigurationEnum.FILE_BASE + "spigotRight.xml")
  case SPIGOT_LEFT extends ConfigurationEnum(
    "Spigot to the Left", "A spigot aimed to the left", ConfigurationEnum.FILE_BASE + "spigotLeft.xml")
  case BASIC extends ConfigurationEnum(
    "Basic", "A stream of water into a pool", ConfigurationEnum.FILE_BASE + "config1.xml")
  case FALLING_BLOB extends ConfigurationEnum(
    "Falling water", "A falling blob of water onto the floor", ConfigurationEnum.FILE_BASE + "fallingWater.xml")
  case FALLING_BLOB_SMALL extends ConfigurationEnum(
    "Falling water small", "A small falling blob of water onto the floor",
    ConfigurationEnum.FILE_BASE + "fallingWaterSmall.xml")
  case WATER_WALL_LEFT extends ConfigurationEnum(
    "Water wall (left)", "An initial wall of water on the left",
    ConfigurationEnum.FILE_BASE + "wallOfWaterLeft.xml")
  case WATER_WALL_RIGHT extends ConfigurationEnum(
    "Water wall (right)", "An initial wall of water on the right",
    ConfigurationEnum.FILE_BASE + "wallOfWaterRight.xml")
  case PULSE_LARGE extends ConfigurationEnum(
    "Pulsing spigot", "Water pulsing out of the spigot", ConfigurationEnum.FILE_BASE + "pulse_large.xml")
  case PULSE_SMALL extends ConfigurationEnum(
    "Pulsing spigot (3x3)", "Water pulsing out of the spigot (3x3)",
    ConfigurationEnum.FILE_BASE + "pulse_small.xml")
  case PULSE_SMALLEST extends ConfigurationEnum(
    "Pulsing spigot (2x2)", "Water pulsing out of the spigot (2x2)",
    ConfigurationEnum.FILE_BASE + "pulse_smallest.xml")

  override def toString: String = name


object ConfigurationEnum:
  /** Must be a compile-time constant so enum case constructors can use it during initialization. */
  inline val FILE_BASE = "com/barrybecker4/simulation/liquid/data/"
  val DEFAULT_VALUE: ConfigurationEnum = PULSE_LARGE

  def toEnum(theName: String): ConfigurationEnum = values.find(_.name == theName).get
