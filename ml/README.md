# ML (object detection)

Repeatable setup on a new machine after cloning (from **repo root**): create a venv, then install either the **training** stack or **inference-only** deps.

## Prerequisites

- Python 3.10–3.12 (3.12 matches typical CUDA wheel choices).
- NVIDIA driver + CUDA PyTorch if you train or run GPU inference: install `torch` / `torchvision` / `torchaudio` per [PyTorch](https://pytorch.org), then install the rest.

### Venv and dependencies

```powershell
cd path\to\cloned\repo
python -m venv .venv-py312
.\.venv-py312\Scripts\Activate.ps1
python -m pip install --upgrade pip
```

**Training + download + clean** (includes FiftyOne, Ultralytics, Torch):

```powershell
pip install -r ml/requirements-train.txt
```

**Inference API only** (lighter; no FiftyOne):

```powershell
pip install -r ml/requirements-inference.txt
```

For inference you do not need FiftyOne on the server; for re-downloading the dataset you do.

---

## 1. Download Open Images → YOLO

Defaults in `download_openimages_v7.py`: validation split, 3000 samples, export under  
`ml/openimages/out/openimages_student20_yolo` (unless you override env vars).

From repo root:

```powershell
.\.venv-py312\Scripts\python.exe ml/openimages/download_openimages_v7.py
```

Optional environment variables (PowerShell):

```powershell
$env:OI_SPLITS = "train,validation"  # or "validation" only
$env:OI_MAX_SAMPLES = "6000"         # per split
$env:OI_EXPORT_DIR = "$(Resolve-Path .)\ml\openimages\out\openimages_student20_yolo_v3"
.\.venv-py312\Scripts\python.exe ml/openimages/download_openimages_v7.py
```

First run can take a long time and use a lot of disk space.

---

## 2. Clean the YOLO export

Point `--dataset_dir` at the folder that contains `dataset.yaml` and `images/<split>` / `labels/<split>`.

Example (validation split, tiny boxes removed, write cleaned labels):

```powershell
.\.venv-py312\Scripts\python.exe ml/openimages/clean_yolo_dataset.py `
  --dataset_dir ml/openimages/out/openimages_student20_yolo_v3 `
  --split val `
  --min_area 0.0004 `
  --write_cleaned
```

Repeat for `train` if you exported train data and have `images/train` + `labels/train`.

Reports: `cleaning_report_val.json` / `cleaning_report_train.json` in that dataset folder.

If you use `labels_clean` for training, your `dataset.yaml` must reference those paths (as when combining splits).

---

## 3. Training (Ultralytics)

From repo root, with a valid `dataset.yaml`:

```powershell
yolo detect train data=ml/openimages/out/YOUR_DATASET/dataset.yaml model=yolov8n.pt epochs=60 imgsz=640 device=0
```

`device=0`: first GPU; use `device=cpu` if no GPU.  
Weights default to `runs/detect/train/weights/best.pt`.

---

## 4. Inference service (after training)

```powershell
pip install -r ml/requirements-inference.txt
cd ml\inference
$env:ESPRIT_OD_MODEL = "..\..\runs\detect\train\weights\best.pt"
python -m uvicorn app:app --host 127.0.0.1 --port 8000
```

Alternatively from **repo root** (module path):

```powershell
$env:ESPRIT_OD_MODEL = "runs\detect\train\weights\best.pt"
.\.venv-py312\Scripts\uvicorn.exe ml.inference.app:app --host 127.0.0.1 --port 8000
```

- Code: `ml/inference/` (FastAPI + Ultralytics).
- Place **`best.pt`** under `runs/detect/train/weights/best.pt`, or set **`ESPRIT_OD_MODEL`** to the full path of `best.pt`.

`runs/` is gitignored; do not commit large weights unless you use **Git LFS**.

---

## Quick checklist

| Step | Command / action |
|------|------------------|
| Venv + deps | `python -m venv .venv-py312`, then `pip install -r ml/requirements-train.txt` or `ml/requirements-inference.txt` |
| Download | `python ml/openimages/download_openimages_v7.py` (+ env vars if needed) |
| Clean | `python ml/openimages/clean_yolo_dataset.py --dataset_dir … --split val` |
| Train | `yolo detect train data=…/dataset.yaml model=yolov8n.pt …` |
| Run API | uvicorn in `ml/inference` with `ESPRIT_OD_MODEL` |

**If you only need to run the app:** copy `best.pt` (or use a shared drive) and skip download/clean/train; install `ml/requirements-inference.txt` only.

---

## Dataset & training scripts

- `ml/openimages/` — download / clean / YOLO export scripts.
- Downloaded datasets under `ml/openimages/out/` are gitignored.
