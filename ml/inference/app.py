import io
import os
from typing import Any

import numpy as np
from fastapi import FastAPI, File, Form, UploadFile
from fastapi.middleware.cors import CORSMiddleware
from PIL import Image
from ultralytics import YOLO


def _abs_path(*parts: str) -> str:
    here = os.path.dirname(os.path.abspath(__file__))
    return os.path.abspath(os.path.join(here, *parts))


MODEL_PATH = os.environ.get(
    "ESPRIT_OD_MODEL",
    _abs_path("..", "..", "runs", "detect", "train", "weights", "best.pt"),
)

app = FastAPI(title="EspritMarket Object Detection", version="0.1.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

model = YOLO(MODEL_PATH)


@app.get("/health")
def health() -> dict[str, Any]:
    return {"ok": True, "model_path": MODEL_PATH}


@app.post("/detect")
async def detect(
    image: UploadFile = File(...),
    conf: float = Form(0.35),
    iou: float = Form(0.7),
    max_det: int = Form(20),
) -> dict[str, Any]:
    raw = await image.read()
    pil = Image.open(io.BytesIO(raw)).convert("RGB")
    arr = np.array(pil)  # HWC RGB

    results = model.predict(arr, conf=conf, iou=iou, max_det=max_det, verbose=False)
    r0 = results[0]

    h, w = arr.shape[0], arr.shape[1]
    detections: list[dict[str, Any]] = []

    if r0.boxes is not None and len(r0.boxes) > 0:
        for b in r0.boxes:
            cls_id = int(b.cls.item())
            label = model.names.get(cls_id, str(cls_id))
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

