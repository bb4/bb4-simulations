# FractalExplorer Improvement Implementation Plan

> **For agentic workers:** Execute task-by-task with TDD. Steps use checkbox (`- [ ]`) syntax for tracking. Work on the **current branch** (no feature branch). Do **not** commit unless the user asks.

**Goal:** Phase 1 high-value tests (+ forced bugfixes), then Phase 2 History cleanup, Scala 3 idioms, redundancy removal, and math-preserving escape-loop speedup.

**Architecture:** Keep FractalExplorer package structure. Strengthen algorithm/model/zoom tests first; then simplify History to single-pop, modernize small APIs, and optimize `EscapeIteration` with local doubles / squared magnitude (`ComplexNumber.magnitude` is already `r²+i²`).

**Tech Stack:** Scala 3.10, ScalaTest AnyFunSuite, Gradle (`./gradlew test --tests 'com.barrybecker4.simulation.fractalexplorer.*'`)

## Global Constraints

- FractalExplorer package only
- Phase 2 may change minor UI edge cases; Mandelbrot/Julia normalized escape-time must stay correct
- No new fractal types, no smooth coloring, no Swing rewrite
- No git commits unless user requests
- Implement on current branch (master), not a feature branch

---

## File map

| File | Phase 1 | Phase 2 |
|------|---------|---------|
| `scala-test/.../EscapeIterationSuite.scala` | Create | Keep; assert opt parity |
| `scala-test/.../HistorySuite.scala` | Extend (double-pop docs) | Update for single-pop |
| `scala-test/.../FractalAlgorithmSuite.scala` | Create (coords, timeStep) | Keep |
| `scala-test/.../ZoomBoxSuite.scala` | Extend | Keep |
| `scala-test/.../RowCalculatorSuite.scala` | Extend | Keep |
| `scala-test/.../AlgorithmEnumSuite.scala` | Create | Keep |
| `algorithm/History.scala` | Empty-safe if needed | Single-pop contract |
| `algorithm/FractalAlgorithm.scala` | Bugfix only | History push previous; Worker → fn; trim comments |
| `algorithm/EscapeIteration.scala` | — | Local doubles + `mag² < 4` |
| `algorithm/RowCalculator.scala` | Edge fix if needed | Drop duplicate setValue |
| `algorithm/AlgorithmEnum.scala` | — | Exhaustive match |
| `FractalExplorer.scala` | — | One setCoordinates site |
| `DynamicOptions.scala` | — | Field order / dead comments |
| `FractalOptionsDialog.scala` | — | Dropdown wiring |

---

### Task 1: EscapeIteration edge tests

**Files:**
- Create: `scala-test/com/barrybecker4/simulation/fractalexplorer/algorithm/EscapeIterationSuite.scala`

- [ ] **Step 1:** Write tests for max-iter interior point → 1.0, quick escape → 0.0, `maxIterations = 1`, point with `|z0| >= 2` → 0.0
- [ ] **Step 2:** Run suite — expect PASS (pure documentation of current behavior) or FAIL if bugs found
- [ ] **Step 3:** Fix `EscapeIteration` only if a test fails incorrectly

---

### Task 2: History / goBack documentation tests (old contract)

**Files:**
- Modify: `scala-test/.../HistorySuite.scala`
- Modify: `algorithm/History.scala` / `FractalAlgorithm.scala` only if empty history crashes

- [ ] **Step 1:** Tests: `goBack` with empty history is no-op; multi-zoom double-pop restores earlier range; document that current range sits on stack
- [ ] **Step 2:** Run HistorySuite
- [ ] **Step 3:** If `popLastRange` on empty is reachable unsafely, make `goBack`/`pop` safe

---

### Task 3: FractalAlgorithm coordinate + timeStep tests

**Files:**
- Create: `scala-test/.../FractalAlgorithmSuite.scala`

- [ ] **Step 1:** Tests: `getComplexPosition(0,0)` ≈ range.point1; opposite corner ≈ point2; `getRange(box)` round-trip; `timeStep` advances `currentRow` and eventually `isDone`
- [ ] **Step 2:** Run suite; fix only real bugs

---

### Task 4: ZoomBox aspect + finalize tests

**Files:**
- Modify: `scala-test/.../ZoomBoxSuite.scala`

- [ ] **Step 1:** Tests: `finalizeBoxForRange` without render; keepAspectRatio expands to algorithm aspect
- [ ] **Step 2:** Run; fix ZoomBox if wrong

---

### Task 5: RowCalculator edge + AlgorithmEnum tests

**Files:**
- Modify: `scala-test/.../RowCalculatorSuite.scala`
- Create: `scala-test/.../AlgorithmEnumSuite.scala`

- [ ] **Step 1:** Row opt near right edge; toggling opt resets currentRow to 0; enum createInstance types
- [ ] **Step 2:** Run all fractalexplorer tests
- [ ] **Step 3:** Phase 1 gate — all green

---

### Task 6: Phase 2 — History single-pop

**Files:**
- Modify: `algorithm/FractalAlgorithm.scala` (`setRange` pushes **previous** range)
- Modify: `algorithm/History.scala` if helpful
- Modify: `scala-test/.../HistorySuite.scala` for new contract

- [ ] **Step 1:** Update tests to expect single pop
- [ ] **Step 2:** Implement push-previous; `goBack` pops once
- [ ] **Step 3:** Run HistorySuite + FractalAlgorithmSuite

---

### Task 7: Phase 2 — EscapeIteration performance + parity

**Files:**
- Modify: `algorithm/EscapeIteration.scala`
- Existing escape tests must still pass

- [ ] **Step 1:** Ensure EscapeIterationSuite / Mandelbrot / Julia suites cover known points
- [ ] **Step 2:** Rewrite loop with local `zr`/`zi` and `zr*zr+zi*zi < 4.0`
- [ ] **Step 3:** Run algorithm suites — same normalized values

---

### Task 8: Phase 2 — Redundancy & Scala 3 idioms

**Files:** listed in file map

- [ ] **Step 1:** AlgorithmEnum match; Worker → function; RowCalculator duplicate setValue; one setCoordinates; DynamicOptions/Dialog cleanup; trim FractalAlgorithm comments
- [ ] **Step 2:** `./gradlew test --tests 'com.barrybecker4.simulation.fractalexplorer.*'`
- [ ] **Step 3:** Done — report summary to user (no commit unless asked)

---

## Spec coverage check

- Phase 1 test table → Tasks 1–5
- Phase 1 empty-history safety → Task 2
- Phase 2 History single-pop → Task 6
- Escape micro-opt → Task 7
- Scala 3 / redundancy / UI light polish → Task 8
