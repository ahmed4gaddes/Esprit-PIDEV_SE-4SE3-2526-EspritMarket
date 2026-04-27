import io
import os
from typing import Any

import numpy as np
from fastapi import FastAPI, File, Form, HTTPException, UploadFile
from fastapi.middleware.cors import CORSMiddleware
from PIL import Image
from ultralytics import YOLO


def _abs_path(*parts: str) -> str:
    here = os.path.dirname(os.path.abspath(__file__))
    return os.path.abspath(os.path.join(here, *parts))


def _resolve_model_path() -> str:
    env = os.environ.get("ESPRIT_OD_MODEL")
    if env:
        return os.path.abspath(env)
    # Ultralytics often writes under runs/detect/<run>/weights/; this repo also has detect/runs/train/
    candidates = (
        _abs_path("..", "..", "runs", "detect", "runs", "train", "weights", "best.pt"),
        _abs_path("..", "..", "runs", "detect", "train", "weights", "best.pt"),
        _abs_path("..", "..", "runs", "detect", "runs", "train_gpu", "weights", "best.pt"),
        _abs_path("..", "..", "ml", "runs", "detect", "train", "weights", "best.pt"),
    )
    for p in candidates:
        if os.path.isfile(p):
            return p
    return candidates[0]


MODEL_PATH = _resolve_model_path()

_model: YOLO | None = None


def _get_model() -> YOLO:
    global _model
    if _model is None:
        if not os.path.isfile(MODEL_PATH):
            raise FileNotFoundError(
                f"Missing weights at {MODEL_PATH}. Train (see ml/train_yolo.py) or set ESPRIT_OD_MODEL."
            )
        _model = YOLO(MODEL_PATH)
    return _model


app = FastAPI(title="EspritMarket Object Detection", version="0.1.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/health")
def health() -> dict[str, Any]:
    weights_ok = os.path.isfile(MODEL_PATH)
    return {"ok": True, "model_path": MODEL_PATH, "weights_present": weights_ok}


@app.post("/detect")
async def detect(
    image: UploadFile = File(...),
    conf: float = Form(0.35),
    iou: float = Form(0.7),
    max_det: int = Form(20),
) -> dict[str, Any]:
    try:
        mdl = _get_model()
    except FileNotFoundError as e:
        raise HTTPException(status_code=503, detail=str(e)) from e

    raw = await image.read()
    pil = Image.open(io.BytesIO(raw)).convert("RGB")
    arr = np.array(pil)  # HWC RGB
    results = mdl.predict(arr, conf=conf, iou=iou, max_det=max_det, verbose=False)
    r0 = results[0]

    h, w = arr.shape[0], arr.shape[1]
    detections: list[dict[str, Any]] = []

    if r0.boxes is not None and len(r0.boxes) > 0:
        for b in r0.boxes:
            cls_id = int(b.cls.item())
            label = mdl.names.get(cls_id, str(cls_id))
            score = float(b.conf.item())

            x1, y1, x2, y2 = [float(v) for v in b.xyxy[0].tolist()]
            detections.append(
                {
                    "label": label,
                    "confidence": score,
                    "bbox_xyxy": {"x1": x1, "y1": y1, "x2": x2, "y2": y2},
                    "bbox_xyxy_norm": {
                        "x1": x1 / w,
                        "y1": y1 / h,
                        "x2": x2 / w,
                        "y2": y2 / h,
                    },
                }
            )

    detections.sort(key=lambda d: d["confidence"], reverse=True)
    return {"width": w, "height": h, "detections": detections}

