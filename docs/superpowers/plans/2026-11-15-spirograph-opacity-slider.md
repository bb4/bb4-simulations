# Spirograph Opacity Slider Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a 0–255 Opacity slider (default 255) to `ColorSliderGroup` so spirograph stroke color includes alpha.

**Architecture:** Extend shared Swing `ColorSliderGroup` in bb4-ui to build `new Color(r, g, b, a)`. Keep `ColorChangeListener.colorChanged(Color)` unchanged. Spirograph already stores and paints `state.color`, so no simulations source changes. Add `OPACITY` to message bundles that already define `RED`/`GREEN`/`BLUE`, including **bb4-common** (default `AppContext` bundle used when constructing the control).

**Tech Stack:** Scala 3, Swing (`JSlider`), ScalaTest `AnyFunSuite`, Gradle (`bb4-ui`, `bb4-common`)

## Global Constraints

- Opacity range **0–255**, initial value **255** (fully opaque)
- Include alpha via `new Color(r, g, b, opacity)` — no listener API change
- No checkerboard swatch; no optional opacity flag; no bb4-simulations UI wiring changes
- Commits happen in the repo that owns the files (`bb4-common`, `bb4-ui`); design/plan docs live in `bb4-simulations`

## File structure

| File | Role |
|------|------|
| `bb4-common/.../i18n/message*.properties` | `OPACITY` next to `RED`/`GREEN`/`BLUE` (required for default `AppContext`) |
| `bb4-ui/.../ui/message*.properties` | Same key for UI bundle locales |
| `bb4-ui/.../uber/message_de.properties` | Uber DE mirror that already has RGB |
| `bb4-ui/.../sliders/ColorSliderGroup.scala` | Opacity slider + RGBA swatch/notify |
| `bb4-ui/.../sliders/ColorSliderGroupSuite.scala` | Listener-based tests for default alpha and opacity changes |

---

### Task 1: i18n — `OPACITY` message keys

**Files:**
- Modify: `/Users/barry/projects/bb4/bb4-common/scala-source/com/barrybecker4/common/i18n/message.properties`
- Modify: `/Users/barry/projects/bb4/bb4-common/scala-source/com/barrybecker4/common/i18n/message_de.properties`
- Modify: `/Users/barry/projects/bb4/bb4-common/scala-source/com/barrybecker4/common/i18n/message_ja.properties`
- Modify: `/Users/barry/projects/bb4/bb4-common/scala-source/com/barrybecker4/common/i18n/message_vi.properties`
- Modify: `/Users/barry/projects/bb4/bb4-ui/scala-source/com/barrybecker4/ui/message.properties`
- Modify: `/Users/barry/projects/bb4/bb4-ui/scala-source/com/barrybecker4/ui/message_de.properties`
- Modify: `/Users/barry/projects/bb4/bb4-ui/scala-source/com/barrybecker4/ui/message_ja.properties`
- Modify: `/Users/barry/projects/bb4/bb4-ui/scala-source/com/barrybecker4/ui/message_vi.properties`
- Modify: `/Users/barry/projects/bb4/bb4-ui/scala-test/com/barrybecker4/ui/uber/message_de.properties`
- Skip empty `message_en.properties` files (ui + uber) — they are empty today; do not invent content
- Skip empty uber `message_ja.properties` / `message_vi.properties`

**Interfaces:**
- Consumes: existing `RED` / `GREEN` / `BLUE` key placement
- Produces: `OPACITY` string resource readable via `AppContext.getLabel("OPACITY")`

- [ ] **Step 1: Add `OPACITY` after `BLUE` in bb4-common English**

In `bb4-common/.../i18n/message.properties`, after `BLUE=Blue`:

```properties
OPACITY=Opacity
```

- [ ] **Step 2: Add locale variants in bb4-common**

`message_de.properties` (after `BLUE = Blue_g`):

```properties
OPACITY = Opacity_g
```

`message_ja.properties` (after `BLUE=青`):

```properties
OPACITY=\u4E0D\u900F\u660E\u5EA6
```

`message_vi.properties` (after `BLUE=Blue`):

```properties
OPACITY=\u0110\u1ED9 \u0111\u1EE5c
```

- [ ] **Step 3: Mirror keys in bb4-ui source message files**

Same values as common, placed after each file’s `BLUE` line:

- `message.properties` → `OPACITY = Opacity`
- `message_de.properties` → `OPACITY = Opacity_g`
- `message_ja.properties` → `OPACITY = \u4E0D\u900F\u660E\u5EA6`
- `message_vi.properties` → `OPACITY = \u0110\u1ED9 \u0111\u1EE5c`

- [ ] **Step 4: Uber DE test bundle**

In `bb4-ui/scala-test/.../uber/message_de.properties`, after `BLUE = Blue_g`:

```properties
OPACITY = Opacity_g
```

- [ ] **Step 5: Commit bb4-common i18n**

