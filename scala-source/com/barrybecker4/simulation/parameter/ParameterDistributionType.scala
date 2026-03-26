// Copyright by Barry G. Becker, 2016-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.parameter

import com.barrybecker4.optimization.parameter.redistribution._
import com.barrybecker4.optimization.parameter.types.{BooleanParameter, DoubleParameter, IntegerParameter, Parameter}

/** Discrete bin counts for enum case initializers; must not read the enum companion during static init. */
private object Consts:
  val NumDiscretes: Int = 10
  val NumDiscretesM1: Int = NumDiscretes - 1

/**
  * Different types of parameter distributions to test.
  * Since we apply a redistribution to the original skewed distribution,
  * we expect the result to be close to a uniform distribution.
  * @author Barry Becker
  */
enum ParameterDistributionType(val name: String, val param: Parameter):

  case GAUSSIAN8 extends ParameterDistributionType("Gaussian 0-8",
    ParameterDistributionType.doubleGaussian(8.0, 0.5, 0.3))
  case GAUSSIAN_NARROW1 extends ParameterDistributionType("Gaussian (narrow 0-1)",
    ParameterDistributionType.doubleGaussian(1.0, 0.5, 0.15))
  case GAUSSIAN_NARROW8 extends ParameterDistributionType("Gaussian (narrow 0-8)",
    ParameterDistributionType.doubleGaussian(8.0, 0.5, 0.15))
  case GAUSSIAN_NARROWER extends ParameterDistributionType("Gaussian (narrower)",
    ParameterDistributionType.doubleGaussian(5.0, 0.5, 0.1))
  case GAUSSIAN_NARROWEST extends ParameterDistributionType("Gaussian (narrowest)",
    ParameterDistributionType.doubleGaussian(5.0, 0.5, 0.01)) // still has a problem
  case GAUSSIAN_WIDE extends ParameterDistributionType("Gaussian (wide)",
    ParameterDistributionType.doubleGaussian(5.0, 0.5, 0.8))
  case UNIFORM extends ParameterDistributionType("Uniform",
    ParameterDistributionType.doubleUniform(5.0))
  case RIGHT_SKEWED_GAUSSIAN extends ParameterDistributionType("Right skewed gaussian",
    ParameterDistributionType.doubleGaussian(10.0, 0.7, 0.3))
  case RIGHT_SKEWED_GAUSSIAN1 extends ParameterDistributionType("Right skewed gaussian1",
    ParameterDistributionType.doubleGaussian(10.0, 0.8, 0.1))
  case RIGHT_SKEWED_GAUSSIAN2 extends ParameterDistributionType("Right skewed gaussian2",
    ParameterDistributionType.doubleGaussian(10.0, 0.9, 0.05))
  case RIGHT_SKEWED_GAUSSIAN3 extends ParameterDistributionType("Right skewed gaussian3",
    ParameterDistributionType.doubleGaussian(10.0, 0.9, 0.5))
  case LEFT_SKEWED_GAUSSIAN extends ParameterDistributionType("Left skewed gaussian",
    ParameterDistributionType.doubleGaussian(10.0, 0.3, 0.3))
  case LEFT_SKEWED_GAUSSIAN1 extends ParameterDistributionType("Left skewed gaussian1",
    ParameterDistributionType.doubleGaussian(10.0, 0.2, 0.1))
  case LEFT_SKEWED_GAUSSIAN2 extends ParameterDistributionType("Left skewed gaussian2",
    ParameterDistributionType.doubleGaussian(10.0, 0.1, 0.05))
  case LEFT_SKEWED_GAUSSIAN3 extends ParameterDistributionType("Left skewed gaussian3",
    ParameterDistributionType.doubleGaussian(10.0, 0.1, 0.7))
  case MIN_SKEWED_GAUSSIAN extends ParameterDistributionType("Minimum skewed gaussian",
    ParameterDistributionType.doubleGaussian(10.0, 0.0, 0.1))
  case MAX_SKEWED_GAUSSIAN extends ParameterDistributionType("Maximum skewed gaussian",
    ParameterDistributionType.doubleGaussian(10.0, 1.0, 0.1))
  case SPECIAL_UNIFORM2 extends ParameterDistributionType("Special Uniform 0.2",
    ParameterDistributionType.doubleUniformRedist(5.0, Array(0.2), Array(0.01)))
  case SPECIAL_UNIFORM_LEFT extends ParameterDistributionType("Special Uniform Left only",
    ParameterDistributionType.doubleUniformRedist(5.0, Array(0.0), Array(0.01)))
  case SPECIAL_UNIFORM_RIGHT extends ParameterDistributionType("Special Uniform Right only",
    ParameterDistributionType.doubleUniformRedist(5.0, Array(1.0), Array(0.02)))
  case SPECIAL_UNIFORM_LEFTRIGHT extends ParameterDistributionType("Special Uniform Left and Right only",
    ParameterDistributionType.doubleUniformRedist(5.0, Array(0.0, 1.0), Array(0.005, 0.005)))
  case SPECIAL_UNIFORM_SEVERAL extends ParameterDistributionType("Special Uniform Several",
    ParameterDistributionType.doubleUniformRedist(5.0,
      Array(0.1, 0.3, 0.8, 0.832), Array(0.01, 0.02, 0.01, 0.01)))
  case DISCRETE_UNIFORM extends ParameterDistributionType("Uniform discrete",
    ParameterDistributionType.integerUniform(Consts.NumDiscretesM1))
  case SPECIFIC_DISCRETE_1 extends ParameterDistributionType("Uniform discrete1",
    ParameterDistributionType.integerDiscrete(Consts.NumDiscretesM1,
      DiscreteRedistribution(Consts.NumDiscretes, Array(3), Array(0.8))))
  case SPECIFIC_DISCRETE_2 extends ParameterDistributionType("Uniform discrete (0, 4)",
    ParameterDistributionType.integerDiscrete(Consts.NumDiscretesM1,
      DiscreteRedistribution(Consts.NumDiscretes, Array(0, 4), Array(0.3, 0.3))))
  case SPECIFIC_DISCRETE_ALL extends ParameterDistributionType("Uniform discrete (all)",
    ParameterDistributionType.integerDiscrete(3,
      DiscreteRedistribution(4, Array(0, 1, 2, 3), Array(0.25, 0.25, 0.25, 0.25))))
  case SPECIFIC_DISCRETE_3 extends ParameterDistributionType("Uniform discrete (1, 2, 6)",
    ParameterDistributionType.integerDiscrete(Consts.NumDiscretesM1,
      DiscreteRedistribution(Consts.NumDiscretes, Array(1, 2, 6), Array(0.3, 0.3, 0.3))))
  case SPECIFIC_DISCRETE_3a extends ParameterDistributionType("Uniform discrete3a",
    ParameterDistributionType.integerDiscrete(Consts.NumDiscretesM1,
      DiscreteRedistribution(Consts.NumDiscretes, Array(1, 2, 6), Array(0.2, 0.6, 0.2)))) // still has a problem
  case BOOLEAN extends ParameterDistributionType("Boolean Uniform", ParameterDistributionType.booleanUniform)
  case BOOLEAN_SKEWED extends ParameterDistributionType("Boolean Skewed",
    ParameterDistributionType.booleanSkewed(0.8))

