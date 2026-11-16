# Snake Locomotion Gaits Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Let the user choose among rectilinear, horizontal undulatory, side-winding, and concertina gaits from the Snake Options dialog, with each gait implemented as a strategy over existing muscle contractions and friction.

**Architecture:** A `GaitType` enum and `Gait` trait with `applyContractions` and `frictionScale`. Four gait implementations live under `com.barrybecker4.simulation.snake.gait`. `Segment.contractMuscles` and `SegmentUpdater.updateFrictionalForce` delegate to the active gait. UI is a combo in `SnakeOptionsDialog` only; OK sets `gaitType` and resets pose (existing `setSnakeData` path).

**Tech Stack:** Scala, Swing (`JComboBox`), existing snake spring-mass physics, Scalatest.

**Decisions (locked):** Fidelity B (no new physics concepts); options dialog only; reset on OK; Approach 2 (strategy objects); shared wave knobs for all gaits.

## Global Constraints

- No segment lift / 3D contact; only left/right edge contractions and friction scaling
- Default gait remains horizontal undulatory (current behavior bit-for-bit for contractions and `frictionScale = 1`)
- No live dynamic-options gait control in v1
- No per-gait automatic parameter presets in v1

---

## File map

**Create:**
- `scala-source/.../snake/gait/Gait.scala` — trait
- `scala-source/.../snake/gait/GaitType.scala` — enum + display names + `gait` instance
- `scala-source/.../snake/gait/HorizontalUndulatoryGait.scala` — current L/R opposite wave
- `scala-source/.../snake/gait/RectilinearGait.scala` — bilateral equal contraction; higher friction when shortening
- `scala-source/.../snake/gait/SideWindingGait.scala` — lateral wave + phase-modulated friction
- `scala-source/.../snake/gait/ConcertinaGait.scala` — localized fold window + high friction in window
- `scala-test/.../snake/gait/GaitContractionTest.scala`
- `docs/superpowers/specs/2026-11-16-snake-locomotion-gaits-design.md`
- `docs/superpowers/plans/2026-11-16-snake-locomotion-gaits.md`

**Modify:**
- `LocomotionParameters.scala` — `var gaitType`
- `Segment.scala` — delegate `contractMuscles`
- `SegmentUpdater.scala` — multiply friction by `frictionScale`
- `SnakeOptionsDialog.scala` — gait combo; set on OK
- `SnakeSimulationTest.scala` — keep default undulatory smoke green

## Gait formulas (concrete)

Shared phase: `theta = segmentIndex / wavePeriod - waveSpeed * time`, `wave = waveType.calculateOffset(theta)`.

| Gait | Contractions | frictionScale |
|------|-------------|---------------|
| Horizontal undulatory | `L = 1 + amp*(wave - dir)`, `R = 1 - amp*(wave - dir)` (current) | `1.0` |
| Rectilinear | `L = R = 1 + amp*wave` (ignore dir for lateral bend) | `>1` when `wave < 0` (shortening), else `<=1` |
| Side-winding | Same L/R opposition as undulatory | `1 + k * max(0, wave)` (plant near peaks) |
| Concertina | If segment in traveling fold window of width ~`wavePeriod`: high-amp undulatory L/R; else ~`1.0` | Elevated inside fold window |

Keep contractions `>= 0` (preserve existing guard / throw for invalid values).

## Tasks

### Task 0: Persist approved design + plan docs
- [ ] Write design spec
- [ ] Save this implementation plan under `docs/superpowers/plans/`

### Task 1: Gait API + undulatory extraction (TDD)
- [ ] Failing tests: undulatory L/R opposite and antisymmetric around 1.0 for sine at known theta
- [ ] Add `Gait` / `GaitType` / `HorizontalUndulatoryGait` with logic moved from `Segment.contractMuscles`
- [ ] Wire `LocomotionParameters.gaitType` (default undulatory) and delegate from `Segment`
- [ ] Confirm `SnakeSimulationTest` still passes

### Task 2: Friction hook
- [ ] Add default `frictionScale = 1.0` on `Gait`
- [ ] Apply scale in `SegmentUpdater`
- [ ] Undulatory behavior unchanged at scale 1

### Task 3: Rectilinear, side-winding, concertina
- [ ] Implement remaining three gaits
- [ ] Tests for rectilinear L==R; concertina outside window; side-winding frictionScale
- [ ] Tune constants for visually distinct motion

### Task 4: Options dialog
- [ ] Add `JComboBox` for `GaitType`
- [ ] On `ok()`: set `params.gaitType`; keep `setSnakeData` reset path
- [ ] Select current `gaitType` when opening the dialog

### Task 5: Verification
- [ ] Run snake unit tests
- [ ] Manual check of each gait after OK/reset

## Out of scope

- Live gait switching on dynamic options panel
- Segment lift / multi-contact side-winding
- Per-gait optimized wave/friction presets
- Optimizer searching over gait type

## Success criteria

- Four gaits selectable from Snake Options; default matches today’s undulatory motion
- OK applies gait and resets snake pose
- Unit tests cover contraction/friction pattern differences; existing smoke tests pass
