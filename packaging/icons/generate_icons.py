#!/usr/bin/env python3
"""Generate a family of app icons for bb4-simulations (PNG + ICO + ICNS).

Visual language (keep this consistent across other bb4 installer repos):
- 1024 canvas, macOS-style rounded squircle
- Deep navy fill (#0D2438), 1px inner rim in muted teal
- Cream glyphs (#F4F1DE) with a shared accent palette (cyan / gold / coral)
- No wordmarks; one distinctive geometric motif per app
"""

from __future__ import annotations

import math
import os
import shutil
import subprocess
import tempfile
from pathlib import Path

from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parent
SIZE = 1024
NAVY = (13, 36, 56, 255)
TEAL_RIM = (58, 140, 150, 255)
CREAM = (244, 241, 222, 255)
CYAN = (78, 205, 196, 255)
GOLD = (232, 184, 74, 255)
CORAL = (224, 122, 95, 255)
SOFT = (90, 140, 170, 255)


def new_canvas() -> tuple[Image.Image, ImageDraw.ImageDraw]:
    img = Image.new("RGBA", (SIZE, SIZE), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    pad = 48
    radius = 220
    draw.rounded_rectangle([pad, pad, SIZE - pad, SIZE - pad], radius=radius, fill=NAVY)
    draw.rounded_rectangle(
        [pad + 18, pad + 18, SIZE - pad - 18, SIZE - pad - 18],
        radius=radius - 12,
        outline=TEAL_RIM,
        width=8,
    )
    return img, draw


def save_all(app_id: str, img: Image.Image) -> None:
    out = ROOT / app_id
    out.mkdir(parents=True, exist_ok=True)
    png_path = out / "icon.png"
    img.save(png_path, "PNG")

    ico_sizes = [(16, 16), (32, 32), (48, 48), (64, 64), (128, 128), (256, 256)]
    img.save(out / "icon.ico", sizes=ico_sizes)

    if shutil.which("iconutil") and shutil.which("sips"):
        write_icns(img, out / "icon.icns")
    else:
        print(f"warning: iconutil/sips not available; skipped icns for {app_id}")


def write_icns(img: Image.Image, dest: Path) -> None:
    with tempfile.TemporaryDirectory() as tmp:
        iconset = Path(tmp) / "icon.iconset"
        iconset.mkdir()
        specs = [
            ("icon_16x16.png", 16),
            ("icon_16x16@2x.png", 32),
            ("icon_32x32.png", 32),
            ("icon_32x32@2x.png", 64),
            ("icon_128x128.png", 128),
            ("icon_128x128@2x.png", 256),
            ("icon_256x256.png", 256),
            ("icon_256x256@2x.png", 512),
            ("icon_512x512.png", 512),
            ("icon_512x512@2x.png", 1024),
        ]
        master = Path(tmp) / "master.png"
        img.save(master)
        for name, px in specs:
            subprocess.check_call(
                ["sips", "-z", str(px), str(px), str(master), "--out", str(iconset / name)],
                stdout=subprocess.DEVNULL,
            )
        subprocess.check_call(["iconutil", "-c", "icns", str(iconset), "-o", str(dest)])


def draw_reactiondiffusion(draw: ImageDraw.ImageDraw) -> None:
    blobs = [
        (380, 400, 210, CYAN),
        (640, 520, 180, CORAL),
        (520, 340, 90, GOLD),
        (300, 620, 70, SOFT),
        (720, 340, 60, CREAM),
    ]
    for x, y, r, color in blobs:
        draw.ellipse([x - r, y - r, x + r, y + r], fill=color)
    for i in range(18):
        ang = i * math.tau / 18
        x = 512 + int(math.cos(ang) * 280)
        y = 512 + int(math.sin(ang) * 220)
        r = 14 + (i % 3) * 6
        draw.ellipse([x - r, y - r, x + r, y + r], fill=CREAM if i % 2 == 0 else CYAN)


def draw_henonexplorer(draw: ImageDraw.ImageDraw) -> None:
    a = 1.4
    pts = []
    x, y = 0.1, 0.1
    for _ in range(4000):
        x, y = 1 - a * x * x + y, 0.3 * x
        pts.append((int(512 + x * 220), int(520 - y * 280)))
    draw.point(pts, fill=GOLD)
    draw.point(pts[::3], fill=CYAN)


def draw_fractalexplorer(draw: ImageDraw.ImageDraw) -> None:
    # Stylized Mandelbrot cardioid + bulb
    draw.ellipse([250, 300, 720, 770], fill=CYAN)
    draw.ellipse([200, 420, 430, 650], fill=GOLD)
    draw.ellipse([470, 250, 560, 340], fill=CORAL)
    draw.ellipse([430, 470, 540, 580], fill=NAVY)
    draw.ellipse([300, 490, 390, 580], fill=NAVY)


def draw_cave(draw: ImageDraw.ImageDraw) -> None:
    draw.ellipse([220, 260, 800, 860], fill=SOFT)
    draw.ellipse([300, 340, 720, 820], fill=NAVY)
    draw.polygon([(360, 700), (430, 520), (510, 680), (580, 500), (660, 720), (360, 720)], fill=GOLD)
    draw.ellipse([470, 600, 550, 680], fill=CORAL)


def draw_dungeon(draw: ImageDraw.ImageDraw) -> None:
    rooms = [(240, 280, 480, 500), (520, 280, 780, 460), (240, 560, 500, 780), (540, 500, 780, 780)]
    for box in rooms:
        draw.rounded_rectangle(box, radius=24, outline=CREAM, width=10)
    draw.rectangle([460, 360, 540, 400], fill=CYAN)
    draw.rectangle([340, 480, 380, 580], fill=CYAN)
    draw.rectangle([620, 440, 660, 520], fill=GOLD)


def draw_conway(draw: ImageDraw.ImageDraw) -> None:
    origin, cell, gap = 250, 70, 10
    live = {(1, 0), (2, 1), (0, 2), (1, 2), (2, 2)}  # glider
    for r in range(7):
        for c in range(7):
            x0 = origin + c * (cell + gap)
            y0 = origin + r * (cell + gap)
            color = GOLD if (c, r) in live else SOFT
            draw.rounded_rectangle([x0, y0, x0 + cell, y0 + cell], radius=10, fill=color)


def draw_snake(draw: ImageDraw.ImageDraw) -> None:
    pts = []
    for i in range(80):
        t = i / 79
        x = 220 + t * 580
        y = 512 + math.sin(t * math.pi * 2.2) * 160
        pts.append((x, y))
    draw.line(pts, fill=CYAN, width=54, joint="curve")
    hx, hy = pts[-1]
    draw.ellipse([hx - 40, hy - 40, hx + 40, hy + 40], fill=GOLD)
    draw.ellipse([hx + 6, hy - 16, hx + 22, hy], fill=NAVY)


def draw_dice(draw: ImageDraw.ImageDraw) -> None:
    draw.rounded_rectangle([280, 280, 744, 744], radius=80, fill=CREAM)
    pips = [(400, 400), (512, 512), (624, 624), (400, 624), (624, 400)]
    for x, y in pips:
        draw.ellipse([x - 38, y - 38, x + 38, y + 38], fill=CORAL)


def draw_stock(draw: ImageDraw.ImageDraw) -> None:
    pts = [(220, 680), (340, 560), (430, 600), (530, 420), (640, 470), (780, 300)]
    draw.line(pts, fill=CYAN, width=18, joint="curve")
    for x, y in pts:
        draw.ellipse([x - 14, y - 14, x + 14, y + 14], fill=GOLD)
    draw.line([(220, 760), (800, 760)], fill=SOFT, width=8)


def draw_trading(draw: ImageDraw.ImageDraw) -> None:
    candles = [(300, 360, 620, True), (420, 300, 540, False), (540, 280, 500, True), (660, 240, 430, False)]
    for x, top, height, up in candles:
        color = CYAN if up else CORAL
        draw.rectangle([x - 6, top, x + 6, top + height + 80], fill=color)
        draw.rectangle([x - 28, top + 40, x + 28, top + height], fill=color)


def draw_habitat(draw: ImageDraw.ImageDraw) -> None:
    draw.polygon([(512, 240), (300, 560), (724, 560)], fill=CYAN)
    draw.rectangle([480, 560, 544, 760], fill=GOLD)
    draw.ellipse([220, 680, 340, 800], fill=CORAL)
    draw.ellipse([700, 660, 820, 780], fill=CREAM)
    draw.ellipse([250, 700, 280, 730], fill=NAVY)
    draw.ellipse([740, 690, 770, 720], fill=NAVY)


def draw_verhulst(draw: ImageDraw.ImageDraw) -> None:
    pts = []
    for i in range(80):
        t = i / 79
        y = 1 / (1 + math.exp(-10 * (t - 0.45)))
        pts.append((220 + t * 580, 760 - y * 460))
    draw.line(pts, fill=GOLD, width=18, joint="curve")
    draw.line([(220, 760), (800, 760), (220, 760), (220, 260)], fill=SOFT, width=8)


def draw_voronoi(draw: ImageDraw.ImageDraw) -> None:
    sites = [(360, 360), (640, 340), (500, 560), (320, 680), (700, 660), (520, 780)]
    colors = [CYAN, GOLD, CORAL, SOFT, CREAM, TEAL_RIM]
    # Approximate cells as polygons around a hex-ish partition
    polys = [
        [(220, 240), (500, 240), (460, 430), (300, 470)],
        [(500, 240), (800, 240), (800, 500), (560, 430)],
        [(300, 470), (460, 430), (560, 430), (640, 620), (400, 640)],
        [(220, 470), (300, 470), (400, 640), (220, 800)],
        [(560, 430), (800, 500), (800, 800), (640, 620)],
        [(400, 640), (640, 620), (800, 800), (220, 800)],
    ]
    for poly, color in zip(polys, colors):
        draw.polygon(poly, fill=color, outline=NAVY)
    for x, y in sites:
        draw.ellipse([x - 14, y - 14, x + 14, y + 14], fill=NAVY)


def draw_predprey(draw: ImageDraw.ImageDraw) -> None:
    prey, pred = [], []
    for i in range(90):
        t = i / 89 * math.tau * 2
        prey.append((220 + i * 6.4, 512 - math.sin(t) * 160))
        pred.append((220 + i * 6.4, 512 - math.sin(t - 1.1) * 120))
    draw.line(prey, fill=CYAN, width=14, joint="curve")
    draw.line(pred, fill=CORAL, width=14, joint="curve")


def draw_fluid(draw: ImageDraw.ImageDraw) -> None:
    for k, color in enumerate((CYAN, GOLD, CREAM)):
        pts = []
        for i in range(60):
            t = i / 59 * math.tau
            r = 90 + k * 70 + 18 * math.sin(3 * t)
            pts.append((512 + r * math.cos(t + k), 512 + r * math.sin(t + k)))
        pts.append(pts[0])
        draw.line(pts, fill=color, width=16, joint="curve")


def draw_liquid(draw: ImageDraw.ImageDraw) -> None:
    drops = [(400, 320, 70), (512, 280, 90), (640, 340, 60), (460, 520, 110), (620, 540, 80), (540, 700, 50)]
    for x, y, r in drops:
        draw.ellipse([x - r, y - r, x + r, y + int(r * 1.15)], fill=CYAN)
        draw.ellipse([x - r // 3, y - r // 2, x, y], fill=CREAM)


def draw_water(draw: ImageDraw.ImageDraw) -> None:
    for i, amp in enumerate((90, 70, 50)):
        pts = [(200, 520 + i * 70)]
        for x in range(200, 830, 8):
            y = 500 + i * 80 + math.sin((x + i * 40) / 70) * amp
            pts.append((x, y))
        draw.line(pts, fill=CYAN if i == 0 else SOFT, width=16, joint="curve")
    draw.polygon([(200, 760), (200, 640), (820, 700), (820, 760)], fill=(40, 90, 140, 255))


def draw_trebuchet(draw: ImageDraw.ImageDraw) -> None:
    draw.line([(280, 760), (520, 320)], fill=CREAM, width=22)
    draw.line([(520, 320), (760, 420)], fill=GOLD, width=16)
    draw.line([(400, 760), (640, 760)], fill=SOFT, width=18)
    draw.polygon([(500, 300), (540, 300), (520, 240)], fill=CORAL)
    draw.ellipse([740, 400, 800, 460], fill=CYAN)


def draw_spirograph(draw: ImageDraw.ImageDraw) -> None:
    pts = []
    R, r, d = 180, 70, 110
    for i in range(720):
        t = i * math.tau / 180
        x = 512 + (R - r) * math.cos(t) + d * math.cos((R - r) / r * t)
        y = 512 + (R - r) * math.sin(t) - d * math.sin((R - r) / r * t)
        pts.append((x, y))
    draw.line(pts, fill=GOLD, width=8, joint="curve")
    draw.ellipse([512 - R, 512 - R, 512 + R, 512 + R], outline=CYAN, width=6)


def draw_sierpinski(draw: ImageDraw.ImageDraw) -> None:
    def tri(p1, p2, p3, depth):
        if depth == 0:
            draw.polygon([p1, p2, p3], outline=GOLD)
            return
        mid = lambda a, b: ((a[0] + b[0]) / 2, (a[1] + b[1]) / 2)
        tri(p1, mid(p1, p2), mid(p1, p3), depth - 1)
        tri(p2, mid(p2, p1), mid(p2, p3), depth - 1)
        tri(p3, mid(p3, p1), mid(p3, p2), depth - 1)

    draw.polygon([(512, 220), (220, 780), (804, 780)], fill=CYAN)
    tri((512, 220), (220, 780), (804, 780), 3)


def draw_lsystem(draw: ImageDraw.ImageDraw) -> None:
    def branch(x, y, angle, depth, length):
        if depth == 0:
            return
        x2 = x + math.cos(angle) * length
        y2 = y - math.sin(angle) * length
        width = max(4, depth * 4)
        color = GOLD if depth < 3 else CYAN
        draw.line([(x, y), (x2, y2)], fill=color, width=width)
        branch(x2, y2, angle + 0.5, depth - 1, length * 0.7)
        branch(x2, y2, angle - 0.55, depth - 1, length * 0.7)

    branch(512, 800, math.pi / 2, 8, 140)


def draw_wavefunctioncollapse(draw: ImageDraw.ImageDraw) -> None:
    tiles = 6
    origin, span = 250, 524
    s = span / tiles
    palette = [CYAN, GOLD, CORAL, CREAM, SOFT, TEAL_RIM]
    for r in range(tiles):
        for c in range(tiles):
            x0 = origin + c * s
            y0 = origin + r * s
            color = palette[(r * 3 + c) % len(palette)]
            draw.rectangle([x0, y0, x0 + s - 6, y0 + s - 6], fill=color)
            if (r + c) % 2 == 0:
                draw.rectangle([x0 + 16, y0 + 16, x0 + s - 22, y0 + s - 22], fill=NAVY)


def draw_graphing(draw: ImageDraw.ImageDraw) -> None:
    draw.line([(240, 760), (240, 260), (800, 260)], fill=SOFT, width=8)
    pts = [(280, 700), (380, 520), (500, 560), (620, 360), (760, 320)]
    draw.line(pts, fill=GOLD, width=16, joint="curve")


def draw_funcinverse(draw: ImageDraw.ImageDraw) -> None:
    a = [(260 + i * 6, 720 - (i * i) * 0.08) for i in range(70)]
    b = [(260 + (i * i) * 0.08, 720 - i * 6) for i in range(70)]
    draw.line(a, fill=CYAN, width=12, joint="curve")
    draw.line(b, fill=CORAL, width=12, joint="curve")
    draw.line([(240, 760), (780, 220)], fill=SOFT, width=6)


def draw_parameter(draw: ImageDraw.ImageDraw) -> None:
    bars = [180, 260, 140, 320, 220, 280]
    for i, h in enumerate(bars):
        x = 280 + i * 80
        draw.rectangle([x, 760 - h, x + 50, 760], fill=GOLD if i % 2 == 0 else CYAN)


def draw_complexmapping(draw: ImageDraw.ImageDraw) -> None:
    for i in range(8):
        p = i / 7
        draw.arc([260 + i * 12, 260 + i * 8, 760 - i * 18, 760 - i * 4], start=20, end=320, fill=CYAN, width=6)
    draw.ellipse([480, 480, 560, 560], fill=GOLD)


DRAWERS = {
    "reactiondiffusion": draw_reactiondiffusion,
    "henonexplorer": draw_henonexplorer,
    "fractalexplorer": draw_fractalexplorer,
    "cave": draw_cave,
    "dungeon": draw_dungeon,
    "conway": draw_conway,
    "snake": draw_snake,
    "dice": draw_dice,
    "stock": draw_stock,
    "trading": draw_trading,
    "habitat": draw_habitat,
    "verhulst": draw_verhulst,
    "voronoi": draw_voronoi,
    "predprey": draw_predprey,
    "fluid": draw_fluid,
    "liquid": draw_liquid,
    "water": draw_water,
    "trebuchet": draw_trebuchet,
    "spirograph": draw_spirograph,
    "sierpinski": draw_sierpinski,
    "lsystem": draw_lsystem,
    "graphing": draw_graphing,
    "funcinverse": draw_funcinverse,
    "parameter": draw_parameter,
    "complexmapping": draw_complexmapping,
    "waveFunctionCollapse": draw_wavefunctioncollapse,
}


def main() -> None:
    os.chdir(ROOT)
    for app_id, drawer in DRAWERS.items():
        img, draw = new_canvas()
        drawer(draw)
        save_all(app_id, img)
        print(f"wrote {app_id}")


if __name__ == "__main__":
    main()
