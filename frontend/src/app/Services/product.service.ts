import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../models/product'; // ✅ adapter le chemin

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  // ✅ même port que StoreService
  private apiUrl = 'http://localhost:8081/Product';

  constructor(private http: HttpClient) {}

  // ── GET all ────────────────────────────────────
  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/getall`);
  }

  // ── GET by id ──────────────────────────────────
  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/get/${id}`);
  }

  // ── POST add ───────────────────────────────────
  addProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(`${this.apiUrl}/addprodcut`, product);
  }

  // ── PUT update ─────────────────────────────────
  updateProduct(product: Product, id: number): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/update/${id}`, product);
  }

  // ── DELETE ─────────────────────────────────────
  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`);
  }
}