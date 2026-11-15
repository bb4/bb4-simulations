# Spirograph Simulation Improvement Design

**Date:** 2026-11-15  
**Package:** `com.barrybecker4.simulation.spirograph`  
**Repo:** bb4/bb4-simulations

## Goal

Improve the spirograph simulation in two phases:

1. **Phase 1 — Correctness & high-value tests** (priority A)
2. **Phase 2 — Maintainability & Scala 3 idioms** (priority B)

**Scope:** Spirograph package only (plus its tests). No new UI features, no Swing toolkit replacement, no brittle full-applet UI suites. No changes outside this package except continued use of `MathUtil.gcd`.

**Recommended strategy:** Extract pure curve math (`SpirographMath`) first — tests around kinematics, period, segment limits, and start-point consistency; fix bugs those tests expose (initialize stray line, dead theta branch, ControlPanel field init order). Then Phase 2 dead-code removal and practical Scala 3 cleanup.

---

## Phase 1 — Correctness & High-Value Tests

### Intent

Add tests that are likely to uncover real bugs. Fix clear bugs that tests expose (or that block writing them). No style sweeps in this phase beyond what wiring requires.

### Extract

`model/SpirographMath` — pure functions:

- `sign`, `numRevolutions` (GCD period)
- `segmentsPerPass`, `segmentLimit`
- `theta`, `phi`
- `center`, `penPoint`

`GraphState`, `Parameters`, and `GraphRenderer` delegate to it.

### New / extended tests

| Area | What to probe |
|------|----------------|
| `SpirographMath` | Hand-checked points; negative r2; period table; segment limit vs `|pos/r2|`; closure; no NaN/∞ |
| `Parameters` | `initialize` start == pen at θ=0; `getCenter` matches math |
| `GraphState` | Existing period/delay; revolutions via delegate |
| `ControlPanel` | Field order so labels/buttons are not wiped after construction |

### Bugfixes in Phase 1

1. **`Parameters.initialize`** — replace `r1 + (r2 + sign) + pos` with pen-at-θ=0 formula.
2. **`updateThetaForStep` dead branch** — `count == limit` never runs under `count < limit`; remove.
3. **`ControlPanel` field order** — declare mutable UI fields before methods that assign them.

### Out of Phase 1

Slider enum, stroke reuse, comment cleanup, decoration sharing, ParametricEquations numeric unification, Scala style sweeps.

### Done when

- New math/parameter suites exist and pass
- Phase 1 bugfixes have regression coverage where practical
- Default max-speed draw still produces a closed curve

### Phase 1 results (implemented)

- Added `SpirographMath` with period, segment count, θ/φ, center, pen.
- Suites: `SpirographMathSuite`, `ParametersSuite`; existing `GraphStateSuite` / `ParametricEquationsSuite` still pass.
- Fixed `Parameters.initialize` to use pen-at-θ=0 (removes stray first segment).
- Removed dead `count == limit` theta branch; renderer uses `SpirographMath` for n/limit/theta/phi/pen.
- Fixed `ControlPanel` field declaration order so labels/buttons are not wiped after construction.
- `./gradlew test --tests 'com.barrybecker4.simulation.spirograph.**'` green (22 tests).

---

## Phase 2 — Maintainability & Scala 3 Idioms

### Intent

Clearer structure, less redundancy, practical Scala 3. Curve math must stay correct relative to Phase 1 baselines. No Swing redesign.

### Cleanup

- Stale checkbox comment; duplicate attribution blurb
- Misleading GraphPanel “monitor locking” comment
- ParametricEquations scaladoc (R2 vs “main circle”); display-only vs `SpirographMath`
- Drop unused stored ctor fields on ParametricEquations if only equation strings are used

### Structure / perf / idioms

- DecorationRenderer uses shared center/pen helpers
- Create `BasicStroke` once per draw, not per segment
- Slider indices → local `enum` + exhaustive `match`
- Skip opaque types, givens, exports, immutable `Parameters` rewrite

### Done when

Phase 1 suites still pass; cleanup landed; decoration tracks pen; max-speed redraw works.

### Phase 2 results (implemented)

- Removed stale checkbox comment and duplicate David Little blurb from `ControlSliderGroup`; attribution kept on `Spirograph`.
- Clarified `GraphPanel` pause/threading comments (no monitor locking).
- `ParametricEquations`: corrected scaladoc (rolling R2); dropped unused stored radius fields; display-only vs `SpirographMath` documented.
- `DecorationRenderer` uses `SpirographMath.center` / `penPoint` for circle/spoke/dot alignment.
- `GraphRenderer` creates `BasicStroke` once per draw.
- Slider indices → `enum SliderId` with exhaustive `match` in `sliderChanged`.
- `./gradlew test --tests 'com.barrybecker4.simulation.spirograph.**'` still green.
