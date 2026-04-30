import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../models/product';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private readonly COMPARE_KEYWORDS = [
    "meilleur", "optimal", "lequel", "quel", "choisir",
    "ahsen", "ahsni", "anho", "l'ahsni", "parmi",
    "bchah", "mieux", "top", "recommande", "compare",
    "winner", "best", "which", "suggest"
  ];

  private apiUrl = 'http://localhost:8081/Product';//crud produit 
  //private api = 'http://localhost:8081/api'; // ← URL séparée pour le modèle ML


  constructor(private http: HttpClient) { }

  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/getall`);
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/get/${id}`);
  }

  addProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(`${this.apiUrl}/addproduct`, product);
  }

  updateProduct(product: Product, id: number): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/update/${id}`, product);
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`);
  }

  searchProducts(name?: string, categoryId?: number, minPrice?: number, maxPrice?: number): Observable<Product[]> {
    let params = new HttpParams();
    if (name) params = params.set('name', name);
    if (categoryId) params = params.set('categoryId', categoryId.toString());
    if (minPrice != null) params = params.set('minPrice', minPrice.toString());
    if (maxPrice != null) params = params.set('maxPrice', maxPrice.toString());

    return this.http.get<Product[]>(`${this.apiUrl}/search`, { params });
  }

  getProductRecommendations(id: number): Observable<any> {
    return this.http.get<any>(`http://localhost:8081/api/recommendations/product/${id}`);
  }

  notifyMe(productId: number): Observable<string> {
    return this.http.post(`http://localhost:8081/StockAlert/notify-me/${productId}`, {}, { responseType: 'text' });
  }
  // model ai ML


}
