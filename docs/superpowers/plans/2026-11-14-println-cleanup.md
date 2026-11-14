# Println Cleanup Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Remove ungated and commented-out `println` noise per issue #53 / design option C.

**Architecture:** Delete noisy ungated prints; gate liquid overflow via existing `Logger`; gate PoissonGrid retry warning behind local `DEBUG = false`; delete commented-out println lines.

**Tech Stack:** Scala 3, existing `com.barrybecker4.simulation.liquid.Logger`

## Global Constraints

- Do not touch CLI/benchmark/applet println, WfcDebug, CreatureProcessor.DEBUG, Cave.print(), or test System.out.println
- Paths under `scala-source/com/barrybecker4/simulation/`

---

### Task 1: Delete ungated noise

**Files:**
- Modify: `trebuchet/model/Trebuchet.scala`, `trebuchet/model/Projectile.scala`, `common/ui/Simulator.scala`, `voronoi/.../PoissonPlacement.scala`, `voronoi/algorithm/VoronoiAlgorithm.scala`, `dungeon/generator/DungeonGeneratorStrategy.scala`

- [ ] Remove 8 ungated println call sites; remove unused `startTime` in dungeon strategy; leave `doOptimization` as empty no-op

### Task 2: Gate diagnostics

**Files:**
- Modify: `liquid/compute/SurfaceVelocityUpdater.scala` — use `Logger.log(1, ...)`
- Modify: `voronoi/.../PoissonGrid.scala` — `private val DEBUG = false` wrap

- [ ] Gate the two diagnostic prints

### Task 3: Delete commented-out printlns

**Files:** Trebuchet, Sling, PoissonPlacement, LiquidEnvironment, InteractionHandler, GrayScottAlgorithm, StockRunner, TradingStrategy

- [ ] Remove all 11 remaining commented println lines/blocks

### Task 4: Verify

- [ ] `rg println scala-source` — only gated + CLI remain
- [ ] `./gradlew test` (or compile) passes
- [ ] Commit
