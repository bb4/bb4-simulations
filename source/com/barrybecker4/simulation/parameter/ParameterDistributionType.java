/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.parameter;

import com.barrybecker4.optimization.parameter.redistribution.BooleanRedistribution;
import com.barrybecker4.optimization.parameter.redistribution.DiscreteRedistribution;
import com.barrybecker4.optimization.parameter.redistribution.GaussianRedistribution;
import com.barrybecker4.optimization.parameter.redistribution.RedistributionFunction;
import com.barrybecker4.optimization.parameter.redistribution.UniformRedistribution;
import com.barrybecker4.optimization.parameter.types.BooleanParameter;
import com.barrybecker4.optimization.parameter.types.DoubleParameter;
import com.barrybecker4.optimization.parameter.types.IntegerParameter;
import com.barrybecker4.optimization.parameter.types.Parameter;

/**
 * Different types of parameter distributions to test.
 * Since we apply a redistribution to the original skewed distribution,
 * we expect the result to be a uniform distribution.
 *
 * @author Barry Becker
 */
public enum ParameterDistributionType {
    GAUSSIAN8("Gaussian 0-8",
            new DoubleParameter(0, 0, 8.0, getDefaultName()),
            new GaussianRedistribution(0.5, 0.3)),
    GAUSSIAN_NARROW1("Gaussian (narrow 0-1)",
            new DoubleParameter(0, 0, 1.0, getDefaultName()),
            new GaussianRedistribution(0.5, 0.15)),
    GAUSSIAN_NARROW8("Gaussian (narrow 0-8)",
            new DoubleParameter(0, 0, 8.0, getDefaultName()),
            new GaussianRedistribution(0.5, 0.15)),
    GAUSSIAN_NARROWER("Gaussian (narrower)",
            new DoubleParameter(0, 0, 5.0, getDefaultName()),
            new GaussianRedistribution(0.5, 0.1)),
    GAUSSIAN_NARROWEST("Gaussian (narrowest)",
            new DoubleParameter(0, 0, 5.0, getDefaultName()),
            new GaussianRedistribution(0.5, 0.01)), // still has a problem
    GAUSSIAN_WIDE("Gaussian (wide)",
            new DoubleParameter(0, 0, 5.0, getDefaultName()),
            new GaussianRedistribution(0.5, 0.8)),
    UNIFORM("Uniform",
            new DoubleParameter(0, 0, 5.0, getDefaultName()), null),
    RIGHT_SKEWED_GAUSSIAN("Right skewed gaussian",
            new DoubleParameter(0, 0, 10.0, getDefaultName()),
            new GaussianRedistribution(0.7, 0.3)),
    RIGHT_SKEWED_GAUSSIAN1("Right skewed gaussian1",
            new DoubleParameter(0, 0, 10.0, getDefaultName()),
            new GaussianRedistribution(0.8, 0.1)),
    RIGHT_SKEWED_GAUSSIAN2("Right skewed gaussian2",
            new DoubleParameter(0, 0, 10.0, getDefaultName()),
            new GaussianRedistribution(0.9, 0.05)),
    RIGHT_SKEWED_GAUSSIAN3("Right skewed gaussian3",
            new DoubleParameter(0, 0, 10.0, getDefaultName()),
            new GaussianRedistribution(0.9, 0.5)),
    LEFT_SKEWED_GAUSSIAN("Left skewed gaussian",
            new DoubleParameter(0, 0, 10.0, getDefaultName()),
            new GaussianRedistribution(0.3, 0.3)),
    LEFT_SKEWED_GAUSSIAN1("Left skewed gaussian1",
            new DoubleParameter(0, 0, 10.0, getDefaultName()),
            new GaussianRedistribution(0.2, 0.1)),
    LEfT_SKEWED_GAUSSIAN2("Left skewed gaussian2",
            new DoubleParameter(0, 0, 10.0, getDefaultName()),
            new GaussianRedistribution(0.1, 0.05)),
    LEfT_SKEWED_GAUSSIAN3("Left skewed gaussian3",
            new DoubleParameter(0, 0, 10.0, getDefaultName()),
            new GaussianRedistribution(0.1, 0.7)),
    MIN_SKEWED_GAUSSIAN("Minimum skewed gaussian",
            new DoubleParameter(0, 0, 10.0, getDefaultName()),
            new GaussianRedistribution(0.0, 0.1)),
    MAX_SKEWED_GAUSSIAN("Maximum skewed gaussian",
            new DoubleParameter(0, 0, 10.0, getDefaultName()),
            new GaussianRedistribution(1.0, 0.1)),
    SPECIAL_UNIFORM2("Special Uniform 0.2",
            new DoubleParameter(0, 0, 5.0, getDefaultName()),
            getUniform1()),
    SPECIAL_UNIFORM_LEFT("Special Uniform Left only",
            new DoubleParameter(0, 0, 5.0, getDefaultName()),
            getUniformLeft()),
    SPECIAL_UNIFORM_RIGHT("Special Uniform Right only",
            new DoubleParameter(0, 0, 5.0, getDefaultName()),
            getUniformRight()),
    SPECIAL_UNIFORM_LEFTRIGHT("Special Uniform Left and Right only",
            new DoubleParameter(0, 0, 5.0, getDefaultName()),
            getUniformLeftRight()),
    SPECIAL_UNIFORM_SEVERAL("Special Uniform Several",
            new DoubleParameter(0, 0, 5.0, getDefaultName()),
            getUniformSeveral()),
    DISCRETE_UNIFORM("Uniform discrete",
            new IntegerParameter(0, 0, getNumDiscretes()-1, getDefaultName()), null),
    SPECIFIC_DISCRETE_1("Uniform discrete1",
            new IntegerParameter(0, 0, getNumDiscretes()-1, getDefaultName()),
            getDiscUniform1()),
    SPECIFIC_DISCRETE_2("Uniform discrete (0, 4)",
            new IntegerParameter(0, 0, getNumDiscretes()-1, getDefaultName()),
            getDiscUniform2()),
    SPECIFIC_DISCRETE_ALL("Uniform discrete (all)",
            new IntegerParameter(0, 0, 3, getDefaultName()),  getDiscUniformAll()),
    SPECIFIC_DISCRETE_3("Uniform discrete (1, 2, 6)",
            new IntegerParameter(0, 0, getNumDiscretes()-1, getDefaultName()),
            getDiscUniform3()),
    SPECIFIC_DISCRETE_3a("Uniform discrete3a",
            new IntegerParameter(0, 0, getNumDiscretes()-1, getDefaultName()),
            getDiscUniform3a()),   // still has a problem
    BOOLEAN("Boolean Uniform",
            new BooleanParameter(false, "Param"), null),
    BOOLEAN_SKEWED("Boolean Skewed",
            new BooleanParameter(false, "Param"),
            new BooleanRedistribution(0.8));

