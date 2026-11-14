# Parameter Package Improvement Implementation Plan

> **For agentic workers:** Use superpowers:executing-plans or subagent-driven-development. Steps use checkbox syntax for tracking.

**Goal:** High-value tests and forced bugfixes for `simulation.parameter` and `bb4-optimization` redistribution, then maintainability / Scala 3 cleanup.

**Architecture:** Library contracts tested in bb4-optimization; simulator wiring in bb4-simulations. Two phases.

**Tech Stack:** Scala 3.10, ScalaTest AnyFunSuite, Gradle

## Global Constraints

- Cross-repo fixes allowed when tests pin a bug
- No ScalaCheck; seeded Random + tolerances
- No new redistribution types / Swing rewrite / Snake-Trebuchet changes
- Phase 2 must not regress Phase 1

---

### Task 1: Optimization redistribution tests + fixes

**Files:**
- Modify: `bb4-optimization/scala-test/.../redistribution/RedistributionSuite.scala`
- Possibly: `GaussianRedistribution.scala`, `UniformRedistribution.scala`
- Possibly: parameter type suites for seeded sampling

- [ ] Add narrow Gaussian / discrete sum=1 / boolean skew / invertibility tests
- [ ] Fix forced library bugs
- [ ] `./gradlew test --tests 'com.barrybecker4.optimization.parameter.redistribution.*'`

### Task 2: Simulator histogram fix

**Files:**
- Modify: `ParameterSimulator.scala`, `ParameterSimulatorHistogramSuite.scala`

- [ ] Correct offset or use `LinearFunction(Range, n)`
- [ ] Update tests for `min ≠ 0`
- [ ] Run parameter tests

### Task 3: Sampling modes + comments

**Files:**
- Modify: `ParameterSimulator.scala`
- Create/Modify: sampling suite

- [ ] Seeded true/false redistribution tests
- [ ] Fix inverted class comment

### Task 4: Dialog + enum tracking

**Files:**
- Modify: `ParameterSimulator.scala`, `ParameterOptionsDialog.scala`
- Create/Modify: dialog/type suites

- [ ] Track selected `ParameterDistributionType`
- [ ] Sync combo on open; ordinal wiring tests

### Task 5: Phase 2 cleanup

- [ ] Scala 3 style on dialog; typed simulator; Consts collapse; dead code; `LinearFunction(Range)`

### Task 6: Verify

```bash
# bb4-optimization
./gradlew test --tests 'com.barrybecker4.optimization.parameter.redistribution.*'
./gradlew test --tests 'com.barrybecker4.optimization.parameter.types.*'

# bb4-simulations
./gradlew test --tests 'com.barrybecker4.simulation.parameter.*'
```
