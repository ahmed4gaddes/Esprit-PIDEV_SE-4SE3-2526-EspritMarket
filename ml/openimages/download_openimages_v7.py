import os

import fiftyone as fo
import fiftyone.zoo as foz


"""
Downloads an Open Images V7 *subset* (detections only) and exports to YOLO format.

Run:
  python ml/openimages/download_openimages_v7.py

Then you should get:
  ml/openimages/out/openimages_student20_yolo_v3/
"""


# 20 "everyday student life" classes.
# NOTE: Open Images class names must match exactly. If this script errors on a class
# name, we'll adjust it based on the error message and rerun.
CLASSES = [
    "Backpack",
    "Handbag",
    "Book",
    "Pen",
    "Ring binder",
    "Calculator",
    "Pencil case",
    "Pencil sharpener",
    "Mobile phone",
    "Laptop",
    "Computer keyboard",
    "Computer mouse",
    "Headphones",
    "Power plugs and sockets",
    "Coffee cup",
    "Bottle",
    "Apple",
    "Banana",
    "Bicycle",
    "Umbrella",
]

def _parse_splits(raw: str) -> list[str]:
    # Accept: "validation", "train,validation", "train validation", etc.
    parts = [p.strip() for p in raw.replace(";", ",").replace(" ", ",").split(",")]
    splits = [p for p in parts if p]
    if not splits:
        return ["validation"]
    # Keep stable order but remove dupes
    out: list[str] = []
    seen: set[str] = set()
    for s in splits:
        if s not in seen:
            out.append(s)
            seen.add(s)
    return out


SPLITS = _parse_splits(os.environ.get("OI_SPLITS", os.environ.get("OI_SPLIT", "validation")))
MAX_SAMPLES_PER_SPLIT = int(os.environ.get("OI_MAX_SAMPLES", "3000"))

EXPORT_DIR = os.path.abspath(
    os.environ.get(
        "OI_EXPORT_DIR",
        os.path.join("ml", "openimages", "out", "openimages_student20_yolo"),
    )
)


def main() -> None:
    print("Loading Open Images V7 subset...")
    print("  splits     =", ", ".join(SPLITS))
    print("  max_samples_per_split =", MAX_SAMPLES_PER_SPLIT)
    print("  classes    =", len(CLASSES))
    print("  export_dir =", EXPORT_DIR)

    total = 0
    for split in SPLITS:
        dataset_name = f"open-images-v7-student20-{split}-{MAX_SAMPLES_PER_SPLIT}"

        print("")
        print("Downloading split:", split)
        dataset = foz.load_zoo_dataset(
            "open-images-v7",
            split=split,
            label_types=["detections"],
            classes=CLASSES,
            only_matching=True,
            max_samples=MAX_SAMPLES_PER_SPLIT,
            dataset_name=dataset_name,
        )
        print("Downloaded samples:", len(dataset))
        total += len(dataset)

        print("Exporting to YOLO format (merged) ...")
        dataset.export(
            export_dir=EXPORT_DIR,
            dataset_type=fo.types.YOLOv5Dataset,
            label_field="ground_truth",
            classes=CLASSES,  # force stable class list even if some have zero instances
        )

    print("Done.")
    print("Total downloaded samples (sum of splits):", total)
    print("YOLO export directory:", EXPORT_DIR)


if __name__ == "__main__":
    main()