    private String name;
    private Parameter param;

    /**
     * Constructor
     */
    ParameterDistributionType(String name, Parameter param, RedistributionFunction redistFunction) {
        this.name = name;
        this.param = param;
        if (redistFunction != null) {
            this.param.setRedistributionFunction(redistFunction);
        }
    }

    public Parameter getParameter() {
        return param;
    }

    private static int getNumDiscretes() {
        return 10;
    }
    private static String getDefaultName() {
        return "Param";
    }

    private static RedistributionFunction getUniform1() {
        final double[] values = {0.2};
        final double[] valueProbs = {0.01};
        return new UniformRedistribution(values, valueProbs);
    }
    private static RedistributionFunction getUniformLeft() {
        final double[] values = {0.0, };
        final double[] valueProbs = {0.01};
        return new UniformRedistribution(values, valueProbs);
    }
    private static RedistributionFunction getUniformRight() {
        final double[] values = {1.0};
        final double[] valueProbs = {0.02};
        return new UniformRedistribution(values, valueProbs);
    }
    private static RedistributionFunction getUniformLeftRight() {
        final double[] values = {0.0, 1.0};
        final double[] valueProbs = {0.005, 0.005};
        return new UniformRedistribution(values, valueProbs);
    }
    private static RedistributionFunction getUniformSeveral() {
        final double[] values = {0.1, 0.3, 0.8, 0.832};
        final double[] valueProbs = {0.01, 0.02, 0.01, 0.01};
        return new UniformRedistribution(values, valueProbs);
    }

    private static RedistributionFunction getDiscUniform1() {
        final int[] values = {3};
        final double[] valueProbs = {0.8};
        return new DiscreteRedistribution(getNumDiscretes(), values, valueProbs);
    }
    private static RedistributionFunction getDiscUniform2() {
        final int[] values = {0, 4};
        final double[] valueProbs = {0.3, 0.3};
        return new DiscreteRedistribution(getNumDiscretes(), values, valueProbs);
    }
    private static RedistributionFunction getDiscUniform3() {
        final int[] values = {1, 2, 6};
        final double[] valueProbs = {0.3, 0.3, 0.3};
        return new DiscreteRedistribution(getNumDiscretes(), values, valueProbs);
    }
    private static RedistributionFunction getDiscUniform3a() {
        final int[] values = {1, 2, 6};
        final double[] valueProbs = {0.2, 0.6, 0.2};
        return new DiscreteRedistribution(getNumDiscretes(), values, valueProbs);
    }
    private static RedistributionFunction getDiscUniformAll() {
        final int[] values = {0, 1, 2, 3};
        final double[] valueProbs = {0.25, 0.25, 0.25, 0.25};
        return new DiscreteRedistribution(4, values, valueProbs);
    }


    public String toString() {
        return name;
    }
}
