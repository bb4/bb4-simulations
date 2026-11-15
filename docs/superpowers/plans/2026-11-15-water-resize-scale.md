# Water Environment Resize Scale Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Recreate the water environment on window resize so it fills the drawable area inside the border inset, with mouse mapping aligned to that inset.

**Architecture:** Expose render `OFFSET`; add a pure size-mapping helper; recreate `Environment` in `WaterSimulator.paint` using padded dims; subtract `OFFSET` in `InteractionHandler` for column and hit tests.

**Tech Stack:** Scala 3, ScalaTest, AWT/Swing (`Graphics`, `Dimension`)

## Global Constraints

- Recreate/reset on resize (do not preserve wave/terrain state)
- Keep paint-time size check (no `ComponentListener`)
- Paths under `scala-source/com/barrybecker4/simulation/water/` and `scala-test/com/barrybecker4/simulation/water/`
- Spec: `docs/superpowers/specs/2026-11-15-water-resize-scale-design.md`

## File map

| File | Role |
|------|------|
| `water/rendering/EnvironmentRenderer.scala` | Make `OFFSET` package-visible (`private[water]`) |
| `water/ui/WaterSimulator.scala` | Padded env size on resize; track last env dims |
| `water/ui/InteractionHandler.scala` | OFFSET-aware mouse → env mapping |
| `water/ui/EnvSize.scala` (new) | Pure `envDims(panelW, panelH, offset)` helper |
| `scala-test/.../water/ui/EnvSizeSuite.scala` (new) | Size mapping tests |
| `scala-test/.../water/ui/InteractionHandlerSuite.scala` | OFFSET column / env-y mapping tests |

---

### Task 1: Env size mapping helper + tests

**Files:**
- Create: `scala-source/com/barrybecker4/simulation/water/ui/EnvSize.scala`
- Create: `scala-test/com/barrybecker4/simulation/water/ui/EnvSizeSuite.scala`

**Interfaces:**
- Produces: `object EnvSize { def envDims(panelWidth: Int, panelHeight: Int, offset: Int): Option[(Int, Int)] }`
  - Returns `None` when `panelWidth <= 0` or `panelHeight <= 0`
  - Otherwise `Some((max(1, panelWidth - 2*offset), max(1, panelHeight - 2*offset)))`

- [ ] **Step 1: Write the failing test**

```scala
package com.barrybecker4.simulation.water.ui

import org.scalatest.funsuite.AnyFunSuite

class EnvSizeSuite extends AnyFunSuite {

  test("envDims returns None for non-positive panel size") {
    assert(EnvSize.envDims(0, 400, 10).isEmpty)
    assert(EnvSize.envDims(400, 0, 10).isEmpty)
    assert(EnvSize.envDims(-1, 200, 10).isEmpty)
  }

  test("envDims subtracts 2*offset from each dimension") {
    assert(EnvSize.envDims(820, 420, 10).contains((800, 400)))
  }

  test("envDims clamps to at least 1 when panel is smaller than inset") {
    assert(EnvSize.envDims(15, 15, 10).contains((1, 1)))
    assert(EnvSize.envDims(5, 100, 10).contains((1, 80)))
  }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew test --tests 'com.barrybecker4.simulation.water.ui.EnvSizeSuite'`
Expected: FAIL (EnvSize not found / compile error)

- [ ] **Step 3: Write minimal implementation**

```scala
// Copyright by Barry G. Becker, 2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.water.ui

/** Map panel pixel size to environment dimensions inside a border inset. */
object EnvSize {

  /**
    * @return None if the panel is not yet sized; otherwise env width/height
    *         filling the area inside `offset` on each side (at least 1×1).
    */
  def envDims(panelWidth: Int, panelHeight: Int, offset: Int): Option[(Int, Int)] = {
    if (panelWidth <= 0 || panelHeight <= 0) None
    else Some(
      (math.max(1, panelWidth - 2 * offset),
       math.max(1, panelHeight - 2 * offset))
    )
  }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew test --tests 'com.barrybecker4.simulation.water.ui.EnvSizeSuite'`
Expected: BUILD SUCCESSFUL, all tests pass

- [ ] **Step 5: Commit**

```bash
git add scala-source/com/barrybecker4/simulation/water/ui/EnvSize.scala \
  scala-test/com/barrybecker4/simulation/water/ui/EnvSizeSuite.scala
git commit -m "feat(water): add EnvSize mapping for padded panel dims"
```

---

### Task 2: Expose OFFSET and wire WaterSimulator resize

**Files:**
- Modify: `scala-source/com/barrybecker4/simulation/water/rendering/EnvironmentRenderer.scala` (OFFSET visibility)
- Modify: `scala-source/com/barrybecker4/simulation/water/ui/WaterSimulator.scala`

