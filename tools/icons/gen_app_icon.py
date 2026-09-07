"""앱 아이콘 한 벌을 **원본 하나에서** 굽는다.

    python3 tools/icons/gen_app_icon.py [원본.png]

원본은 `assets/brand/logo.png` — 검정 배경에 붉은 FS 마크다.
**원본 파일은 건드리지 않는다.** 크기 보정은 여기서(굽는 단계에서) 한다.
"""
import math
import os
import sys
from PIL import Image, ImageChops

ROOT = os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
SRC = sys.argv[1] if len(sys.argv) > 1 else f"{ROOT}/assets/brand/logo.png"

ANDROID = f"{ROOT}/androidApp/src/main/res"
IOS_ICON = f"{ROOT}/iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/AppIcon.png"

# 런처 아이콘 밀도별 크기 — 레거시(48dp) / 어댑티브 레이어(108dp)
DENSITIES = {"mdpi": 1, "hdpi": 1.5, "xhdpi": 2, "xxhdpi": 3, "xxxhdpi": 4}

# ── MyFIS 아이콘과 **같은 크기·같은 자리**로 맞춘다
#
# 같은 FS 마크인데 TeamFIS 원본이 1% 크다. 홈 화면에 두 앱을 나란히 뒀을 때
# 마크가 어긋나 보이면 안 되므로, 굽기 전에 MyFIS 기준으로 맞춘다.
#
# 기준은 **bbox**(알파 반값 기준 외곽 상자)다. 면적·무게중심으로 맞추면 안 된다 —
# 두 원본 다 마크 둘레에 옅은 글로우가 있는데 양이 서로 달라서(TeamFIS 쪽이 더 부드럽다)
# 무게중심이 10px 씩 밀린다. 실제로 그렇게 맞췄다가 오히려 더 어긋났다.
#
# 값은 MyFIS 원본(`MyFIS-App/assets/brand/logo.png`, 1254 캔버스, bbox 263,437~1015,837)을
# 캔버스 비율로 적은 것이다.
TARGET_X0, TARGET_Y0 = 0.2097289, 0.3484848   # bbox 좌상단
TARGET_W, TARGET_H = 0.5996810, 0.3189793     # bbox 폭·높이
ALPHA_HALF = 128                              # 알파 반값 — bbox 를 재는 문턱


def extract(src):
    """검정 배경 위 그림에서 **밝기를 알파로** 떼어낸다.

    배경이 순검정이라 픽셀값이 이미 `색 × 알파` 다. 알파로 나눠 **색을 되돌려야**
    가장자리가 거뭇하게 남지 않는다.
    """
    im = src.convert("RGB")
    r, g, b = im.split()
    alpha = ImageChops.lighter(ImageChops.lighter(r, g), b)
    px, ap = im.load(), alpha.load()
    out = Image.new("RGBA", im.size)
    op = out.load()
    for y in range(im.size[1]):
        for x in range(im.size[0]):
            a = ap[x, y]
            if a == 0:
                continue
            cr, cg, cb = px[x, y]
            k = 255 / a
            op[x, y] = (min(255, int(cr * k)), min(255, int(cg * k)), min(255, int(cb * k)), a)
    return out


def mark_bbox(im):
    """검정 배경 위 그림에서 마크의 외곽 상자. 밝기가 곧 알파다."""
    r, g, b = im.convert("RGB").split()
    a = ImageChops.lighter(ImageChops.lighter(r, g), b)
    return a.point(lambda v: 255 if v > ALPHA_HALF else 0).getbbox()


def normalize(im):
    """마크를 MyFIS 기준 크기·자리로 옮긴다.

    **가로세로비는 건드리지 않는다** — 로고를 늘이는 것이라 0.3% 라도 하면 안 된다.
    폭·높이 비의 기하평균으로 균일하게 줄이고 bbox 중심을 맞춘다.

    변환은 **검정 배경 위(= premultiplied)에서** 한다. 알파를 떼어낸 뒤에 늘이면
    투명 픽셀의 검정이 가장자리로 번져 테두리가 거뭇해진다.
    """
    W, H = im.size
    x0, y0, x1, y1 = mark_bbox(im)
    s = math.sqrt((TARGET_W * W / (x1 - x0)) * (TARGET_H * H / (y1 - y0)))
    ox = (TARGET_X0 + TARGET_W / 2) * W - s * (x0 + x1) / 2
    oy = (TARGET_Y0 + TARGET_H / 2) * H - s * (y0 + y1) / 2
    print("  크기 보정 %.5f 배, 이동 (%+.2f, %+.2f) px" % (s, ox, oy))
    # 아핀 데이터는 **출력 → 입력** 방향이라 역변환을 넣는다
    out = im.transform((W, H), Image.AFFINE, (1 / s, 0, -ox / s, 0, 1 / s, -oy / s),
                       resample=Image.BICUBIC)
    got, want = mark_bbox(out), (round(TARGET_X0 * W), round(TARGET_Y0 * H),
                                 round((TARGET_X0 + TARGET_W) * W), round((TARGET_Y0 + TARGET_H) * H))
    print("  맞춘 bbox %s / 기준 %s  (차이 %s)" % (got, want, tuple(g - w for g, w in zip(got, want))))
    return out


def circle_mask(size):
    # 4배로 그리고 줄여서 계단을 없앤다
    m = Image.new("L", (size * 4, size * 4), 0)
    from PIL import ImageDraw
    ImageDraw.Draw(m).ellipse((0, 0, size * 4 - 1, size * 4 - 1), fill=255)
    return m.resize((size, size), Image.LANCZOS)


def main():
    src = normalize(Image.open(SRC).convert("RGB"))
    mark = extract(src)

    # ── iOS — 1024 **불투명**. iOS 는 알파를 못 쓴다
    flat = Image.new("RGB", src.size, (0, 0, 0))
    flat.paste(mark, (0, 0), mark)
    os.makedirs(os.path.dirname(IOS_ICON), exist_ok=True)
    flat.resize((1024, 1024), Image.LANCZOS).save(IOS_ICON)

    # ── Android
    white = Image.new("RGBA", src.size, (255, 255, 255, 255))
    white.putalpha(mark.getchannel("A"))

    for name, scale in DENSITIES.items():
        d = f"{ANDROID}/mipmap-{name}"
        os.makedirs(d, exist_ok=True)
        legacy, layer = int(48 * scale), int(108 * scale)

        # 어댑티브 레이어 — 배경은 `@color/ic_launcher_background` 가 따로 깐다
        mark.resize((layer, layer), Image.LANCZOS).save(f"{d}/ic_launcher_foreground.png")
        white.resize((layer, layer), Image.LANCZOS).save(f"{d}/ic_launcher_monochrome.png")

        # 레거시(API 25 이하) — 배경까지 그려 넣는다
        sq = flat.resize((legacy, legacy), Image.LANCZOS).convert("RGBA")
        sq.save(f"{d}/ic_launcher.png")
        rnd = sq.copy()
        rnd.putalpha(circle_mask(legacy))
        rnd.save(f"{d}/ic_launcher_round.png")

    # ── 브랜드 원본 옆에 **배경 뺀 벌**도 같이 둔다 (마크만 필요한 곳용)
    mark.crop(mark.getbbox()).save(f"{ROOT}/assets/brand/logo-mark.png")

    box = src.convert("L").point(lambda v: 255 if v > 30 else 0).getbbox()
    print("구웠다 — 마크가 캔버스의 %.1f%% (MyFIS 기준으로 맞춤)" % ((box[2] - box[0]) / src.size[0] * 100))


main()
