# MathUtil.RANDOM Replacement Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace all `MathUtil.RANDOM` uses with explicit `scala.util.Random` instances per issue #50 / design approach 1.

**Architecture:** Dice and Parameter each own a private `Random`. Habitat owns a seeded `Random` and threads it through Population, Creature, and package helpers; `Habitat.reset` reseeds. Benchmark drops the vestigial `MathUtil.RANDOM.setSeed`.

**Tech Stack:** Scala 3, `scala.util.Random`, Gradle (`./gradlew test`)

## Global Constraints

- Zero `MathUtil.RANDOM` references remain after completion
- Keep non-RANDOM `MathUtil` uses (`EPS`, `gcd`, `getDirectionTo`)
- Habitat reset with seed 1 stays reproducible
- Paths under `scala-source/` and `scala-test/` as listed

## File map

| File | Change |
|------|--------|
| `dice/DiceSimulator.scala` | Own `rnd`; pass to `rollSum` |
| `parameter/ParameterSimulator.scala` | Own `rnd` |
| `benchmark/BenchmarkCases.scala` | Remove `MathUtil.RANDOM.setSeed` |
| `habitat/HabitatSimulator.scala` | Drop reseed; `habitat.reset` owns it |
| `habitat/creatures/package.scala` | Helpers take `Random` |
| `habitat/creatures/Creature.scala` | Store `rnd`; pass to helpers |
| `habitat/creatures/CreatureAttributes.scala` | `flock`/`doneEating` take `rnd` |
| `habitat/creatures/CreatureProcessor.scala` | Pass `creature.rnd` |
| `habitat/creatures/populations/Population.scala` | Store/use `rnd` |
| `habitat/creatures/populations/Habitat.scala` | Own `rnd`; reseed in `reset` |
| `SerengetiHabitat` / `CatRatHabitat` / `SingleCreatureHabitat` | Pass `rnd` to `createPopulation` |
| Habitat tests | Pass `Random` where needed |
| New: habitat reset reproducibility test | Assert two resets match |

---

### Task 1: DiceSimulator + ParameterSimulator + BenchmarkCases

**Files:**
- Modify: `scala-source/com/barrybecker4/simulation/dice/DiceSimulator.scala`
- Modify: `scala-source/com/barrybecker4/simulation/parameter/ParameterSimulator.scala`
- Modify: `scala-source/com/barrybecker4/simulation/benchmark/BenchmarkCases.scala`

**Interfaces:**
- Produces: no shared API; local `private val rnd = new Random` in each simulator

- [ ] **Step 1: Update DiceSimulator**

Replace `MathUtil` with `scala.util.Random`:

```scala
import scala.util.Random
// remove: import com.barrybecker4.math.MathUtil

class DiceSimulator() extends DistributionSimulator("Dice Histogram") {
  private val options = new DiceOptions
  private val rnd = new Random
  // ...
  override protected def getXPositionToIncrement: Double =
    DiceRollLogic.rollSum(options.numDice, options.numSides, rnd)
}
```

- [ ] **Step 2: Update ParameterSimulator**

```scala
import scala.util.Random
// remove MathUtil import

class ParameterSimulator() extends DistributionSimulator("Parameter Histogram"):
  private val rnd = new Random
  // ...
  override protected def getXPositionToIncrement: Double =
    if showRedistribution then parameter.randomizeValue(rnd).getValue
    else
      val v = parameter.minValue + rnd.nextDouble() * parameter.range
      parameter.setValue(v).getValue
```

- [ ] **Step 3: Update BenchmarkCases**

Delete `import com.barrybecker4.math.MathUtil` and the line `MathUtil.RANDOM.setSeed(seed)` inside `run`. Keep `val rnd = new Random(seed)` and other `new Random(seed)` calls.

- [ ] **Step 4: Compile-check these modules**

Run: `./gradlew compileScala compileTestScala -q`
Expected: SUCCESS (habitat still uses MathUtil.RANDOM; that is fine until Task 2–3)

- [ ] **Step 5: Commit**

```bash
git add scala-source/com/barrybecker4/simulation/dice/DiceSimulator.scala \
  scala-source/com/barrybecker4/simulation/parameter/ParameterSimulator.scala \
  scala-source/com/barrybecker4/simulation/benchmark/BenchmarkCases.scala
git commit -m "replace MathUtil.RANDOM in dice, parameter, and benchmark"
```

