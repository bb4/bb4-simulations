# Spirograph Opacity Slider Design

**Date:** 2026-11-15  
**Primary change:** `bb4-ui` `ColorSliderGroup`  
**Consumer:** `bb4-simulations` spirograph (unchanged wiring)

## Goal

Add an opacity slider alongside the existing R/G/B sliders used by the spirograph color controls. Default is fully opaque.

## Decision summary

| Choice | Decision |
|--------|----------|
| Where | Extend shared `ColorSliderGroup` in bb4-ui (only current consumer is spirograph) |
| Range | 0–255 (same as RGB), default **255** |
| API | Keep `ColorChangeListener.colorChanged(Color)`; include alpha in the `Color` |
| Swatch | Use the RGBA color as background; no checkerboard |
| Simulations | No code changes; existing `GraphState` / `GraphRenderer` already use `state.color` |

## UI & behavior

In `com.barrybecker4.ui.sliders.ColorSliderGroup`:

1. Add an **Opacity** `JSlider` after Blue: min 0, max 255, initial 255.
2. Add label `Opacity : <value>` using the same pattern as Red/Green/Blue.
3. On any slider change (including opacity), `updateSwatch()` builds

   `new Color(red, green, blue, opacity)` and notifies the listener.
4. Swatch `setBackground` uses that color. At alpha < 255 it will look washed against the panel background (acceptable for this change).

i18n: add `OPACITY` next to existing `RED` / `GREEN` / `BLUE` keys in:

- `bb4-ui/scala-source/com/barrybecker4/ui/message.properties` and `message_en.properties` → `OPACITY = Opacity`
- `message_de.properties` / uber de → follow that file’s RED/GREEN/BLUE style (placeholder suffix if present)
- `message_ja.properties` / `message_vi.properties` (and uber mirrors) → translated label consistent with those files’ color names
- Matching keys in `bb4-ui/scala-test/com/barrybecker4/ui/uber/message*.properties` where RED/GREEN/BLUE already appear

## Data flow (unchanged path)

```
ColorSliderGroup.updateSwatch()
  → ColorChangeListener.colorChanged(Color)   // GraphState
  → GraphState.color
  → GraphRenderer.drawSegment → OfflineGraphics.setColor(state.color)
```

`OfflineGraphics` uses ARGB-compatible images, so translucent strokes composite over the white background and overlapping segments without further changes.

Opacity changes mid-draw affect **new** strokes only — same behavior as changing R/G/B today.

## Scope

**In**

- `ColorSliderGroup.scala` opacity slider + alpha in `Color`
- Opacity label strings in the message property files listed above

**Out**

- Checkerboard transparency preview
- Optional constructor flag to hide opacity
- Spirograph-only duplicate slider
- Changes to already-rasterized segments when opacity changes
- bb4-simulations source changes (unless a dependency bump is required to pick up bb4-ui)

## Verification

1. Build bb4-ui with the change; ensure bb4-simulations resolves the updated artifact (local publish / composite as usual for this monorepo workflow).
2. Launch spirograph: opacity at 255 matches prior fully opaque look.
3. Lower opacity and draw: curve strokes are translucent; overlapping passes darken/blend as expected on the white background.
4. Move R/G/B with opacity < 255: color and alpha both update on new strokes.

## Approach rejected

- **Spirograph-only slider** — duplicates color UI and splits opacity from the swatch.
- **Optional opacity flag on** `ColorSliderGroup` — unused flexibility (YAGNI; single consumer).
- **0.0–1.0 UI scale** — rejected; use 0–255 for consistency with RGB.
