# Snake Simulation Improvement Design

**Date:** 2026-11-14  
**Package:** `com.barrybecker4.simulation.snake`  
**Repo:** bb4/bb4-simulations

## Goal

Improve the spring-based snake simulation in two phases:

1. **Phase 1 — Correctness & high-value tests** (priority A)
2. **Phase 2 — Maintainability & Scala 3 idioms** (priority B)

Phase 2 must not change locomotion physics except where Phase 1 already fixed a proven bug, or where a width-index fix is explicitly approved after investigation.

**Scope:** Snake package only (plus its tests). No new locomotion features, no collision/multi-snake, no Swing toolkit replacement, no changes outside this package except shared types already used (`WaveType`, vecmath, optimizer APIs).

**Recommended strategy:** Algorithm-core first — tests around edge springs, muscles, stability, timestep adaptation, shared locomotion params, and fitness; then dead-code removal and practical Scala 3 cleanup. Avoid brittle Swing UI tests.

**Widths decision (user choice C):** Investigate first. Add a characterization test for current vs intended `SnakeData.widths` mapping. Do not remapping geometry until the user approves after seeing results.

---

## Phase 1 — Correctness & High-Value Tests

### Intent

Add tests that are likely to uncover real bugs. Fix only clear bugs that tests expose (or that block writing them). No style sweeps or geometry remapping in this phase.

### Existing coverage (baseline)

Already tested: `TEST_SNAKE` construction invariants, `contractMuscles` smoke for default params, `stepForward` finite positions after 50 steps, `Edge.getForce` finite at equilibrium and when stretched.

### New tests

| Area | What to probe | Why |
|------|----------------|-----|
| `Edge.getForce` | Rest ≈ zero force; stretch → restoring; `length > 2×effective` branch; near-zero / coincident particles | Core spring; NaN/∞ risk; coverage is shallow |
| `Edge.setContraction` | `contraction ≤ 0` rejects; valid contraction updates `effectiveLength` | Muscle API illegal states |
| `Segment.contractMuscles` | Default params OK; amplitude that drives a side ≤ 0 throws; left/right opposite offsets | Can throw mid-simulation |
| `Snake` construction | Head/tail flags; `widths.length` vs `numSegments`; **per-segment rear half-width vs `widths(i)`** (document actual) | Surfaces width-index mismatch without changing geometry yet |
| `Snake` + `SnakeUpdater` | Share one `LocomotionParameters`; `massScale` change visible on particles after step | Current tests construct separate param objects |
| `SnakeUpdater.stepForward` | Finite positions after N steps; large `dt` returns halved step when unstable | Timestep adaptation |
| `Snake.isStable` | Mild snake stable; artificially kinked edges → unstable | Fitness penalty path |
| `SnakeSimulator` fitness | Default params → finite fitness `< UnstableFitnessPenalty`; extreme params → unstable penalty when reproducible without UI | Optimizer contract |

### Widths investigation (no fix yet)

`SnakeData` documents `widths.length == numSegments + 1` cross-sections. `Snake.resetFromData` appears to:

- Use `widths(0)` / `widths(1)` for the head
- Index body segments such that the last one or two width entries are unused
- Likely reuse the head rear width for body segment 1 instead of the next cross-section

**Phase 1 deliverable:** a test that records the actual rear half-width (or particle spacing) per segment index vs `widths(i)`, with comments stating the intended mapping. **No remapping** until the user approves.

### Phase 1 results (implemented)

**Widths characterization (`TEST_SNAKE`):** Confirmed mismatch with intended `segment i rear == widths(i+1)`:
- Head rear matches `widths(1)` (intended).
- Body segment 1 rear currently equals `widths(1)` (same as head), not `widths(2)`.
- Last `widths` entry unused by current `resetFromData`.
- Remapping still **not** done — awaiting approval.

**Fixes forced by tests:**
- `Edge.getForce`: coincident / near-zero length returned NaN from `normalize`; now returns zero force below `MinLengthForForce`.
- `BUMPY_SNAKE` preset: `widths.length` was 42 for `numSegments=42`; appended tip width `1.0` so length is 43 (`numSegments + 1`).

