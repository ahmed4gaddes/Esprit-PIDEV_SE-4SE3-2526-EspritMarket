import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Detection {
  label: string;
  confidence: number;
  bbox_xyxy: { x1: number; y1: number; x2: number; y2: number };
  bbox_xyxy_norm: { x1: number; y1: number; x2: number; y2: number };
}

export interface DetectResponse {
  width: number;
  height: number;
  detections: Detection[];
}

@Injectable({ providedIn: 'root' })
export class ObjectDetectionService {
  // FastAPI inference service
  private apiUrl = 'http://localhost:8000';

  constructor(private http: HttpClient) {}

  detectImage(blob: Blob, conf = 0.35): Observable<DetectResponse> {
    const fd = new FormData();
    fd.append('image', blob, 'frame.jpg');
    fd.append('conf', String(conf));
    return this.http.post<DetectResponse>(`${this.apiUrl}/detect`, fd);
  }
}

