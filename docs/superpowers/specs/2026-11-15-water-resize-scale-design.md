# Water Environment Resize Scale Design

**Date:** 2026-11-15  
**Repo:** bb4/bb4-simulations  
**Package:** `com.barrybecker4.simulation.water`

## Goal

When the Water simulation panel is resized, recreate the environment at a size that fills the drawable area inside the existing border inset, resetting waves and terrain.

## Background

`WaterSimulator.paint` already recreates `Environment` when the panel size changes, using `getWidth` × `getHeight`. Rendering draws with a fixed `OFFSET` (10px) inset, so an env sized to the full panel overflows the component and does not cleanly fill the window. Mouse hit-testing does not subtract that inset either.

## Strategy

**Approach 1 — Fit env to panel minus `OFFSET`:** keep recreate-on-resize; size the env to the padded drawable area; align interaction with the same inset.

## Behavior

1. On panel size change, create a new `Environment(w, h)` (reset waves/terrain).
2. Wire `envRenderer` and `handler` to the new env.
3. Drawable size:
   - `w = max(1, getWidth - 2 * OFFSET)`
   - `h = max(1, getHeight - 2 * OFFSET)`
4. Skip recreate when panel width or height is still 0, or when the derived `(w, h)` is unchanged from the last env size.
5. Height-dependent initial profile (`yrat = height / 400.0` in `Environment`) continues to scale the default water/floor curves with the new height.

## Touch points

| File | Change |
|------|--------|
| `water/rendering/EnvironmentRenderer.scala` | Expose `OFFSET` (package-visible or shared) so simulator and interaction use the same inset; drawing stays at `OFFSET + column/height`. |
| `water/ui/WaterSimulator.scala` | Derive padded `w`/`h` on size change; recreate only when those differ; guard zero-size panels. |
| `water/ui/InteractionHandler.scala` | Map mouse X/Y into env space by subtracting `OFFSET` (column index and floor-vs-water hit test). |

## Tests

- Unit-test size mapping: given panel W×H and `OFFSET`, expected env dims (including `max(1, …)` for small panels).
- Extend column / mouse mapping tests for OFFSET-aware coordinates if a helper is extracted.

## Out of scope

- Preserving or resampling wave/terrain state across resize
- Scale-to-fit transforms / fixed grid with draw scaling
- Moving resize handling to a `ComponentListener` (paint-time check remains, consistent with fluid)

## Success criteria

- Resizing the Water window fills the bordered drawable area with a freshly initialized environment.
- Clicks/drags remain aligned with columns and floor/water after resize.
- Existing water model and renderer tests still pass; new mapping tests cover padded sizing and OFFSET mouse mapping.
