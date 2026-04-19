## Local inference API (webcam testing)

This starts a small HTTP API to run the trained YOLO model (`best.pt`) on images sent from Angular.

### Install

From repo root (recommended):

```powershell
pip install -r ml/requirements-inference.txt
```

### Start

From repo root (uses package `ml.inference.app`):

```powershell
$env:ESPRIT_OD_MODEL = "runs\detect\train\weights\best.pt"
.\.venv-py312\Scripts\uvicorn.exe ml.inference.app:app --host 0.0.0.0 --port 8000
```

Or from this folder (`ml\inference`):

```powershell
$env:ESPRIT_OD_MODEL = "..\..\runs\detect\train\weights\best.pt"
python -m uvicorn app:app --host 127.0.0.1 --port 8000
```

Set `ESPRIT_OD_MODEL` to your `best.pt` path if it is not under `runs/detect/train/weights/`.

### Health check

```powershell
Invoke-RestMethod http://localhost:8000/health
```

See `ml/README.md` for the full new-machine workflow (dataset, train, inference).
