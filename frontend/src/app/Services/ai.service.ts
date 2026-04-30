import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProductRecommendation {
  id: number;
  name: string;
  price: number;
  stock: number;
  stockLabel: string;
  description: string;
  categoryName: string;
  ramGb: number;
  score: number;
  imageUrl?: string;
  profile?: string;
  brand?: string;
  budget?: number;
}

// ✅ AJOUT
export interface BundleProduct {
  productId:   number;
  name:        string;
  price:       number;
  stock:       number;
  description: string;
  frequency:   number;
}

export interface ChatResponse {
  type: 'search' | 'comparison' | 'not_found' | 'brand_not_found' | 'bundle' | 'budget_too_low'; // ✅ bundle
  message?:     string;
  options?:    string[];       // ✅ AJOUT pour clarification
  warning?:     string;
  profile?:     string;
  category?:    string;
  brand?:       string;
  budget?:      number;
  filters?:     string;       // ✅ AJOUT
  productName?: string;       // ✅ AJOUT
  bundles?:     BundleProduct[]; // ✅ AJOUT
  products?:    ProductRecommendation[];
  best?:        ProductRecommendation & { totalScore: number };
  ranked?:      any[];
  reasons?:     string[];
}

@Injectable({ providedIn: 'root' })
export class AiService {
  private baseUrl = 'http://localhost:5000';

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