**Tests added:** `EdgePhysicsTest` (expanded), `SegmentMuscleTest`, `SnakeGeometryTest`, `SnakeUpdaterTest`, `SnakeFitnessTest`; `SnakeSimulationTest` reduced to shared-wiring smoke.

### Out of Phase 1

Scala 3 style sweeps, UI/dialog/renderer cleanup, integrator rewrite, width remapping, performance micro-opts.

### Done when

- New suites above exist and pass
- Any Phase 1 bugfixes have regression tests
- Width characterization is recorded; geometry unchanged unless a separate approval follows
- Observable locomotion for default `TEST_SNAKE` remains finite and stable under the existing smoke path

---

## Phase 2 — Maintainability & Scala 3 Idioms

### Intent

Clearer structure, less redundancy, practical Scala 3. Physics must stay correct relative to Phase 1 baselines. No Swing redesign.

### Dead / unused to remove

- `Segment.EPS` (unused)
- Unused `Segment.changeVec`
- `Edge` construction-time `Line2D` field and `intersects` (geometry never updated after construct; unused)
- Dead comments (`scale = 1.0 // snake.getRenderingParams...`, obsolete notes that no longer guide work)
- Redundant `1.0 * timeStep` in `SnakeUpdater`

### Structure / understandability

- Prefer companion `apply` / fewer `def this()` chains on `Segment` / `SnakeSimulator` where clarity wins; keep package-private visibility for Swing wiring
- After widths investigation concludes: either validate `widths.length == numSegments + 1` and fix indexing (if approved), or document the actual contract in `SnakeData` scaladoc and keep characterizing tests
- Keep mutable particle/edge arrays (hot path)
- Clarify `SegmentUpdater` force helpers with names only; no force-equation rewrite

### Scala 3 idioms (practical)

Already in good shape: `enum SnakeType`, `scala.compiletime.uninitialized` for Swing lifecycle fields.

Do:

- Prefer `WaveType.values` over `WaveType.VALUES` in this package if a one-line change
- Prefer exhaustive `match` / enum usage over stringly logic where local and safe
- Keep Java-bean style getters required by `NewtonianSimulator` / Swing

Skip:

- Opaque types, givens, exports-for-their-own-sake
- Rewriting mutable physics state into immutable snapshots
- Large file splits that do not remove real duplication

### Performance (cheap wins only)

- Preserve scratch `Vector2d` reuse in `Edge` / `SegmentUpdater` (already the right pattern)
- No Euler → RK rewrite, no constraint solver, no parallel segment updates (particles are shared)
- Only clarify hot-path code when it also improves readability

### Still out of scope

New snake types as features, skin/texture, collision, multi-snake, hill-climbing UI changes, packages outside snake.

### Done when

- Listed dead code is gone
- Phase 1 tests still pass (updated only for intentional approved behavior changes)
- Package reads more clearly without expanding scope

---

## Architecture notes

```
SnakeSimulator (NewtonianSimulator UI + Optimizee)
  ├── SnakeDynamicOptions / SnakeOptionsDialog
  ├── SnakeRenderer → SegmentRenderer → EdgeRenderer
  ├── SnakeUpdater
  │     └── SegmentUpdater (forces, friction, integrate)
  └── Snake
        ├── HeadSegment / Segment (particles, edges)
        ├── Edge (spring force)
        └── SnakeData / SnakeType
```

Phase 1 focuses on `Edge`, `Segment`, `Snake`, `SnakeUpdater`, and fitness sampling. Phase 2 cleans that core and light UI/render dead code after the safety net exists.

## Testing approach

- Continue ScalaTest `AnyFunSuite` under `scala-test/.../snake/`
- Prefer pure geometry/physics/fitness tests over Swing interaction tests
- Run snake-related tests via Gradle after each phase, e.g.  
  `./gradlew test --tests 'com.barrybecker4.simulation.snake.*'`

## Risks

| Risk | Mitigation |
|------|------------|
| Width remapping changes look and fitness numbers | Characterize first; require explicit approval before fix |
| Fitness tests flaky or slow | Cap steps; use `TEST_SNAKE`; assert ranges not bit-identical speeds |
| Over-refactoring constructors breaks particle sharing | Keep shared front/back particles; small steps with geometry tests |
| Spring force “cleanup” changes damping behavior | Pin force direction/sign with Phase 1 edge tests before edits |