end ParameterDistributionType

object ParameterDistributionType:

  private[parameter] val DefaultName = "Param"
  private[parameter] val NumDiscretes: Int = Consts.NumDiscretes
  private[parameter] val NumDiscretesM1: Int = Consts.NumDiscretesM1

  private[parameter] def doubleUniform(max: Double): DoubleParameter =
    new DoubleParameter(0, 0, max, DefaultName)

  private[parameter] def doubleGaussian(max: Double, mean: Double, sigma: Double): DoubleParameter =
    new DoubleParameter(0, 0, max, DefaultName, Some(new GaussianRedistribution(mean, sigma)))

  private[parameter] def doubleUniformRedist(max: Double, positions: Array[Double], weights: Array[Double]): DoubleParameter =
    new DoubleParameter(0, 0, max, DefaultName, Some(new UniformRedistribution(positions, weights)))

  private[parameter] def integerUniform(maxIndex: Int): IntegerParameter =
    new IntegerParameter(0, 0, maxIndex, DefaultName)

  private[parameter] def integerDiscrete(maxIndex: Int, redistribution: DiscreteRedistribution): IntegerParameter =
    new IntegerParameter(0, 0, maxIndex, DefaultName, Some(redistribution))

  private[parameter] def booleanUniform: BooleanParameter =
    new BooleanParameter(false, DefaultName)

  private[parameter] def booleanSkewed(trueBias: Double): BooleanParameter =
    new BooleanParameter(false, DefaultName, Some(new BooleanRedistribution(trueBias)))

end ParameterDistributionType