```bash
cd /Users/barry/projects/bb4/bb4-common
git add scala-source/com/barrybecker4/common/i18n/message.properties \
  scala-source/com/barrybecker4/common/i18n/message_de.properties \
  scala-source/com/barrybecker4/common/i18n/message_ja.properties \
  scala-source/com/barrybecker4/common/i18n/message_vi.properties
git commit -m "$(cat <<'EOF'
Add OPACITY i18n key alongside RGB color labels.

EOF
)"
```

- [ ] **Step 6: Commit bb4-ui i18n**

```bash
cd /Users/barry/projects/bb4/bb4-ui
git add scala-source/com/barrybecker4/ui/message.properties \
  scala-source/com/barrybecker4/ui/message_de.properties \
  scala-source/com/barrybecker4/ui/message_ja.properties \
  scala-source/com/barrybecker4/ui/message_vi.properties \
  scala-test/com/barrybecker4/ui/uber/message_de.properties
git commit -m "$(cat <<'EOF'
Add OPACITY label strings for ColorSliderGroup locales.

EOF
)"
```

---

### Task 2: Failing tests for default and changed opacity

**Files:**
- Create: `/Users/barry/projects/bb4/bb4-ui/scala-test/com/barrybecker4/ui/sliders/ColorSliderGroupSuite.scala`

**Interfaces:**
- Consumes: `ColorSliderGroup`, `ColorChangeListener`, `AppContext` + `MessageContext.COMMON_BUNDLE`
- Produces: failing suite that expects alpha 255 by default and alpha updates when the 4th `JSlider` changes

- [ ] **Step 1: Write the suite**

Create `ColorSliderGroupSuite.scala`:

```scala
/* Copyright by Barry G. Becker, 2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT */
package com.barrybecker4.ui.sliders

import com.barrybecker4.common.app.AppContext
import com.barrybecker4.common.i18n.{LocaleType, MessageContext}

import java.awt.Color
import javax.swing.JSlider
import org.scalatest.funsuite.AnyFunSuite

class ColorSliderGroupSuite extends AnyFunSuite {

  private def withCommonMessages[T](body: => T): T = {
    val ctx = new MessageContext(MessageContext.COMMON_BUNDLE)
    ctx.setLocale(LocaleType.ENGLISH)
    AppContext.injectMessageContext(ctx)
    body
  }

  private def slidersOf(group: ColorSliderGroup): Array[JSlider] =
    group.getComponents.collect { case s: JSlider => s }

  test("default notified color is opaque black") {
    withCommonMessages {
      var received: Option[Color] = None
      val group = new ColorSliderGroup
      group.setColorChangeListener((c: Color) => received = Some(c))
      val color = received.get
      assert(color.getRed == 0)
      assert(color.getGreen == 0)
      assert(color.getBlue == 0)
      assert(color.getAlpha == 255)
      assert(slidersOf(group).length == 4)
    }
  }

  test("opacity slider changes alpha on notified color") {
    withCommonMessages {
      var received: Option[Color] = None
      val group = new ColorSliderGroup
      group.setColorChangeListener((c: Color) => received = Some(c))
      val opacitySlider = slidersOf(group)(3)
      opacitySlider.setValue(128)
      assert(received.get.getAlpha == 128)
      assert(received.get.getRed == 0)
      assert(received.get.getGreen == 0)
      assert(received.get.getBlue == 0)
    }
  }
}
```

- [ ] **Step 2: Run tests — expect failure (no 4th slider / alpha always 255)**

```bash
cd /Users/barry/projects/bb4/bb4-ui
./gradlew test --tests 'com.barrybecker4.ui.sliders.ColorSliderGroupSuite'
```

Expected: FAIL — e.g. `slidersOf(group).length == 4` is false (only 3 sliders), and/or alpha assertion never reached for the opacity-change test.

- [ ] **Step 3: Commit failing test**

```bash
cd /Users/barry/projects/bb4/bb4-ui
git add scala-test/com/barrybecker4/ui/sliders/ColorSliderGroupSuite.scala
git commit -m "$(cat <<'EOF'
Add failing ColorSliderGroup tests for opacity slider.

EOF
)"
```

---

### Task 3: Implement opacity slider in `ColorSliderGroup`

**Files:**
- Modify: `/Users/barry/projects/bb4/bb4-ui/scala-source/com/barrybecker4/ui/sliders/ColorSliderGroup.scala`

**Interfaces:**
- Consumes: `AppContext.getLabel("OPACITY")`, existing RGB slider pattern
- Produces: four sliders; `updateSwatch()` notifies `new Color(r, g, b, opacity)` with opacity default 255

- [ ] **Step 1: Add OPACITY constant**

In companion object, after `BLUE`:

```scala
private val OPACITY = "OPACITY"
```

- [ ] **Step 2: Add opacity label, panel, and slider (initial 255)**

After the blue label/slider setup, mirror the RGB pattern:

