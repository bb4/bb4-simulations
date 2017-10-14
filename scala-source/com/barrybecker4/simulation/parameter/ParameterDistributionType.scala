// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.parameter

import com.barrybecker4.optimization.parameter.redistribution._
import com.barrybecker4.optimization.parameter.types.{BooleanParameter, DoubleParameter, IntegerParameter, Parameter}


/**
  * Different types of parameter distributions to test.
  * Since we apply a redistribution to the original skewed distribution,
  * we expect the result to be close to a uniform distribution.
  * @author Barry Becker
  */
object ParameterDistributionType extends Enumeration {

  val DEFAULT_NAME = "Param"
  val NUM_DISCRETES = 10
  val NUM_DISCRETESM1: Int = NUM_DISCRETES - 1

  case class Val(name: String, param: Parameter, redistFunction: RedistributionFunction) extends super.Val {
    if (redistFunction != null) this.param.setRedistributionFunction(redistFunction)
  }
  implicit def valueToFunctionTypeVal(x: Value): Val = x.asInstanceOf[Val]

  val GAUSSIAN8 = Val("Gaussian 0-8",
    new DoubleParameter(0, 0, 8.0, DEFAULT_NAME), new GaussianRedistribution(0.5, 0.3))
  val GAUSSIAN_NARROW1 = Val("Gaussian (narrow 0-1)",
    new DoubleParameter(0, 0, 1.0, DEFAULT_NAME), new GaussianRedistribution(0.5, 0.15))
  val GAUSSIAN_NARROW8 = Val("Gaussian (narrow 0-8)",
    new DoubleParameter(0, 0, 8.0, DEFAULT_NAME), new GaussianRedistribution(0.5, 0.15))
  val GAUSSIAN_NARROWER = Val("Gaussian (narrower)",
    new DoubleParameter(0, 0, 5.0, DEFAULT_NAME), new GaussianRedistribution(0.5, 0.1))
  val GAUSSIAN_NARROWEST = Val("Gaussian (narrowest)",
    new DoubleParameter(0, 0, 5.0, DEFAULT_NAME), new GaussianRedistribution(0.5, 0.01)) // still has a problem
  val GAUSSIAN_WIDE = Val("Gaussian (wide)",
    new DoubleParameter(0, 0, 5.0, DEFAULT_NAME), new GaussianRedistribution(0.5, 0.8))
  val UNIFORM = Val("Uniform", new DoubleParameter(0, 0, 5.0, DEFAULT_NAME), null)
  val RIGHT_SKEWED_GAUSSIAN = Val("Right skewed gaussian",
    new DoubleParameter(0, 0, 10.0, DEFAULT_NAME), new GaussianRedistribution(0.7, 0.3))
  val RIGHT_SKEWED_GAUSSIAN1 = Val("Right skewed gaussian1",
    new DoubleParameter(0, 0, 10.0, DEFAULT_NAME), new GaussianRedistribution(0.8, 0.1))
  val RIGHT_SKEWED_GAUSSIAN2 = Val("Right skewed gaussian2",
    new DoubleParameter(0, 0, 10.0, DEFAULT_NAME), new GaussianRedistribution(0.9, 0.05))
  val RIGHT_SKEWED_GAUSSIAN3 = Val("Right skewed gaussian3",
    new DoubleParameter(0, 0, 10.0, DEFAULT_NAME), new GaussianRedistribution(0.9, 0.5))
  val LEFT_SKEWED_GAUSSIAN = Val("Left skewed gaussian",
    new DoubleParameter(0, 0, 10.0, DEFAULT_NAME), new GaussianRedistribution(0.3, 0.3))
  val LEFT_SKEWED_GAUSSIAN1 = Val("Left skewed gaussian1",
    new DoubleParameter(0, 0, 10.0, DEFAULT_NAME), new GaussianRedistribution(0.2, 0.1))
  val LEFT_SKEWED_GAUSSIAN2 = Val("Left skewed gaussian2",
    new DoubleParameter(0, 0, 10.0, DEFAULT_NAME), new GaussianRedistribution(0.1, 0.05))
  val LEFT_SKEWED_GAUSSIAN3 = Val("Left skewed gaussian3",
    new DoubleParameter(0, 0, 10.0, DEFAULT_NAME), new GaussianRedistribution(0.1, 0.7))
  val MIN_SKEWED_GAUSSIAN = Val("Minimum skewed gaussian",
    new DoubleParameter(0, 0, 10.0, DEFAULT_NAME), new GaussianRedistribution(0.0, 0.1))
  val MAX_SKEWED_GAUSSIAN = Val("Maximum skewed gaussian",
    new DoubleParameter(0, 0, 10.0, DEFAULT_NAME), new GaussianRedistribution(1.0, 0.1))
  val SPECIAL_UNIFORM2 = Val("Special Uniform 0.2",
    new DoubleParameter(0, 0, 5.0, DEFAULT_NAME), new UniformRedistribution(Array(0.2), Array(0.01)))
  val SPECIAL_UNIFORM_LEFT = Val("Special Uniform Left only",
    new DoubleParameter(0, 0, 5.0, DEFAULT_NAME), new UniformRedistribution(Array(0.0), Array(0.01)))
  val SPECIAL_UNIFORM_RIGHT = Val("Special Uniform Right only",
    new DoubleParameter(0, 0, 5.0, DEFAULT_NAME), new UniformRedistribution(Array(1.0), Array(0.02)))
  val SPECIAL_UNIFORM_LEFTRIGHT = Val("Special Uniform Left and Right only",
    new DoubleParameter(0, 0, 5.0, DEFAULT_NAME), new UniformRedistribution(Array(0.0, 1.0), Array(0.005, 0.005)))
  val SPECIAL_UNIFORM_SEVERAL = Val("Special Uniform Several",
    new DoubleParameter(0, 0, 5.0, DEFAULT_NAME),
    new UniformRedistribution(Array(0.1, 0.3, 0.8, 0.832), Array(0.01, 0.02, 0.01, 0.01)))
  val DISCRETE_UNIFORM = Val("Uniform discrete",
    new IntegerParameter(0, 0, NUM_DISCRETESM1, DEFAULT_NAME), null)
  val SPECIFIC_DISCRETE_1 = Val("Uniform discrete1",
    new IntegerParameter(0, 0, NUM_DISCRETESM1, DEFAULT_NAME),
    new DiscreteRedistribution(NUM_DISCRETES, Array(3), Array(0.8)))
  val SPECIFIC_DISCRETE_2 = Val("Uniform discrete (0, 4)",
    new IntegerParameter(0, 0, NUM_DISCRETESM1, DEFAULT_NAME),
    new DiscreteRedistribution(NUM_DISCRETES, Array(0, 4), Array(0.3, 0.3)))
  val SPECIFIC_DISCRETE_ALL = Val("Uniform discrete (all)",
    new IntegerParameter(0, 0, 3, DEFAULT_NAME),
    new DiscreteRedistribution(4, Array(0, 1, 2, 3), Array(0.25, 0.25, 0.25, 0.25)))
  val SPECIFIC_DISCRETE_3 = Val("Uniform discrete (1, 2, 6)",
    new IntegerParameter(0, 0, NUM_DISCRETESM1, DEFAULT_NAME),
    new DiscreteRedistribution(NUM_DISCRETES, Array(1, 2, 6), Array(0.3, 0.3, 0.3)))
  val SPECIFIC_DISCRETE_3a = Val("Uniform discrete3a",
    new IntegerParameter(0, 0, NUM_DISCRETESM1, DEFAULT_NAME),
    new DiscreteRedistribution(NUM_DISCRETES, Array(1, 2, 6), Array(0.2, 0.6, 0.2)))   // still has a problem
  val BOOLEAN = Val("Boolean Uniform", new BooleanParameter(false, "Param"), null)
  val BOOLEAN_SKEWED = Val("Boolean Skewed",
    new BooleanParameter(false, "Param"), new BooleanRedistribution(0.8))
  
