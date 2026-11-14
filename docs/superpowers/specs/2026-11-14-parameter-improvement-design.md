# Parameter Package Improvement Design

**Date:** 2026-11-14  
**Packages:** `com.barrybecker4.simulation.parameter` (bb4-simulations), `com.barrybecker4.optimization.parameter.redistribution` (bb4-optimization)  
**Repos:** bb4/bb4-simulations (+ sibling bb4-optimization)

## Goal

Improve the parameter histogram demo and the redistribution library it exercises in two phases:

1. **Phase 1 — Correctness & high-value tests** (priority A)
2. **Phase 2 — Maintainability & Scala 3 idioms** (priority B)

**Scope decisions:** Cross-repo fixes allowed. Library behavior tests live in `bb4-optimization`; simulator wiring/UI/mode tests live in `bb4-simulations`. No ScalaCheck, no new redistribution types, no Swing toolkit rewrite, no Snake/Trebuchet changes.

**Recommended strategy:** Approach 1 — two-phase, test-first (same shape as FractalExplorer).

---

## Phase 1 — Correctness & High-Value Tests

### Intent

Add tests that are likely to uncover real bugs. Fix only clear bugs that tests expose. No style sweeps in this phase.

### Known issues

1. **Wrong double-histogram offset** in `ParameterSimulator`: `LinearFunction` is `scale*x + offset`; mapping `[min,max]→[0,N]` needs `offset = -scale*min`, not `-min`. Latent because all presets use `min=0`. Existing tests lock the wrong formula.
2. **Inverted class comment** vs code for `showRedistribution`: `true` → skewed (`randomizeValue.getValue`); `false` → ~uniform (`setValue`/`getValue` round-trip).
3. **Dialog combo** always opens at index 0; does not reflect current preset.
4. **Library edge cases:** `GAUSSIAN_NARROWEST` (σ=0.01); discrete special-prob sum / `MathUtil.EPS` + `PiecewiseFunction` strictly-increasing requirement.

### New tests — bb4-optimization

| Area | What to probe |
|------|----------------|
| `GaussianRedistribution` | Narrow σ (~0.01): construct, monotonic, bounded, invertibility on a grid |
| `DiscreteRedistribution` / `UniformRedistribution` | Special-prob sum = 1 with last special below max |
| `BooleanRedistribution` | Skew (0.8): forward + inverse round-trip |
| `Parameter` sampling | Seeded `randomizeValue` / `setValue`/`getValue` with redistribution |

### New tests — bb4-simulations / parameter

| Area | What to probe |
|------|----------------|
| Double histogram | Correct offset / `LinearFunction(Range, n)` when `min ≠ 0` |
| Integer histogram | Non-zero `min` binning if supported |
| Sampling modes | Seeded: redistribution on → skewed; off → near-uniform |
| `setParameter` | Switches integer vs double histogram size |
| Dialog | Ordinal → correct param; combo sync on open |

### Likely Phase 1 fixes (only if forced)

- Correct histogram mapping; fix docs/comments; update wrong offset tests
- CDF/EPS handling for discrete/uniform; raise Gaussian map resolution or harden narrow-σ path

### Out of Phase 1

Scala 3 style sweeps, `Consts` collapse, performance micro-opts, Swing toolkit changes, redistribution redesign beyond failing tests.

### Done when

- New suites exist and pass
- Bugfixes have regression tests
- Skewed vs uniform modes documented correctly

---

## Phase 2 — Maintainability & Scala 3 Idioms

### Intent

Clearer APIs, less redundancy, modern Scala 3. Phase 1 behavior preserved.

### Changes

- Convert `ParameterOptionsDialog` to significant indentation / `end`
- Hold typed `ParameterSimulator` instead of `asInstanceOf`
- Track selected `ParameterDistributionType` on the simulator for dialog sync
- Collapse `Consts` into companion without enum static-init cycles
- Prefer `LinearFunction(Range, n)` for clarity
- Remove stale comments / unused code on touched paths
- Opt: extract shared CDF/EPS helpers only if Phase 1 forced duplication

### Still out of scope

New redistribution kinds, Swing rewrite, Snake/Trebuchet UI, ScalaCheck, publishing `parameter` as its own module.

### Done when

- Phase 1 suites still green
- Redundancy cleaned; comments match behavior

---

## Architecture notes

```
ParameterSimulator (DistributionSimulator)
  ├── ParameterDistributionType (enum of presets → Parameter)
  ├── ParameterOptionsDialog
  └── bb4-optimization Parameter + RedistributionFunction
        ├── GaussianRedistribution
        ├── UniformRedistribution / DiscreteRedistribution
        └── BooleanRedistribution
```

## Testing approach

- ScalaTest `AnyFunSuite`; seeded `Random` + tolerances (no ScalaCheck)
- Prefer pure redistribution/parameter tests over Swing interaction where possible
- Verify with Gradle filters for redistribution, parameter types, and `simulation.parameter`

## Risks

| Risk | Mitigation |
|------|------------|
| Narrow Gaussian still coarse with small map | Raise `NUM_MAP_VALUES` only if invertibility tests fail |
| Histogram max bin dropped at index N | Document / clamp; same as Range ctor behavior |
| Dialog sync needs enum tracking | Store selected type on simulator in Phase 1/2 |
