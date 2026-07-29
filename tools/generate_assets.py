"""Generate LightHealth brand assets (icon, banner, gallery cards)."""
from __future__ import annotations

from pathlib import Path

from PIL import Image, ImageDraw, ImageFilter, ImageFont

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "assets"
ASSETS.mkdir(exist_ok=True)


def load_font(size: int, bold: bool = False) -> ImageFont.FreeTypeFont | ImageFont.ImageFont:
    candidates = [
        r"C:\Windows\Fonts\segoeuib.ttf" if bold else r"C:\Windows\Fonts\segoeui.ttf",
        r"C:\Windows\Fonts\arialbd.ttf" if bold else r"C:\Windows\Fonts\arial.ttf",
        r"C:\Windows\Fonts\calibrib.ttf" if bold else r"C:\Windows\Fonts\calibri.ttf",
    ]
    for path in candidates:
        try:
            return ImageFont.truetype(path, size)
        except OSError:
            continue
    return ImageFont.load_default()


def lerp(a: int, b: int, t: float) -> int:
    return int(a + (b - a) * t)


def gradient(size: tuple[int, int], c1: tuple[int, int, int], c2: tuple[int, int, int]) -> Image.Image:
    w, h = size
    img = Image.new("RGB", size)
    px = img.load()
    for y in range(h):
        for x in range(w):
            t = 0.65 * (y / max(h - 1, 1)) + 0.35 * ((x + y) / (w + h))
            px[x, y] = (
                lerp(c1[0], c2[0], t),
                lerp(c1[1], c2[1], t),
                lerp(c1[2], c2[2], t),
            )
    return img


def soft_circle(
    img: Image.Image,
    xy: tuple[int, int],
    radius: int,
    color: tuple[int, int, int, int],
    blur: int = 18,
) -> Image.Image:
    layer = Image.new("RGBA", img.size, (0, 0, 0, 0))
    d = ImageDraw.Draw(layer)
    x, y = xy
    d.ellipse((x - radius, y - radius, x + radius, y + radius), fill=color)
    layer = layer.filter(ImageFilter.GaussianBlur(blur))
    return Image.alpha_composite(img.convert("RGBA"), layer)


def rounded_rect(draw: ImageDraw.ImageDraw, box, r, fill=None, outline=None, width: int = 1) -> None:
    draw.rounded_rectangle(box, radius=r, fill=fill, outline=outline, width=width)