```scala
private val opacity = new JLabel(getColorLabel(ColorSliderGroup.OPACITY) + "255", SwingConstants.LEFT)
val opacityPanel: JPanel = createColorLabelPanel(opacity)
private val opacitySlider = new JSlider(SwingConstants.HORIZONTAL, 0, 255, 255)
opacitySlider.addChangeListener(this)
```

Add to the panel after blue:

```scala
add(opacityPanel)
add(opacitySlider)
```

Keep `updateSwatch()` call at the end of construction.

- [ ] **Step 3: Include alpha in `updateSwatch`**

Replace the opaque RGB constructor:

```scala
def updateSwatch(): Unit = {
  val color = new Color(
    redSlider.getValue,
    greenSlider.getValue,
    blueSlider.getValue,
    opacitySlider.getValue)
  colorListener.foreach(_.colorChanged(color))
  swatch.setBackground(color)
  swatch.setOpaque(true)
  swatch.repaint()
}
```

- [ ] **Step 4: Update `stateChanged` for opacity**

```scala
override def stateChanged(e: ChangeEvent): Unit = {
  val src = e.getSource.asInstanceOf[JSlider]
  if (src eq redSlider) red.setText(getColorLabel(ColorSliderGroup.RED) + redSlider.getValue)
  else if (src eq greenSlider) green.setText(getColorLabel(ColorSliderGroup.GREEN) + greenSlider.getValue)
  else if (src eq blueSlider) blue.setText(getColorLabel(ColorSliderGroup.BLUE) + blueSlider.getValue)
  else if (src eq opacitySlider) opacity.setText(getColorLabel(ColorSliderGroup.OPACITY) + opacitySlider.getValue)
  updateSwatch()
}
```

- [ ] **Step 5: Run tests — expect pass**

```bash
cd /Users/barry/projects/bb4/bb4-ui
./gradlew test --tests 'com.barrybecker4.ui.sliders.ColorSliderGroupSuite'
```

Expected: BUILD SUCCESSFUL, both tests PASS.

- [ ] **Step 6: Commit implementation**

```bash
cd /Users/barry/projects/bb4/bb4-ui
git add scala-source/com/barrybecker4/ui/sliders/ColorSliderGroup.scala
git commit -m "$(cat <<'EOF'
Add opacity slider to ColorSliderGroup for RGBA colors.

EOF
)"
```

---

### Task 4: Publish / wire and manual spirograph check

**Files:**
- None in bb4-simulations source (verify only)
- Possibly local publish of bb4-ui / bb4-common so simulations picks up SNAPSHOT

**Interfaces:**
- Consumes: updated `bb4Libs.ui` / composite includeBuild as used in this monorepo
- Produces: spirograph run with translucent strokes when opacity &lt; 255

- [ ] **Step 1: Ensure simulations sees updated libraries**

If using composite/`includeBuild`, a bb4-ui compile is enough. Otherwise publish locally:

```bash
cd /Users/barry/projects/bb4/bb4-common && ./gradlew publishToMavenLocal
cd /Users/barry/projects/bb4/bb4-ui && ./gradlew publishToMavenLocal
```

- [ ] **Step 2: Compile simulations against the new UI**

```bash
cd /Users/barry/projects/bb4/bb4-simulations
./gradlew compileScala
```

Expected: SUCCESS with no missing-symbol errors related to color controls.

- [ ] **Step 3: Manual spirograph verification**

```bash
cd /Users/barry/projects/bb4/bb4-simulations
./gradlew run -PmainClass=com.barrybecker4.simulation.spirograph.Spirograph
```

(or the project’s usual spirograph launch task if different)

Checklist:

1. Opacity slider appears under Blue; shows **255** initially; drawn graph matches prior opaque look.
2. Set opacity to ~80, choose a bright color, Draw: strokes are translucent on white; overlaps blend.
3. Change R/G/B while opacity &lt; 255: new strokes use both new hue and alpha.

- [ ] **Step 4: Commit design + plan docs in bb4-simulations (if not already)**

```bash
cd /Users/barry/projects/bb4/bb4-simulations
git add docs/superpowers/specs/2026-11-15-spirograph-opacity-slider-design.md \
  docs/superpowers/plans/2026-11-15-spirograph-opacity-slider.md
git commit -m "$(cat <<'EOF'
Document spirograph opacity slider design and plan.

EOF
)"
```

---

## Spec coverage self-review

| Spec requirement | Task |
|------------------|------|
| Opacity slider 0–255 default 255 | Task 3 |
| Next to R/G/B in `ColorSliderGroup` | Task 3 |
| `new Color(r,g,b,a)` + existing listener | Task 3 |
| i18n `OPACITY` in ui (+ common for AppContext) | Task 1 |
| No simulations wiring change | Task 4 verify-only |
| No checkerboard / optional flag | Not implemented (YAGNI) |
| Manual translucent draw check | Task 4 |

**Note vs design doc:** Spec listed only bb4-ui message files; plan also adds bb4-common because `RED`/`GREEN`/`BLUE` already live there and default `AppContext` uses that bundle when constructing `ColorSliderGroup` in tests and before full applet init.
