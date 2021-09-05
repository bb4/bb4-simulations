/*
 * Copyright by Barry G. Becker, 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */

package com.barrybecker4.simulation.waveFunctionCollapse.model

class WaveCell(
  var enabled: Array[Boolean] = null,
  var compatible: Array[IntArray] = null,
  var observed: Int = 0,
  var sumOfOnes: Int = 0,
  var sumOfWeights: Double = 0,
  var sumOfWeightLogWeights: Double = 0,
  var entropy: Double = 0) {

  def updateEntropy(weight: Double, weightLogWeight: Double): Unit = {
    var sum = sumOfWeights
    entropy += sumOfWeightLogWeights / sum - Math.log(sum)

    sumOfOnes -= 1
    sumOfWeights -= weight
    sumOfWeightLogWeights -= weightLogWeight

    sum = sumOfWeights
    entropy -= sumOfWeightLogWeights / sum - Math.log(sum)
  }
}