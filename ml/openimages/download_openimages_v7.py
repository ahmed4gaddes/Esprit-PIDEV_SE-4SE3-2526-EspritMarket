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

SPLIT = os.environ.get("OI_SPLIT", "validation")  # validation | train | test
MAX_SAMPLES = int(os.environ.get("OI_MAX_SAMPLES", "3000"))

EXPORT_DIR = os.path.abspath(
    os.environ.get(
        "OI_EXPORT_DIR",
        os.path.join("ml", "openimages", "out", "openimages_student20_yolo"),
    )
)


def main() -> None:
    dataset_name = f"open-images-v7-student20-{SPLIT}-{MAX_SAMPLES}"

    print("Loading Open Images V7 subset...")
    print("  split      =", SPLIT)
    print("  max_samples=", MAX_SAMPLES)
    print("  classes    =", len(CLASSES))

    dataset = foz.load_zoo_dataset(
        "open-images-v7",
        split=SPLIT,
        label_types=["detections"],
        classes=CLASSES,
        only_matching=True,
        max_samples=MAX_SAMPLES,
        dataset_name=dataset_name,
    )

    print("Downloaded samples:", len(dataset))
    print("Exporting to YOLO format...")

    dataset.export(
        export_dir=EXPORT_DIR,
        dataset_type=fo.types.YOLOv5Dataset,
        label_field="ground_truth",
        classes=CLASSES,  # force stable class list even if some have zero instances
    )

    print("Done.")
    print("YOLO export directory:", EXPORT_DIR)


if __name__ == "__main__":
    main()

