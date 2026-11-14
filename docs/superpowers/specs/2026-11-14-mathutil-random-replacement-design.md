# MathUtil.RANDOM Replacement Design (Issue #50)

**Date:** 2026-11-14  
**Issue:** https://github.com/bb4/bb4-simulations/issues/50  
**Repo:** bb4/bb4-simulations

## Goal

Replace all uses of deprecated `MathUtil.RANDOM` with explicit `scala.util.Random` instances so simulators no longer share one mutable fixed-seed generator, while keeping habitat reset and benchmark runs reproducible for a given seed.

## Strategy

**Approach 1:** Per-simulator `Random`, with habitat threading an owned instance through populations and creatures.

## In scope

### 1. DiceSimulator

- Add `private val rnd = new Random`.
- Pass `rnd` to `DiceRollLogic.rollSum(...)`.
- Remove `MathUtil` import.

### 2. ParameterSimulator

- Add `private val rnd = new Random`.
- Use `rnd` for `parameter.randomizeValue(rnd)` and `rnd.nextDouble()`.
- Remove `MathUtil` import.

### 3. Habitat (owned Random, threaded)

- `Habitat` owns `private val rnd = new Random(1)`.
- `Habitat.reset()` calls `rnd.setSeed(1)` before rebuilding populations (seed lives with habitat).
- `HabitatSimulator.reset()` drops `MathUtil.RANDOM.setSeed(1)` and only calls `habitat.reset()`.
- `Population.createPopulation` / `Population` take `rnd` and use it for initial positions and spawn draws.
- `Creature(cType, location, rnd)` stores `rnd`; uses it for initial direction.
- Package helpers: `randomDirection(rnd: Random)` and `perturbDirection(direction, rnd)`.
- `CreatureAttributes.flock` / `doneEating` receive `rnd` from `CreatureProcessor` via `creature.rnd` (attributes stay data-only).
- Concrete habitats (`SerengetiHabitat`, `CatRatHabitat`, `SingleCreatureHabitat`) pass `rnd` into `createPopulation`.
- Habitat unit tests that construct `Creature` / `Population` pass an explicit `Random`.

### 4. BenchmarkCases

- Delete `MathUtil.RANDOM.setSeed(seed)` and the unused `MathUtil` import.
- Keep existing `new Random(seed)` for snake/trebuchet params and the optimizer `rnd`.
- No other `MathUtil.RANDOM` on the benchmark path in this repo.

## Out of scope

- Non-RANDOM `MathUtil` uses (`EPS`, `gcd`, `getDirectionTo`, comments).
- Constructor-injected `Random` UI for Dice/Parameter histogram sims.
- Changing bb4-math or bb4-optimization APIs.

## Testing

- Grep: zero `MathUtil.RANDOM` references remain in the repo.
- Update habitat tests that construct `Creature` / `Population` to pass `Random`.
- Add habitat reset reproducibility test: e.g. `CatRatHabitat`, two `reset()` calls, assert creature locations (and optionally directions) match.
- Run the project’s usual test task (`./gradlew test`); existing Dice / Parameter / Benchmark suites cover non-habitat paths adequately.

## Acceptance criteria

- [ ] No references to `MathUtil.RANDOM` remain in this repo.
- [ ] Habitat reset still gives the same populations for the same seed.
- [ ] Benchmark runs remain reproducible for a given seed (via per-run `new Random(seed)`).
