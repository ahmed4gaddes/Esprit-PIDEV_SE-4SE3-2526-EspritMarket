import argparse

from ultralytics import YOLO


def main() -> None:
    ap = argparse.ArgumentParser()
    ap.add_argument("--data", required=True, help="Path to dataset.yaml")
    ap.add_argument("--model", default="yolov8n.pt")
    ap.add_argument("--epochs", type=int, default=60)
    ap.add_argument("--imgsz", type=int, default=640)
    ap.add_argument("--device", default="0")  # "0" for first GPU, "cpu" for CPU
    ap.add_argument("--batch", type=int, default=8)
    ap.add_argument("--workers", type=int, default=0)
    ap.add_argument("--amp", type=int, default=0, help="1 to enable AMP, 0 to disable")
    ap.add_argument("--val", type=int, default=0, help="1 to run validation, 0 to skip (saves VRAM)")
    ap.add_argument("--plots", type=int, default=0, help="1 to save plots, 0 to disable (saves time/mem)")
    ap.add_argument("--cache", type=int, default=0, help="1 to cache images in RAM, 0 to disable")
    ap.add_argument("--mosaic", type=float, default=0.0, help="Mosaic augmentation probability")
    ap.add_argument("--close_mosaic", type=int, default=0, help="Disable mosaic N epochs before end")
    ap.add_argument("--freeze", type=int, default=0, help="Freeze first N layers to reduce VRAM (0 disables)")
    args = ap.parse_args()

    model = YOLO(args.model)
    model.train(
        data=args.data,
        epochs=args.epochs,
        imgsz=args.imgsz,
        device=args.device,
        batch=args.batch,
        workers=args.workers,
        amp=bool(args.amp),
        val=bool(args.val),
        plots=bool(args.plots),
        cache=bool(args.cache),
        mosaic=args.mosaic,
        close_mosaic=args.close_mosaic,
        freeze=args.freeze if args.freeze > 0 else None,
    )


if __name__ == "__main__":
    main()