---

### Task 2: Habitat Random plumbing (package → Creature → Population → Habitat)

**Files:**
- Modify: `scala-source/com/barrybecker4/simulation/habitat/creatures/package.scala`
- Modify: `scala-source/com/barrybecker4/simulation/habitat/creatures/Creature.scala`
- Modify: `scala-source/com/barrybecker4/simulation/habitat/creatures/CreatureAttributes.scala`
- Modify: `scala-source/com/barrybecker4/simulation/habitat/creatures/CreatureProcessor.scala`
- Modify: `scala-source/com/barrybecker4/simulation/habitat/creatures/populations/Population.scala`
- Modify: `scala-source/com/barrybecker4/simulation/habitat/creatures/populations/Habitat.scala`
- Modify: `scala-source/com/barrybecker4/simulation/habitat/creatures/populations/SerengetiHabitat.scala`
- Modify: `scala-source/com/barrybecker4/simulation/habitat/creatures/populations/CatRatHabitat.scala`
- Modify: `scala-source/com/barrybecker4/simulation/habitat/creatures/populations/SingleCreatureHabitat.scala`
- Modify: `scala-source/com/barrybecker4/simulation/habitat/HabitatSimulator.scala`
- Modify: `scala-test/com/barrybecker4/simulation/habitat/creatures/CreatureSuite.scala`
- Modify: `scala-test/com/barrybecker4/simulation/habitat/creatures/populations/PopulationSuite.scala`

**Interfaces:**
- Produces:
  - `def randomDirection(rnd: Random): Double`
  - `def perturbDirection(direction: Double, rnd: Random): Double`
  - `class Creature private[creatures](cType: CreatureType, location: Point2d, val rnd: Random)`
  - `CreatureAttributes.flock(..., rnd: Random)` and `doneEating(normalSpeed: Double, rnd: Random)`
  - `Population.createPopulation(creatureType, initialSize, rnd: Random)`
  - `Habitat` has `protected val rnd = new Random(1)`; `reset()` calls `rnd.setSeed(1)` then rebuilds

- [ ] **Step 1: Update package helpers**

```scala
package object creatures {
  val TWO_PI: Double = 2.0 * Math.PI
  def absMod(value: Double): Double = (value + 1.0) % 1.0
  def randomDirection(rnd: Random): Double = TWO_PI * rnd.nextDouble()
  def perturbDirection(direction: Double, rnd: Random): Double = {
    val perturbation = 0.1 * TWO_PI * rnd.nextDouble()
    val perturbedDirection = direction + perturbation
    if (perturbedDirection < 0) perturbedDirection + TWO_PI
    else if (perturbedDirection > TWO_PI) perturbedDirection - TWO_PI
    else perturbedDirection
  }
}
```

Add `import scala.util.Random`; remove `MathUtil` import.

- [ ] **Step 2: Update Creature**

```scala
class Creature private[creatures](var cType: CreatureType, var location: Point2d, val rnd: Random) {
  private val attributes =
    new CreatureAttributes(0, 0, 0,
      cType.normalSpeed, randomDirection(rnd), location, cType.nutritionalValue, false, false)
  // rest unchanged
}
```

- [ ] **Step 3: Update CreatureAttributes + CreatureProcessor**

```scala
def flock(cType: CreatureType, friends: List[Creature], nearestFriend: Option[Creature], rnd: Random): Unit = {
  val perturbedDirection = perturbDirection(direction, rnd)
  // ... rest unchanged
}

def doneEating(normalSpeed: Double, rnd: Random): Unit = {
  // ...
  direction = randomDirection(rnd)
}
```

In `CreatureProcessor`:
```scala
a.doneEating(cType.normalSpeed, creature.rnd)
// ...
a.flock(cType, nbrs.flockFriends, nbrs.nearestFriend, creature.rnd)
```

Remove unused `MathUtil` import from `CreatureAttributes` only if nothing else needs it — keep `MathUtil.getDirectionTo`.

- [ ] **Step 4: Update Population**

