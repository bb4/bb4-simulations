// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.parameter

import com.barrybecker4.optimization.parameter.redistribution._
import com.barrybecker4.optimization.parameter.types.{BooleanParameter, DoubleParameter, IntegerParameter, Parameter}
import ParameterDistributionType._


object ParameterDistributionType {
  val DEFAULT_NAME = "Param"
  val NUM_DISCRETES = 10
  val NUM_DISCRETESM1: Int = NUM_DISCRETES - 1
}

/**
  * Different types of parameter distributions to test.
  * Since we apply a redistribution to the original skewed distribution,
  * we expect the result to be close to a uniform distribution.
  * @author Barry Becker
  */
enum ParameterDistributionType(val name: String, val param: Parameter):

  case GAUSSIAN8 extends ParameterDistributionType("Gaussian 0-8",
    new DoubleParameter(0, 0, 8.0, ParameterDistributionType.DEFAULT_NAME,
    Some(new GaussianRedistribution(0.5, 0.3))))
  case GAUSSIAN_NARROW1 extends ParameterDistributionType("Gaussian (narrow 0-1)",
    new DoubleParameter(0, 0, 1.0, ParameterDistributionType.DEFAULT_NAME,
    Some(new GaussianRedistribution(0.5, 0.15))))
  case GAUSSIAN_NARROW8 extends ParameterDistributionType("Gaussian (narrow 0-8)",
    new DoubleParameter(0, 0, 8.0, ParameterDistributionType.DEFAULT_NAME,
    Some(new GaussianRedistribution(0.5, 0.15))))
  case GAUSSIAN_NARROWER extends ParameterDistributionType("Gaussian (narrower)",
    new DoubleParameter(0, 0, 5.0, ParameterDistributionType.DEFAULT_NAME,
    Some(new GaussianRedistribution(0.5, 0.1))))
  case GAUSSIAN_NARROWEST  extends ParameterDistributionType("Gaussian (narrowest)",
    new DoubleParameter(0, 0, 5.0, ParameterDistributionType.DEFAULT_NAME,
    Some(new GaussianRedistribution(0.5, 0.01)))) // still has a problem
  case GAUSSIAN_WIDE extends ParameterDistributionType("Gaussian (wide)",
    new DoubleParameter(0, 0, 5.0, ParameterDistributionType.DEFAULT_NAME,
    Some(new GaussianRedistribution(0.5, 0.8))))
  case UNIFORM extends ParameterDistributionType("Uniform",
    new DoubleParameter(0, 0, 5.0, ParameterDistributionType.DEFAULT_NAME))
  case RIGHT_SKEWED_GAUSSIAN extends ParameterDistributionType("Right skewed gaussian",
    new DoubleParameter(0, 0, 10.0, ParameterDistributionType.DEFAULT_NAME,
    Some(new GaussianRedistribution(0.7, 0.3))))
  case RIGHT_SKEWED_GAUSSIAN1 extends ParameterDistributionType("Right skewed gaussian1",
    new DoubleParameter(0, 0, 10.0, ParameterDistributionType.DEFAULT_NAME,
    Some(new GaussianRedistribution(0.8, 0.1))))
  case RIGHT_SKEWED_GAUSSIAN2 extends ParameterDistributionType("Right skewed gaussian2",
    new DoubleParameter(0, 0, 10.0, ParameterDistributionType.DEFAULT_NAME,
    Some(new GaussianRedistribution(0.9, 0.05))))
  case RIGHT_SKEWED_GAUSSIAN3 extends ParameterDistributionType("Right skewed gaussian3",
    new DoubleParameter(0, 0, 10.0, ParameterDistributionType.DEFAULT_NAME,
      Some(new GaussianRedistribution(0.9, 0.5))))
  case LEFT_SKEWED_GAUSSIAN extends ParameterDistributionType("Left skewed gaussian",
    new DoubleParameter(0, 0, 10.0, ParameterDistributionType.DEFAULT_NAME,
    Some(new GaussianRedistribution(0.3, 0.3))))
  case LEFT_SKEWED_GAUSSIAN1 extends ParameterDistributionType("Left skewed gaussian1",
    new DoubleParameter(0, 0, 10.0, ParameterDistributionType.DEFAULT_NAME,
    Some(new GaussianRedistribution(0.2, 0.1))))
  case LEFT_SKEWED_GAUSSIAN2 extends ParameterDistributionType("Left skewed gaussian2",
    new DoubleParameter(0, 0, 10.0, ParameterDistributionType.DEFAULT_NAME,
    Some(new GaussianRedistribution(0.1, 0.05))))
  case LEFT_SKEWED_GAUSSIAN3 extends ParameterDistributionType("Left skewed gaussian3",
    new DoubleParameter(0, 0, 10.0, ParameterDistributionType.DEFAULT_NAME,
    Some(new GaussianRedistribution(0.1, 0.7))))
  case MIN_SKEWED_GAUSSIAN extends ParameterDistributionType("Minimum skewed gaussian",
    new DoubleParameter(0, 0, 10.0, ParameterDistributionType.DEFAULT_NAME,
    Some(new GaussianRedistribution(0.0, 0.1))))
  case MAX_SKEWED_GAUSSIAN extends ParameterDistributionType("Maximum skewed gaussian",
    new DoubleParameter(0, 0, 10.0, ParameterDistributionType.DEFAULT_NAME,
    Some(new GaussianRedistribution(1.0, 0.1))))
  case SPECIAL_UNIFORM2 extends ParameterDistributionType("Special Uniform 0.2",
    new DoubleParameter(0, 0, 5.0, ParameterDistributionType.DEFAULT_NAME,
      Some(new UniformRedistribution(Array(0.2), Array(0.01)))))
  case SPECIAL_UNIFORM_LEFT extends ParameterDistributionType("Special Uniform Left only",
    new DoubleParameter(0, 0, 5.0, ParameterDistributionType.DEFAULT_NAME,
      Some(new UniformRedistribution(Array(0.0), Array(0.01)))))
  case SPECIAL_UNIFORM_RIGHT extends ParameterDistributionType("Special Uniform Right only",
    new DoubleParameter(0, 0, 5.0, ParameterDistributionType.DEFAULT_NAME,
      Some(new UniformRedistribution(Array(1.0), Array(0.02)))))
  case SPECIAL_UNIFORM_LEFTRIGHT extends ParameterDistributionType("Special Uniform Left and Right only",
    new DoubleParameter(0, 0, 5.0, ParameterDistributionType.DEFAULT_NAME,
      Some(new UniformRedistribution(Array(0.0, 1.0), Array(0.005, 0.005)))))
  case SPECIAL_UNIFORM_SEVERAL extends ParameterDistributionType("Special Uniform Several",
    new DoubleParameter(0, 0, 5.0, ParameterDistributionType.DEFAULT_NAME,
    Some(new UniformRedistribution(Array(0.1, 0.3, 0.8, 0.832), Array(0.01, 0.02, 0.01, 0.01)))))
  case DISCRETE_UNIFORM extends ParameterDistributionType("Uniform discrete",
    new IntegerParameter(0, 0, ParameterDistributionType.NUM_DISCRETESM1, ParameterDistributionType.DEFAULT_NAME))
  case SPECIFIC_DISCRETE_1 extends ParameterDistributionType("Uniform discrete1",
    new IntegerParameter(0, 0, ParameterDistributionType.NUM_DISCRETESM1, ParameterDistributionType.DEFAULT_NAME,
    Some(DiscreteRedistribution(ParameterDistributionType.NUM_DISCRETES, Array(3), Array(0.8)))))
  case SPECIFIC_DISCRETE_2 extends ParameterDistributionType("Uniform discrete (0, 4)",
    new IntegerParameter(0, 0, ParameterDistributionType.NUM_DISCRETESM1, ParameterDistributionType.DEFAULT_NAME,
    Some(DiscreteRedistribution(NUM_DISCRETES, Array(0, 4), Array(0.3, 0.3)))))
  case SPECIFIC_DISCRETE_ALL extends ParameterDistributionType("Uniform discrete (all)",
    new IntegerParameter(0, 0, 3, DEFAULT_NAME,
    Some(DiscreteRedistribution(4, Array(0, 1, 2, 3), Array(0.25, 0.25, 0.25, 0.25)))))
  case SPECIFIC_DISCRETE_3 extends ParameterDistributionType("Uniform discrete (1, 2, 6)",
    new IntegerParameter(0, 0, NUM_DISCRETESM1, DEFAULT_NAME,
    Some(DiscreteRedistribution(NUM_DISCRETES, Array(1, 2, 6), Array(0.3, 0.3, 0.3)))))
  case SPECIFIC_DISCRETE_3a extends ParameterDistributionType("Uniform discrete3a",
    new IntegerParameter(0, 0, NUM_DISCRETESM1, DEFAULT_NAME,
    Some(DiscreteRedistribution(NUM_DISCRETES, Array(1, 2, 6), Array(0.2, 0.6, 0.2)))))   // still has a problem
  case BOOLEAN extends ParameterDistributionType("Boolean Uniform", new BooleanParameter(false, "Param"))
  case BOOLEAN_SKEWED extends ParameterDistributionType("Boolean Skewed",
    new BooleanParameter(false, "Param", Some(new BooleanRedistribution(0.8))))