def draw_health_bar(
    draw: ImageDraw.ImageDraw,
    box: tuple[int, int, int, int],
    fraction: float = 0.75,
    segments: int | None = 10,
    pad: int = 4,
    fill_color: tuple[int, int, int, int] = (46, 204, 96, 255),
) -> None:
    x0, y0, x1, y1 = box
    rounded_rect(draw, box, 10, fill=(18, 22, 28, 230), outline=(40, 55, 45, 255), width=2)
    inner = (x0 + pad, y0 + pad, x1 - pad, y1 - pad)
    rounded_rect(draw, inner, 7, fill=(28, 36, 32, 255))
    ix0, iy0, ix1, iy1 = inner
    full_w = ix1 - ix0
    fill_w = int(full_w * max(0.0, min(1.0, fraction)))
    if fill_w > 0:
        fill_box = (ix0, iy0, ix0 + max(fill_w, 8), iy1)
        rounded_rect(draw, fill_box, 7, fill=fill_color)
        shine_h = max(2, (iy1 - iy0) // 3)
        rounded_rect(
            draw,
            (ix0 + 2, iy0 + 2, ix0 + max(fill_w - 2, 6), iy0 + 2 + shine_h),
            4,
            fill=(140, 255, 180, 90),
        )
    if segments:
        for i in range(1, segments):
            sx = ix0 + int(full_w * i / segments)
            draw.line((sx, iy0 + 2, sx, iy1 - 2), fill=(10, 14, 12, 160), width=2)


def draw_heart(draw: ImageDraw.ImageDraw, cx: int, cy: int, s: float, fill) -> None:
    draw.ellipse((cx - s * 0.45, cy - s * 0.35, cx - s * 0.05, cy + s * 0.05), fill=fill)
    draw.ellipse((cx + s * 0.05, cy - s * 0.35, cx + s * 0.45, cy + s * 0.05), fill=fill)
    draw.polygon(
        [(cx - s * 0.48, cy - s * 0.05), (cx + s * 0.48, cy - s * 0.05), (cx, cy + s * 0.5)],
        fill=fill,
    )


def make_icon() -> None:
    s = 512
    img = gradient((s, s), (8, 18, 14), (18, 48, 32)).convert("RGBA")
    img = soft_circle(img, (s // 2, s // 2 + 20), 180, (40, 220, 110, 70), blur=40)
    img = soft_circle(img, (s // 2, 160), 120, (80, 255, 140, 50), blur=30)
    d = ImageDraw.Draw(img, "RGBA")

    margin = 18
    rounded_rect(d, (margin, margin, s - margin, s - margin), 96, fill=(12, 20, 16, 240), outline=(55, 90, 65, 255), width=6)
    rounded_rect(
        d,
        (margin + 10, margin + 10, s - margin - 10, s - margin - 10),
        86,
        fill=(10, 16, 13, 255),
    )

    draw_heart(d, s // 2, 185, 150, (55, 230, 120, 255))
    draw_heart(d, s // 2, 185, 95, (14, 22, 18, 255))
    draw_heart(d, s // 2, 185, 70, (70, 245, 140, 255))

    draw_health_bar(d, (90, 300, 422, 348), fraction=0.72, segments=10, pad=5)
    d.text((s // 2, 380), "15 / 20", font=load_font(36, bold=True), fill=(230, 245, 235, 255), anchor="mm")
    d.text((400, 275), "-5", font=load_font(28, bold=True), fill=(255, 85, 85, 255), anchor="mm")
    d.text((s // 2, 450), "LIGHTHEALTH", font=load_font(22, bold=True), fill=(140, 200, 160, 220), anchor="mm")

    out = img.convert("RGB")
    out.save(ASSETS / "icon.png", optimize=True)
    out.resize((96, 96), Image.Resampling.LANCZOS).save(ASSETS / "icon-96.png", optimize=True)
    print("icon ok")


def make_banner() -> None:
    w, h = 1280, 720
    img = gradient((w, h), (6, 12, 10), (14, 40, 28)).convert("RGBA")
    for i in range(3):
        img = soft_circle(img, (220 + i * 40, 360), 280 - i * 40, (30, 180, 90, 35), blur=50)
    img = soft_circle(img, (1000, 200), 200, (50, 220, 120, 40), blur=45)
    d = ImageDraw.Draw(img, "RGBA")

    title = load_font(86, bold=True)
    sub = load_font(32)
    small = load_font(24, bold=True)
    tiny = load_font(20)

    d.text((80, 140), "LightHealth", font=title, fill=(245, 255, 250, 255))
    d.rounded_rectangle((84, 250, 320, 258), radius=4, fill=(55, 230, 120, 255))
    d.text((84, 280), "Modern mob health feedback", font=sub, fill=(190, 220, 200, 255))
    d.text((84, 330), "Paper  ·  Folia  ·  zero hard deps", font=tiny, fill=(130, 170, 150, 255))

    chips = ["Hologram", "Damage #", "Actionbar", "Bossbar", "Look-at"]
    x, y = 84, 400
    for chip in chips:
        bbox = d.textbbox((0, 0), chip, font=small)
        tw, th = bbox[2] - bbox[0], bbox[3] - bbox[1]
        pad_x, pad_y = 16, 10
        box = (x, y, x + tw + pad_x * 2, y + th + pad_y * 2)
        rounded_rect(d, box, 14, fill=(20, 40, 30, 230), outline=(55, 120, 80, 255), width=2)
        d.text((x + pad_x, y + pad_y - 1), chip, font=small, fill=(180, 255, 200, 255))
        x = box[2] + 12

    card = (720, 120, 1200, 600)
    rounded_rect(d, card, 28, fill=(10, 16, 14, 230), outline=(50, 90, 65, 255), width=3)
    d.text((960, 170), "Wither Skeleton", font=small, fill=(200, 220, 210, 255), anchor="mm")
    draw_health_bar(d, (780, 200, 1140, 248), fraction=0.75, segments=10, pad=5)
    d.text((960, 275), "15 / 20 HP", font=load_font(28, bold=True), fill=(230, 245, 235, 255), anchor="mm")
    d.text((1100, 320), "-12", font=load_font(42, bold=True), fill=(255, 90, 90, 255), anchor="mm")
    d.text((820, 340), "✦ -24", font=load_font(34, bold=True), fill=(255, 210, 80, 255), anchor="mm")
    d.text((960, 420), "Boss bar", font=tiny, fill=(140, 170, 155, 255), anchor="mm")
    draw_health_bar(d, (800, 445, 1120, 485), fraction=0.4, segments=None, pad=4, fill_color=(230, 180, 50, 255))
    d.text((960, 540), "Actionbar  ·  8/20  -3", font=tiny, fill=(160, 200, 175, 255), anchor="mm")

    d.text((80, 660), "en  ·  ru  ·  es  ·  zh", font=tiny, fill=(110, 150, 130, 255))
    d.text((1200, 660), "MIT", font=tiny, fill=(110, 150, 130, 255), anchor="rm")

    img.convert("RGB").save(ASSETS / "banner.png", optimize=True, quality=95)
    print("banner ok")


def make_feature_card(
    filename: str,
    title: str,
    lines: list[str],
    accent: tuple[int, int, int],
    fraction: float,
    fill_color: tuple[int, int, int, int] | None = None,
) -> None:
    w, h = 960, 540
    img = gradient((w, h), (8, 14, 12), (16, 36, 26)).convert("RGBA")
    img = soft_circle(img, (w // 2, h // 2), 220, (*accent, 45), blur=50)
    d = ImageDraw.Draw(img, "RGBA")
    rounded_rect(d, (40, 40, w - 40, h - 40), 32, fill=(12, 18, 15, 235), outline=(*accent, 180), width=3)

    d.text((80, 90), title, font=load_font(48, bold=True), fill=(245, 255, 250, 255))
    d.rounded_rectangle((84, 160, 200, 168), radius=4, fill=(*accent, 255))

    y = 200
    for line in lines:
        d.text((84, y), line, font=load_font(28), fill=(180, 210, 195, 255))
        y += 44

    fc = fill_color or (*accent, 255)
    draw_health_bar(d, (80, 400, w - 80, 460), fraction=fraction, segments=10, pad=6, fill_color=fc)
    img.convert("RGB").save(ASSETS / filename, optimize=True, quality=95)
    print(filename, "ok")


def make_og() -> None:
    img = Image.open(ASSETS / "banner.png").resize((1200, 630), Image.Resampling.LANCZOS)
    img.save(ASSETS / "og.png", optimize=True, quality=95)
    print("og ok")


def maybe_import_ai() -> None:
    """Prefer AI icon/banner if present and convert to PNG."""
    session_images = Path.home() / ".grok" / "sessions"
    # also check relative session path from tool output
    candidates = [
        Path(r"C:\Users\dimas\.grok\sessions\C%3A%5CUsers%5Cdimas\019fad41-0932-7961-834d-57ea50fa6cd8\images"),
    ]
    for folder in candidates:
        if not folder.is_dir():
            continue
        icon_src = folder / "1.jpg"
        banner_src = folder / "2.jpg"
        if icon_src.exists():
            im = Image.open(icon_src).convert("RGB")
            # square crop center
            side = min(im.size)
            left = (im.width - side) // 2
            top = (im.height - side) // 2
            im = im.crop((left, top, left + side, top + side)).resize((512, 512), Image.Resampling.LANCZOS)
            im.save(ASSETS / "icon-ai.png", optimize=True)
            im.resize((96, 96), Image.Resampling.LANCZOS).save(ASSETS / "icon-ai-96.png", optimize=True)
            print("icon-ai ok")
        if banner_src.exists():
            im = Image.open(banner_src).convert("RGB")
            im = im.resize((1280, 720), Image.Resampling.LANCZOS)
            im.save(ASSETS / "banner-ai.png", optimize=True, quality=95)
            print("banner-ai ok")


def polish_user_icons() -> None:
    """Crop/resize user's best ChatGPT icons into clean 512 assets."""
    preview = ROOT / ".tmp-preview"
    if not preview.is_dir():
        return
    # img3 = rounded app icon, img4 = square promo, img5 = flat green
    mapping = {
        "img3.png": "icon-user-dark.png",
        "img4.png": "promo-user.png",
        "img5.png": "icon-user-flat.png",
    }
    for src_name, dst_name in mapping.items():
        src = preview / src_name
        if not src.exists():
            continue
        im = Image.open(src).convert("RGBA")
        # center square
        side = min(im.size)
        left = (im.width - side) // 2
        top = (im.height - side) // 2
        im = im.crop((left, top, left + side, top + side)).resize((512, 512), Image.Resampling.LANCZOS)
        # flatten on dark green for formats that need RGB
        bg = Image.new("RGB", (512, 512), (12, 20, 16))
        bg.paste(im, mask=im.split()[-1] if im.mode == "RGBA" else None)
        bg.save(ASSETS / dst_name, optimize=True)
        print(dst_name, "ok")


def main() -> None:
    make_icon()
    make_banner()
    make_feature_card(
        "gallery-hologram.png",
        "Hologram",
        [
            "TextDisplay above the mob",
            "Does not rewrite entity names",
            "Styles: bar · hearts · numeric",
        ],
        (55, 220, 120),
        0.8,
    )
    make_feature_card(
        "gallery-damage.png",
        "Damage numbers",
        [
            "Color tiers by damage amount",
            "Critical hits: larger scale",
            "Lightweight floating text",
        ],
        (255, 100, 100),
        0.55,
        fill_color=(255, 100, 100, 255),
    )
    make_feature_card(
        "gallery-lookat.png",
        "Look-at mode",
        [
            "Show HP while aiming",
            "Independent channel toggles",
            "Range & filter configurable",
        ],
        (80, 180, 255),
        1.0,
        fill_color=(80, 180, 255, 255),
    )
    make_og()
    maybe_import_ai()
    polish_user_icons()
    for p in sorted(ASSETS.glob("*")):
        print(f"  {p.name:24} {p.stat().st_size // 1024} KB")


if __name__ == "__main__":
    main()
