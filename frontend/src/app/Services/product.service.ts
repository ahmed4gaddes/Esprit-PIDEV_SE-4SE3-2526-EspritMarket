import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../models/product';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  // URL matches @RequestMapping("Product") in RestControllerProduct.java
  private apiUrl = 'http://localhost:8081/Product';

  constructor(private http: HttpClient) { }

  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/getall`);
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/get/${id}`);
  }

  // ⚠️ "addprodcut" est une faute dans le backend aussi - garder tel quel !
  addProduct(product: Product): Observable<Product> {
    return this.http.post<Product>(`${this.apiUrl}/addprodcut`, product);
  }

  updateProduct(product: Product, id: number): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/update/${id}`, product);
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`);
  }
}