**Interfaces:**
- Consumes: `EnvSize.envDims`, `EnvironmentRenderer.OFFSET` (as `private[water] val OFFSET`)
- Produces: recreate path uses padded dims; tracks last env `(w, h)` via `oldDimensions`

- [ ] **Step 1: Expose OFFSET**

In `EnvironmentRenderer` companion, change:

```scala
private val OFFSET = 10
```

to:

```scala
private[water] val OFFSET = 10
```

- [ ] **Step 2: Update WaterSimulator.paint resize block**

Replace the resize block in `paint` with:

```scala
EnvSize.envDims(getWidth, getHeight, rendering.EnvironmentRenderer.OFFSET) match {
  case Some((w, h)) if w != oldDimensions.width || h != oldDimensions.height =>
    env = new Environment(w, h)
    envRenderer.setEnvironment(env)
    handler.setEnvironment(env)
    oldDimensions = new Dimension(w, h)
  case _ => // not sized yet, or unchanged
}
```

Remove reliance on raw `this.getSize()` for env sizing. Keep `synchronized` on `paint` / `timeStep`.

Import not needed if using fully-qualified `rendering.EnvironmentRenderer.OFFSET` (package already imports `rendering`).

- [ ] **Step 3: Compile / run water tests**

Run: `./gradlew test --tests 'com.barrybecker4.simulation.water.*'`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add scala-source/com/barrybecker4/simulation/water/rendering/EnvironmentRenderer.scala \
  scala-source/com/barrybecker4/simulation/water/ui/WaterSimulator.scala
git commit -m "feat(water): size environment to drawable area on resize"
```

---

### Task 3: OFFSET-aware interaction mapping

**Files:**
- Modify: `scala-source/com/barrybecker4/simulation/water/ui/InteractionHandler.scala`
- Modify: `scala-test/com/barrybecker4/simulation/water/ui/InteractionHandlerSuite.scala`

**Interfaces:**
- Consumes: `EnvironmentRenderer.OFFSET`
- Produces: `InteractionHandler.columnAt(pixelX, xStep, width, offset)` and env-y via `pixelY - offset` for floor hit test
  - Keep binary-compatible default by adding `offset` param (update call sites and tests)

- [ ] **Step 1: Extend failing tests**

Add to `InteractionHandlerSuite`:

```scala
test("columnAt subtracts offset before dividing by xStep") {
  val offset = 10
  assert(InteractionHandler.columnAt(10, 1, 10, offset) == 0)
  assert(InteractionHandler.columnAt(15, 1, 10, offset) == 5)
  assert(InteractionHandler.columnAt(0, 1, 10, offset) == 0) // left of content
  assert(InteractionHandler.columnAt(100, 1, 10, offset) == 9)
}

test("envYFromPixel subtracts offset") {
  assert(InteractionHandler.envYFromPixel(50, 10) == 40)
  assert(InteractionHandler.envYFromPixel(5, 10) == -5)
}
```

Update existing `columnAt` calls to pass `offset = 0` (or overload) so old tests still express unpadded mapping:

```scala
assert(InteractionHandler.columnAt(0, 1, 10, 0) == 0)
// ... same for other existing asserts with trailing 0
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew test --tests 'com.barrybecker4.simulation.water.ui.InteractionHandlerSuite'`
Expected: FAIL (wrong arity / missing `envYFromPixel`)

- [ ] **Step 3: Implement mapping**

In `InteractionHandler` object:

```scala
private[water] def columnAt(pixelX: Int, xStep: Int, width: Int, offset: Int): Int =
  ((pixelX - offset) / xStep).max(0).min(width - 1)

private[water] def envYFromPixel(pixelY: Int, offset: Int): Int =
  pixelY - offset
```

In `mousePressed`:

```scala
val offset = rendering.EnvironmentRenderer.OFFSET
val col = InteractionHandler.columnAt(currentX, env.xStep, env.width, offset)
editTarget =
  if (InteractionHandler.envYFromPixel(currentY, offset) > env.floor(col)) EditTarget.Floor
  else EditTarget.Water
```

Add import: `com.barrybecker4.simulation.water.rendering` (or use full path via existing package).

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew test --tests 'com.barrybecker4.simulation.water.*'`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
git add scala-source/com/barrybecker4/simulation/water/ui/InteractionHandler.scala \
  scala-test/com/barrybecker4/simulation/water/ui/InteractionHandlerSuite.scala
git commit -m "fix(water): align mouse hit-testing with render OFFSET"
```

---

### Task 4: Final verification

- [ ] **Step 1: Run full water test suite**

Run: `./gradlew test --tests 'com.barrybecker4.simulation.water.*'`
Expected: BUILD SUCCESSFUL

- [ ] **Step 2: Manual check (if UI available)**

Launch Water sim, resize window larger and smaller: border + water fill the inset area; drag water/floor still hits the right columns.

- [ ] **Step 3: Commit plan checkboxes if updated** (optional) or leave plan as written
