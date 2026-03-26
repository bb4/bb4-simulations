// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.model.plugin

import com.barrybecker4.common.util.PackageReflector
import java.io.IOException
import java.lang.reflect.Modifier
import scala.collection.mutable.ListBuffer


/**
  * Finds plugins for type of stock series generation strategies.
  * Can be used to populate a dropdown.
  * Uses reflection to dynamically load available strategies.
  * @param packageName name of the package to get the plugin classes from
  *              Something like "com.barrybecker4.simulation.trading.model.generationstrategy"
  * @param clzz the type of class to look for. e.g. classOf[GenerationStrategy]
  * @param defaultStrategies list of default strategies to always include in the list.
  * @author Barry Becker
  */
class StrategyPlugins[E <: StrategyPlugin](val packageName: String, val clzz: Class[?],
                                           val defaultStrategies: Seq[E]) {
  private val strategyNames = ListBuffer[String]()
  private var valueMap = Map[String, E]()

  try {
    loadFromReflection()
  } catch {
    case _: NullPointerException | _: SecurityException =>
      useDefaultStrategiesOnly()
    case e@(_: ClassNotFoundException | _: InstantiationException | _: IOException | _: IllegalAccessException) =>
      e.printStackTrace()
  }

  mergeDefaultStrategies()

  private def registerStrategy(s: E): Unit = {
    if (!valueMap.contains(s.name)) {
      strategyNames.append(s.name)
    }
    valueMap += s.name -> s
  }

  private def loadFromReflection(): Unit = {
    val strategyClasses: Seq[Class[?]] = new PackageReflector().getClasses(packageName)
    for (c <- strategyClasses) {
      if (!Modifier.isAbstract(c.getModifiers) && clzz.isAssignableFrom(c)) {
        val strategy: E = c.getDeclaredConstructor().newInstance().asInstanceOf[E]
        registerStrategy(strategy)
      }
    }
  }

  private def useDefaultStrategiesOnly(): Unit = {
    valueMap = Map.empty
    strategyNames.clear()
    for (s <- defaultStrategies) {
      registerStrategy(s)
    }
  }

  /** Ensures built-in defaults appear in the map when reflection did not register them (e.g. alternate classloaders). */
  private def mergeDefaultStrategies(): Unit = {
    for (s <- defaultStrategies) {
      if (!valueMap.contains(s.name)) {
        registerStrategy(s)
      }
    }
  }

  def getStrategies: Seq[String] = strategyNames.toSeq

  /** Resolve a strategy by display name, or throw with a clear message. */
  def getStrategy(name: String): E =
    valueMap.getOrElse(
      name,
      throw new IllegalArgumentException(
        s"Unknown strategy '$name'. Known: ${valueMap.keys.toSeq.sorted.mkString(", ")}"
      )
    )
}
