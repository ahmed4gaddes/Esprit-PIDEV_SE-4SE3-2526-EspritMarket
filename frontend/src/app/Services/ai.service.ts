import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProductRecommendation {
  name: string;
  price: number;
  stock: number;
  stockLabel: string;
  description: string;
  categoryName: string;
  ramGb: number;
  score: number;
  profile?: string;
  brand?: string;
  budget?: number;
}

export interface ChatResponse {
  type: 'search' | 'comparison';
  profile?: string;
  category?: string;
  brand?: string;
  budget?: number;
  products?: ProductRecommendation[];
  // comparison
  best?: ProductRecommendation & { totalScore: number };
  ranked?: any[];
  reasons?: string[];
}

@Injectable({ providedIn: 'root' })
export class AiService {
    private baseUrl = 'http://localhost:5000'; // ✅ port 5000, pas de /store

  constructor(private http: HttpClient) {}

  recommend(query: string): Observable<ProductRecommendation[]> {
    return this.http.post<ProductRecommendation[]>(
      `${this.baseUrl}/recommend`,
      { query }
    );
  }

  chat(query: string): Observable<ChatResponse> {
    return this.http.post<ChatResponse>(
      `${this.baseUrl}/chat`,
      { query }
    );
  }
}