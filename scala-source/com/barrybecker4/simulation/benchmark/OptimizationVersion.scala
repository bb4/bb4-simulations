// Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.benchmark

import com.barrybecker4.optimization.Optimizer

/**
  * Determines which bb4-optimization version is actually on the classpath, so that results are never recorded
  * under the wrong library version. The report column is the separate `--label`, which defaults to this version.
  */
object OptimizationVersion {

  private val JarName = """bb4-optimization-(.+)\.jar""".r

  /** @return the version taken from the jar file name, or None if the classes are not from a versioned jar */
  def detect(): Option[String] = {
    val source = classOf[Optimizer].getProtectionDomain.getCodeSource
    if (source == null) None
    else {
      val path = source.getLocation.getPath
      val fileName = path.substring(path.lastIndexOf('/') + 1)
      fileName match {
        case JarName(version) => Some(version)
        case _ => None
      }
    }
  }

  /**
    * @param requested the version the person says is on the classpath (`--lib-version`), if any
    * @return the version on the classpath
    * @throws IllegalStateException if the requested version disagrees with the detected one,
    *                               or if there is no way to know the version
    */
  def resolve(requested: Option[String]): String =
    (detect(), requested) match {
      case (Some(found), Some(wanted)) if found != wanted =>
        throw new IllegalStateException(
          s"bb4-optimization $found is on the classpath, but --lib-version=$wanted was requested. " +
            "Check the bb4-optimization dependency in build.gradle.")
      case (Some(found), _) => found
      case (None, Some(wanted)) =>
        println(s"WARNING: could not detect the bb4-optimization version from the classpath; using --lib-version=$wanted")
        wanted
      case (None, None) =>
        throw new IllegalStateException(
          "Could not detect the bb4-optimization version from the classpath (it may be a local source build). " +
            "Pass --lib-version=<version> to say which version the results belong to.")
    }
}