  val VALUES: Array[Val] = Array(GAUSSIAN8, GAUSSIAN_NARROW1,
    GAUSSIAN_NARROW8, GAUSSIAN_NARROWER, GAUSSIAN_NARROWEST, GAUSSIAN_WIDE, UNIFORM,
    RIGHT_SKEWED_GAUSSIAN, RIGHT_SKEWED_GAUSSIAN1, RIGHT_SKEWED_GAUSSIAN2, RIGHT_SKEWED_GAUSSIAN3,
    LEFT_SKEWED_GAUSSIAN, LEFT_SKEWED_GAUSSIAN1, LEFT_SKEWED_GAUSSIAN2, LEFT_SKEWED_GAUSSIAN3,
    MIN_SKEWED_GAUSSIAN, MAX_SKEWED_GAUSSIAN, SPECIAL_UNIFORM2, SPECIAL_UNIFORM_LEFT, SPECIAL_UNIFORM_RIGHT,
    SPECIAL_UNIFORM_LEFTRIGHT, SPECIAL_UNIFORM_SEVERAL, DISCRETE_UNIFORM,
    SPECIFIC_DISCRETE_1, SPECIFIC_DISCRETE_2, SPECIFIC_DISCRETE_ALL, SPECIFIC_DISCRETE_3, SPECIFIC_DISCRETE_3a,
    BOOLEAN, BOOLEAN_SKEWED
  )
}