```scala
object Population {
  private[creatures] def createPopulation(creatureType: CreatureType, initialSize: Int, rnd: Random) = {
    val pop = new Population(creatureType, rnd)
    pop.createInitialSet(initialSize)
    pop
  }
}

class Population(var creatureType: CreatureType, private val rnd: Random) {
  // create():
  val pos = new Point2d(rnd.nextDouble(), rnd.nextDouble())
  creatures += new Creature(creatureType, pos, rnd)
  // spawn locations / natural spawn: use rnd instead of MathUtil.RANDOM
  // new Creature(creatureType, newLocation, rnd)
}
```

Remove `MathUtil` import.

- [ ] **Step 5: Update Habitat + concrete habitats + HabitatSimulator**

In `Habitat`:
```scala
import scala.util.Random

abstract class Habitat extends ArrayBuffer[Population] {
  protected val rnd = new Random(1)
  // ...
  def reset(): Unit = {
    rnd.setSeed(1)
    dayCount = 0
    grid = createHabitatGrid()
    for (pop <- this) pop.reset()
    // ... rest unchanged
  }
}
```

In each concrete habitat `addPopulations`:
```scala
this.append(Population.createPopulation(CreatureType.RAT, 5, rnd))
```

In `HabitatSimulator.reset`: remove `MathUtil.RANDOM.setSeed(1)` and `MathUtil` import; keep `habitat.reset()`.

- [ ] **Step 6: Fix existing habitat tests**

`CreatureSuite`:
```scala
val creature = new Creature(CreatureType.CAT, new Point2d(2, 3), new Random(0))
```

`PopulationSuite`:
```scala
val pop = Population.createPopulation(CreatureType.RAT, 2, new Random(0))
```

Add `import scala.util.Random` where needed. `createPopulation` is `private[creatures]` — `populations` package is under `creatures.populations`, so tests in `creatures.populations` can call it if they are in that package (they already are).

- [ ] **Step 7: Compile + run habitat/dice/parameter/benchmark tests**

Run: `./gradlew test --tests 'com.barrybecker4.simulation.habitat.*' --tests 'com.barrybecker4.simulation.dice.*' --tests 'com.barrybecker4.simulation.parameter.*' --tests 'com.barrybecker4.simulation.benchmark.*'`
Expected: PASS

- [ ] **Step 8: Commit**

```bash
git add scala-source/com/barrybecker4/simulation/habitat \
  scala-test/com/barrybecker4/simulation/habitat
git commit -m "thread Habitat-owned Random through creatures and populations"
```

---

### Task 3: Habitat reset reproducibility test + final verification

**Files:**
- Create or modify: `scala-test/com/barrybecker4/simulation/habitat/creatures/populations/HabitatResetSuite.scala` (new)
- Verify: grep across repo

**Interfaces:**
- Consumes: `CatRatHabitat` / `Habitat.reset()` with owned seed-1 `rnd`

- [ ] **Step 1: Write failing/passing reproducibility test**

```scala
package com.barrybecker4.simulation.habitat.creatures.populations

import org.scalatest.funsuite.AnyFunSuite

class HabitatResetSuite extends AnyFunSuite {

  test("reset twice with seed 1 yields the same creature locations and directions") {
    val habitat = new CatRatHabitat
    habitat.reset()
    val first = snapshot(habitat)
    habitat.reset()
    val second = snapshot(habitat)
    assert(first == second)
  }

  private def snapshot(habitat: Habitat): List[(String, Double, Double, Double)] =
    habitat.toList.flatMap { pop =>
      pop.creatures.toList
        .sortBy(c => (c.getLocation.x, c.getLocation.y, c.getDirection))
        .map(c => (c.getName, c.getLocation.x, c.getLocation.y, c.getDirection))
    }
}
```

- [ ] **Step 2: Run the new test**

Run: `./gradlew test --tests 'com.barrybecker4.simulation.habitat.creatures.populations.HabitatResetSuite'`
Expected: PASS

- [ ] **Step 3: Repo-wide grep**

Run: `rg 'MathUtil\.RANDOM' -g '*.scala'`
Expected: no matches

- [ ] **Step 4: Full test suite**

Run: `./gradlew test`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
git add scala-test/com/barrybecker4/simulation/habitat/creatures/populations/HabitatResetSuite.scala
git commit -m "test habitat reset reproducibility after Random isolation"
```

---

### Task 4: Close out issue #50

- [ ] Comment on / close GitHub issue #50 noting acceptance criteria met (or leave open for human if preferred — agent should close only if tests pass and grep is clean)
