# FractalExplorer Improvement Design

**Date:** 2026-11-14  
**Package:** `com.barrybecker4.simulation.fractalexplorer`  
**Repo:** bb4/bb4-simulations

## Goal

Improve FractalExplorer in two phases:

1. **Phase 1 — Correctness & high-value tests** (priority A)
2. **Phase 2 — Maintainability & Scala 3 idioms** (priority B)

Phase 2 may change minor UI edge-case behavior if Mandelbrot/Julia escape-time math remains correct.

**Scope:** FractalExplorer only. No new fractal types, no smooth coloring, no Swing toolkit replacement, no changes outside this package (except shared types already used).

**Recommended strategy:** Algorithm-core first — tests and fixes around math, model, history, coordinates, and `timeStep`; then idiomatic cleanup of that core and light UI polish. Avoid brittle Swing UI tests.

---

## Phase 1 — Correctness & High-Value Tests

### Intent

Add tests that are likely to uncover real bugs. Fix only clear bugs that tests expose (or that block writing them). No style sweeps or History API redesign in this phase.

### Existing coverage (baseline)

Already tested: Mandelbrot/Julia escape via `EscapeIteration`, FractalModel bounds/aspect, History push/pop + one `goBack` path, ZoomBox validity/clear, RowCalculator opt≈naive parity.

### New tests

| Area | What to probe | Why |
|------|----------------|-----|
| `EscapeIteration` | max-iter boundary, `|z| = 2` edge, `maxIterations = 1` | Core math; only reached indirectly today |
| `History` / `goBack` | empty history, single zoom, multi-zoom; document current double-pop | Easy to break; confusing contract |
| `FractalAlgorithm` | `getComplexPosition` / `getRange` box mapping; `timeStep` advances rows and finishes | Untested orchestration |
| `ZoomBox` | aspect-preserving box sizing; `finalizeBoxForRange` without prior `render` | User-visible zoom bugs |
| `RowCalculator` | last columns under run-length skip; opt toggling restarts row | Existing suite is coarse parity only |
| `AlgorithmEnum` | `createInstance` returns expected types | Prevents enum/match drift |

### Likely Phase 1 fixes (only if forced)

- Empty-history safety on `pop` / `goBack` (no crash)
- Off-by-one in optimized row fill near width edge, if tests fail

### Out of Phase 1

Scala 3 style sweeps, UI layout cleanup, escape-loop micro-optimizations, History API redesign.

### Done when

- New suites above exist and pass
- Any Phase 1 bugfixes have regression tests
- Observable fractal math and zoom/history behavior match pre-Phase-1 (except intentional empty-history safety)

---

## Phase 2 — Maintainability & Scala 3 Idioms

### Intent

Clearer APIs, less redundancy, modern Scala 3. Idiomatic rewrites OK; Mandelbrot/Julia math must stay correct. Minor UI edge-case changes allowed.

### History / zoom contract (intentional change)

**Current:** `setRange` pushes the *new* range; `goBack` often double-pops when top equals current range.

**Target:** On zoom, record the range *before* the change; `goBack` pops once and restores that range. Empty history: `goBack` is a no-op (no exception). Update Phase 1 history tests to this contract.

### Scala 3 / structure

- `AlgorithmEnum.createInstance` → exhaustive `match`
- Trim Java-bean noise where local style allows; keep Swing-facing getters required by simulator base
- Replace thin `Worker` with a simple row-range function + parallel/sequential `foreach`
- Remove duplicate `model.setValue` in optimized `RowCalculator` path
- Call `setCoordinates` from one site (not both `timeStep` and `paint`)
- `DynamicOptions`: declare fields before use; remove dead comments
- `FractalOptionsDialog`: simplify dropdown wiring

### Performance (math-preserving)

- Escape loop: avoid alloc-heavy `power(2).add`; prefer squared-magnitude check and/or local `zr`/`zi` doubles — same escape rule
- Keep parallel row chunks; no new coloring algorithm

### Unused / dead

- Unused imports, dead comments, redundant local aliases
- Shorten or remove the long stale benchmark comment block in `FractalAlgorithm`

### Still out of scope

New fractal types, smooth coloring, rewriting Swing to another UI toolkit, non-fractalexplorer packages.

### Done when

- History uses single-pop semantics with updated tests
- Redundancy and unused code above are removed
- Escape path preserves normalized escape-time results (covered by Phase 1 + updated tests)
- Code reads more idiomatically without expanding scope beyond FractalExplorer

---

## Architecture notes

```
FractalExplorer (Simulator UI)
  ├── DynamicOptions / FractalOptionsDialog
  ├── ZoomHandler → ZoomBox
  └── FractalAlgorithm (abstract)
        ├── MandelbrotAlgorithm / JuliaAlgorithm → EscapeIteration
        ├── FractalModel (+ FractalColorMap)
        ├── RowCalculator
        └── History
```

Phase 1 focuses on the lower half and ZoomBox. Phase 2 touches the full stack lightly after the safety net exists.

## Testing approach

- Continue ScalaTest `AnyFunSuite` under `scala-test/.../fractalexplorer/`
- Prefer pure algorithm/model tests over Swing interaction tests
- Run fractalexplorer-related tests via Gradle after each phase

## Risks

| Risk | Mitigation |
|------|------------|
| History rewrite breaks “Go Back” | Phase 1 documents old behavior; Phase 2 updates tests first or with the change |
| Escape micro-opt changes floats | Keep `n.toDouble / maxIterations`; assert known points unchanged |
| Over-refactoring UI | Limit Phase 2 UI work to listed items |
