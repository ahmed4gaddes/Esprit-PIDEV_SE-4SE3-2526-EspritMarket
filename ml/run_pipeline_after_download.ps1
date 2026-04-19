# Run from repository root after `download_openimages_v7.py` completes successfully.
# Usage: .\.venv-py312\Scripts\Activate.ps1; .\ml\run_pipeline_after_download.ps1
# Optional: $env:DATASET_DIR = "ml\openimages\out\openimages_student20_yolo_v3"

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
Set-Location $root

$datasetDir = if ($env:DATASET_DIR) { $env:DATASET_DIR } else { "ml\openimages\out\openimages_student20_yolo" }
if (-not (Test-Path $datasetDir)) {
    Write-Error "Dataset folder not found: $datasetDir (set DATASET_DIR if you used OI_EXPORT_DIR)."
}

# YOLO export uses `val` for the validation split (not `validation`).
$split = "val"
Write-Host "Cleaning split=$split under $datasetDir ..."
python ml/openimages/clean_yolo_dataset.py `
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
Write-Host "  yolo detect train data=$yaml model=yolov8n.pt epochs=100 imgsz=640 device=0"
Write-Host ""
Write-Host "If training fails because `train` is empty, point `train:` in dataset.yaml to the same images as `val` or export the train split and merge."
Write-Host "After training, run inference with ESPRIT_OD_MODEL=runs\detect\train\weights\best.pt"
