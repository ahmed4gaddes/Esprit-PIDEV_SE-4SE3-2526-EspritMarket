import argparse
import hashlib
import json
import os
from collections import Counter, defaultdict
from dataclasses import dataclass


"""
YOLO dataset cleaner + profiler (for Open Images subset exports).

What it does:
- Validates YOLO label files (format, ranges)
- Optionally filters tiny boxes (by normalized area threshold)
- Detects duplicate images by SHA256 (optional; can be slow)
- Produces before/after stats + a JSON report you can cite in your writeup

Run (example):
  python ml/openimages/clean_yolo_dataset.py ^
    --dataset_dir ml/openimages/out/openimages_student20_yolo_v3 ^
    --split val ^
    --min_area 0.0004 ^
    --write_cleaned
"""


@dataclass
class Box:
    cls: int
    xc: float
    yc: float
    w: float
    h: float

    @property
    def area(self) -> float:
        return self.w * self.h


def read_lines(path: str) -> list[str]:
    with open(path, "r", encoding="utf-8") as f:
        return [ln.strip() for ln in f.read().splitlines() if ln.strip()]


def sha256_file(path: str, chunk_size: int = 1024 * 1024) -> str:
    h = hashlib.sha256()
    with open(path, "rb") as f:
        while True:
            chunk = f.read(chunk_size)
            if not chunk:
                break
            h.update(chunk)
    return h.hexdigest()


def parse_yolo_line(line: str) -> Box | None:
    parts = line.split()
    if len(parts) != 5:
        return None
    try:
        cls = int(parts[0])
        xc, yc, w, h = map(float, parts[1:])
    except ValueError:
        return None
    return Box(cls=cls, xc=xc, yc=yc, w=w, h=h)


def is_valid_box(b: Box, num_classes: int) -> bool:
    if b.cls < 0 or b.cls >= num_classes:
        return False
    for v in (b.xc, b.yc, b.w, b.h):
        if not (0.0 <= v <= 1.0):
            return False
    if b.w <= 0.0 or b.h <= 0.0:
        return False
    return True


def load_class_names(dataset_yaml_path: str) -> dict[int, str]:
    # Minimal YAML parsing for `names:` mapping as written by FiftyOne
    lines = read_lines(dataset_yaml_path)
    names: dict[int, str] = {}
    in_names = False
    for ln in lines:
        if ln.startswith("names:"):
            in_names = True
            continue
        if in_names:
            if ln.startswith("path:") or ln.startswith("train:") or ln.startswith("val:") or ln.startswith("test:"):
                break
            # e.g. "  0: Backpack"
            if ":" in ln:
                left, right = ln.split(":", 1)
                try:
                    idx = int(left.strip())
                except ValueError:
                    continue
                names[idx] = right.strip()
    return names


def main() -> None:
    ap = argparse.ArgumentParser()
    ap.add_argument("--dataset_dir", required=True)
    ap.add_argument("--split", default="val")
    ap.add_argument("--min_area", type=float, default=0.0, help="Min normalized box area to keep")
    ap.add_argument("--dedupe_images", action="store_true")
    ap.add_argument("--write_cleaned", action="store_true", help="Write cleaned labels to labels_clean/<split>")
    args = ap.parse_args()

    dataset_dir = os.path.abspath(args.dataset_dir)
    dataset_yaml = os.path.join(dataset_dir, "dataset.yaml")
    class_names = load_class_names(dataset_yaml)
    num_classes = len(class_names)

    images_dir = os.path.join(dataset_dir, "images", args.split)
    labels_dir = os.path.join(dataset_dir, "labels", args.split)
    out_labels_dir = os.path.join(dataset_dir, "labels_clean", args.split)
    os.makedirs(out_labels_dir, exist_ok=True)

    image_files = []
    for fn in os.listdir(images_dir):
        if fn.lower().endswith((".jpg", ".jpeg", ".png", ".webp")):
            image_files.append(fn)
    image_files.sort()

    before = {
        "num_images": len(image_files),
        "num_label_files": 0,
        "num_boxes": 0,
        "class_counts": Counter(),
        "invalid_lines": 0,
        "invalid_boxes": 0,
        "tiny_filtered": 0,
        "missing_label_files": 0,
    }

    after = {
        "num_images": len(image_files),
        "num_label_files": 0,
        "num_boxes": 0,
        "class_counts": Counter(),
        "written_clean_labels": 0,
    }

    # Optional image de-duplication
    dup_groups: dict[str, list[str]] = defaultdict(list)
    if args.dedupe_images:
        for fn in image_files:
            fp = os.path.join(images_dir, fn)
            dup_groups[sha256_file(fp)].append(fn)

    for img_fn in image_files:
        stem = os.path.splitext(img_fn)[0]
        label_path = os.path.join(labels_dir, stem + ".txt")

        if not os.path.exists(label_path):
            before["missing_label_files"] += 1
            continue

        before["num_label_files"] += 1
        lines = read_lines(label_path)

        kept: list[Box] = []

        for ln in lines:
            b = parse_yolo_line(ln)
            if b is None:
                before["invalid_lines"] += 1
                continue

            before["num_boxes"] += 1
            before["class_counts"][b.cls] += 1

            if not is_valid_box(b, num_classes):
                before["invalid_boxes"] += 1
                continue

            if args.min_area > 0.0 and b.area < args.min_area:
                before["tiny_filtered"] += 1
                continue

            kept.append(b)

        if args.write_cleaned:
            out_path = os.path.join(out_labels_dir, stem + ".txt")
            with open(out_path, "w", encoding="utf-8") as f:
                for b in kept:
                    f.write(f"{b.cls} {b.xc:.6f} {b.yc:.6f} {b.w:.6f} {b.h:.6f}\n")
            after["written_clean_labels"] += 1

        after["num_label_files"] += 1
        after["num_boxes"] += len(kept)
        for b in kept:
            after["class_counts"][b.cls] += 1

    report = {
        "dataset_dir": dataset_dir,
        "split": args.split,
        "num_classes": num_classes,
        "classes": {str(k): v for k, v in class_names.items()},
        "params": {
            "min_area": args.min_area,
            "dedupe_images": args.dedupe_images,
            "write_cleaned": args.write_cleaned,
        },
        "before": {
            **{k: v for k, v in before.items() if k != "class_counts"},
            "class_counts": {str(k): int(v) for k, v in before["class_counts"].items()},
        },
        "after": {
            **{k: v for k, v in after.items() if k != "class_counts"},
            "class_counts": {str(k): int(v) for k, v in after["class_counts"].items()},
        },
        "duplicates": {
            "num_duplicate_groups": sum(1 for v in dup_groups.values() if len(v) > 1),
            "example_groups": [v for v in dup_groups.values() if len(v) > 1][:10],
        }
        if args.dedupe_images
        else None,
    }

    out_report_path = os.path.join(dataset_dir, f"cleaning_report_{args.split}.json")
    with open(out_report_path, "w", encoding="utf-8") as f:
        json.dump(report, f, indent=2)

    print("Wrote report:", out_report_path)
    print("Before boxes:", before["num_boxes"], "After boxes:", after["num_boxes"])
    print("Invalid lines:", before["invalid_lines"], "Invalid boxes:", before["invalid_boxes"])
    print("Tiny filtered:", before["tiny_filtered"], "Missing labels:", before["missing_label_files"])


if __name__ == "__main__":
    main()

