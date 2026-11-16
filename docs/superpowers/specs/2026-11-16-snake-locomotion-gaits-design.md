# Snake Locomotion Gaits Design

**Date:** 2026-11-16  
**Repo:** bb4/bb4-simulations  
**Package:** `com.barrybecker4.simulation.snake`

## Goal

Add selectable snake locomotion gaits — rectilinear, horizontal undulatory, side-winding, and concertina — via a dropdown in the Snake Options dialog, implemented within the existing spring-mass + friction model.

## Background

The snake simulation (Gavin Miller, SIGGRAPH 1988 style) drives motion by contracting left/right side edges with a traveling wave (`Segment.contractMuscles`) and applying anisotropic friction on center particles (`SegmentUpdater.updateFrictionalForce`). That pattern is horizontal undulatory progression only.

## Decisions

- **Fidelity:** As realistic as the spring-mass model allows; no new physics concepts (no segment lift / 3D contact).
- **UI:** Gait dropdown only in the modal Snake Options dialog (not on the live dynamic options panel).
- **On OK:** Set gait and reset the snake to its initial pose (existing `setSnakeData` rebuild path).
- **Architecture:** Strategy objects (`Gait` trait + `GaitType` enum), not a switch inside `Segment` alone and not preset-only “gaits.”
- **Parameters:** Shared wave speed / amplitude / period / wave type for all gaits; no per-gait automatic presets in v1.

## Architecture

- `GaitType` enum with display names and a `gait` instance each.
- `Gait` trait:
  - `applyContractions(segment, params, time)` — sets left/right edge contractions
  - `frictionScale(segment, params, time)` — multiplier on frictional force (default `1.0`)
- Four implementations under `com.barrybecker4.simulation.snake.gait`.
- `LocomotionParameters.gaitType` selects the active gait (default: horizontal undulatory).
- `Segment.contractMuscles` and `SegmentUpdater.updateFrictionalForce` delegate to the active gait.

## Gait physics

Shared phase: `theta = segmentIndex / wavePeriod - waveSpeed * time`, `wave = waveType.calculateOffset(theta)`.

| Gait | Contractions | frictionScale |
|------|-------------|---------------|
| Horizontal undulatory | `L = 1 + amp*(wave - dir)`, `R = 1 - amp*(wave - dir)` (current) | `1.0` |
| Rectilinear | `L = R = 1 + amp*wave` | `>1` when shortening (`wave < 0`), else `<=1` |
| Side-winding | Same L/R opposition as undulatory | `1 + k * max(0, wave)` (plant near peaks) |
| Concertina | High-amp undulatory L/R inside a traveling fold window; near rest outside | Elevated inside fold window |

Keep contractions positive (existing edge contraction guard).

## UI / reset

- Add a gait `JComboBox` in `SnakeOptionsDialog` alongside snake type and wave type.
- On OK: assign `params.gaitType`; `setSnakeData` continues to rebuild the snake and reset the simulation clock.

## Testing

- Unit tests for contraction patterns (undulatory opposite; rectilinear equal; concertina rest outside window; side-winding friction scale peaks with wave).
- Existing snake smoke tests remain green under default undulatory gait.
- Manual check: each gait produces recognizably different motion after OK/reset.

## Out of scope

- Live gait switching on the dynamic options panel
- Segment lift / multi-contact side-winding
- Per-gait optimized wave/friction presets
- Optimizer searching over gait type

## Success criteria

- Four gaits selectable from Snake Options; default matches today’s undulatory motion.
- OK applies gait and resets snake pose.
- Unit tests cover pattern differences; existing smoke tests pass.
