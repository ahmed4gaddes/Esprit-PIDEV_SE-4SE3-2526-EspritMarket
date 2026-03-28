import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProductAssessment } from '../models/product-assessment';

export interface ProductAssessmentAverageResponse {
  average: number;
  count: number;
}

@Injectable({ providedIn: 'root' })
export class ProductAssessmentService {

  private apiUrl = 'http://localhost:8081/api/assessments';

  constructor(private http: HttpClient) {}

  addAssessment(productId: number, assessment: ProductAssessment): Observable<ProductAssessment> {
    const params = new HttpParams().set('productId', productId);
    return this.http.post<ProductAssessment>(this.apiUrl, assessment, { params, withCredentials: true });
  }

  getByProduct(productId: number): Observable<ProductAssessment[]> {
    return this.http.get<ProductAssessment[]>(`${this.apiUrl}/product/${productId}`);
  }

  getAverageByProduct(productId: number): Observable<ProductAssessmentAverageResponse> {
    return this.http.get<ProductAssessmentAverageResponse>(`${this.apiUrl}/product/${productId}/average`);
  }

  getByUser(userId: number): Observable<ProductAssessment[]> {
    return this.http.get<ProductAssessment[]>(`${this.apiUrl}/user/${userId}`);
  }

  getById(id: number): Observable<ProductAssessment> {
    return this.http.get<ProductAssessment>(`${this.apiUrl}/${id}`);
  }

  update(id: number, assessment: ProductAssessment): Observable<ProductAssessment> {
    return this.http.put<ProductAssessment>(`${this.apiUrl}/${id}`, assessment);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
