# Println Cleanup Design (Issue #53)

**Date:** 2026-11-14  
**Issue:** https://github.com/bb4/bb4-simulations/issues/53  
**Repo:** bb4/bb4-simulations

## Goal

Remove ungated console noise from simulation and UI model code, and delete commented-out `println` lines, without changing CLI/benchmark output or intentionally gated debug paths.

## Strategy

**Option C:** Delete noisy ungated messages; gate only diagnostics that look useful for future debugging.

## In scope

### 1. Delete ungated noise (8 call sites in 6 files)

| File | Action |
|------|--------|
| `trebuchet/model/Trebuchet.scala` | Remove 3-line release banner in `checkProjectileRelease` |
| `trebuchet/model/Projectile.scala` | Remove “no longer on ramp” print; keep `isOnRamp = false` |
| `common/ui/Simulator.scala` | Remove stub message from default `doOptimization`; leave method as empty no-op |
| `voronoi/.../PoissonPlacement.scala` | Remove constructor width/height print |
| `voronoi/algorithm/VoronoiAlgorithm.scala` | Remove “Restarting!” print |
| `dungeon/generator/DungeonGeneratorStrategy.scala` | Remove timing print and unused `startTime` |

### 2. Gate real diagnostics (2 call sites)

| File | Action |
|------|--------|
| `liquid/compute/SurfaceVelocityUpdater.scala` | Replace `println` with `Logger.log(1, …)` so it follows liquid’s existing `LOG_LEVEL` gate |
| `voronoi/.../PoissonGrid.scala` | Wrap retry warning with a local `private val DEBUG = false` (same pattern as habitat `CreatureProcessor.DEBUG`) |

### 3. Delete commented-out `println` lines (11 remaining)

Issue #53 listed 12; `DirichletEtaFunction` no longer has a commented `println` (already removed). Delete the rest:

| File | Notes |
|------|--------|
| `trebuchet/model/Trebuchet.scala` | torque comment block (2 lines) |
| `trebuchet/model/Sling.scala` | 2 sites |
| `voronoi/.../PoissonPlacement.scala` | 1 site |
| `liquid/model/LiquidEnvironment.scala` | 1 site (`////println`) |
| `reactiondiffusion/InteractionHandler.scala` | 1 site (may be multi-line) |
| `reactiondiffusion/algorithm/GrayScottAlgorithm.scala` | 2 sites |
| `trading/model/runner/StockRunner.scala` | 1 site (may be multi-line) |
| `trading/model/tradingstrategy/TradingStrategy.scala` | 2 sites |

## Out of scope

- Already gated debug: `WfcDebug.enabled`, `CreatureProcessor.DEBUG`
- CLI / entry-point console output: Benchmark*, `BatchRun`, `SimulatorApplet`
- Public debug helper `Cave.print()`
- Test `System.out.println` in `SurfaceVelocityUpdaterSuite` (low priority per issue)

## Non-goals

- No new logging framework
- No UI for toggling these flags
- No behavior changes beyond silence / gated diagnostics

## Acceptance criteria

- No ungated `println` remains in simulation or UI model code
- CLI tools keep their console output
- Commented-out `println` lines listed above are removed
- Liquid overflow diagnostic uses `Logger`; PoissonGrid retry warning is behind `DEBUG = false`

## Verification

1. Repo-wide search for `println` under `scala-source`; confirm remaining hits are only groups 2–3 from the issue (gated debug + CLI) plus any `Logger`/`Log` wrappers
2. Compile / run existing tests (`./gradlew test` or project-equivalent) to ensure no breakage from dead-code cleanup (e.g. unused imports after Logger add)
