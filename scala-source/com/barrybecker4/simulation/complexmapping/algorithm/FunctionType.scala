/* Copyright by Barry G. Becker, 2019 - 2021. Licensed under MIT License: http://www.opensource.org/licenses/MIT*/
package com.barrybecker4.simulation.complexmapping.algorithm

import com.barrybecker4.simulation.complexmapping.algorithm.functions._


/**
  * Some functions that we can use to map complex numbers to new locations.
  * @author Barry Becker
  */
enum FunctionType(val name: String, val function: ComplexFunction):

  case IDENTITY extends FunctionType("Identity", IdentityFunction())
  case INT_POWER extends FunctionType("s ^ n, n is int; s is complex", IntPowerFunction())
  case POWER extends FunctionType("n ^ s, n is int; s is complex", PowerFunction())
  case GAMMA extends FunctionType("Gamma", GammaFunction())
  case RIEMANN_ZETA extends FunctionType("Riemann Zeta", RiemannZetaFunction())
  case DIRICHLET_ETA  extends FunctionType("Dirichlet Eta", DirichletEtaFunction())
  case ZETA_ANALYTIC_EXTENSION extends FunctionType("Analytic extension of Zeta", ZetaAnalyticExtensionFunction())

end FunctionType
