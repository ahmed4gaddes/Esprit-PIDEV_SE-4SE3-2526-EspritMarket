# Run from repository root after `download_openimages_v7.py` completes successfully.
# Usage: .\.venv-py312\Scripts\Activate.ps1; .\ml\run_pipeline_after_download.ps1
# Optional: $env:DATASET_DIR = "ml\openimages\out\openimages_student20_yolo_v3"

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
Set-Location $root

$py = Join-Path $root ".venv-py312\\Scripts\\python.exe"
if (-not (Test-Path $py)) {
    Write-Error "Venv python not found: $py (expected venv at .venv-py312)."
}

$datasetDir = if ($env:DATASET_DIR) { $env:DATASET_DIR } else { "ml\openimages\out\openimages_student20_yolo" }
if (-not (Test-Path $datasetDir)) {
    Write-Error "Dataset folder not found: $datasetDir (set DATASET_DIR if you used OI_EXPORT_DIR)."
}

# Training defaults (override via env vars for quick tests)
$epochs = if ($env:YOLO_EPOCHS) { [int]$env:YOLO_EPOCHS } else { 60 }
$imgsz = if ($env:YOLO_IMGSZ) { [int]$env:YOLO_IMGSZ } else { 640 }
$device = if ($env:YOLO_DEVICE) { $env:YOLO_DEVICE } else { "0" }
$model = if ($env:YOLO_MODEL) { $env:YOLO_MODEL } else { "yolov8n.pt" }
$batch = if ($env:YOLO_BATCH) { [int]$env:YOLO_BATCH } else { 8 }
$workers = if ($env:YOLO_WORKERS) { [int]$env:YOLO_WORKERS } else { 0 }
$amp = if ($env:YOLO_AMP) { [int]$env:YOLO_AMP } else { 0 }

# YOLO export uses `val` for the validation split (not `validation`).
$split = "val"
Write-Host "Cleaning split=$split under $datasetDir ..."
& $py ml/openimages/clean_yolo_dataset.py `
    --dataset_dir $datasetDir `
    --split $split `
    --min_area 0.0004 `
    --write_cleaned

$yaml = Join-Path $datasetDir "dataset.yaml"
if (-not (Test-Path $yaml)) {
    Write-Error "Missing dataset.yaml at $yaml"
}

Write-Host ""
Write-Host "Training (adjust device: 0 for GPU, cpu otherwise; reduce epochs for a smoke test) ..."
Write-Host "  $py ml/train_yolo.py --data $yaml --model $model --epochs $epochs --imgsz $imgsz --device $device --batch $batch --workers $workers --amp $amp"
Write-Host ""
Write-Host "Starting training..."
& $py ml/train_yolo.py --data $yaml --model $model --epochs $epochs --imgsz $imgsz --device $device --batch $batch --workers $workers --amp $amp
Write-Host ""
Write-Host "If training fails because `train` is empty, point `train:` in dataset.yaml to the same images as `val` or export the train split and merge."
Write-Host "After training, run inference with ESPRIT_OD_MODEL=runs\detect\train\weights\best.pt"
