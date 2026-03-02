import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProductImage } from '../models/product-image';
@Injectable({
  providedIn: 'root'
})
export class ProductImageService {

  private apiUrl = 'http://localhost:8081/ProductImage'; // ✅ adapter URL
  //http://localhost:8081/ProductImage/addproductimage

  constructor(private http: HttpClient) {}

  getAllImages(): Observable<ProductImage[]> {
    return this.http.get<ProductImage[]>(`${this.apiUrl}/getAll`);
  }

  getImageById(id: number): Observable<ProductImage> {
    return this.http.get<ProductImage>(`${this.apiUrl}/get/${id}`);
  }

  addImage(image: ProductImage): Observable<ProductImage> {
    return this.http.post<ProductImage>(`${this.apiUrl}/addproductimage`, image);
  }

  updateImage(image: ProductImage, id: number): Observable<ProductImage> {
    return this.http.put<ProductImage>(`${this.apiUrl}/updateProductImage/${id}`, image);
  }

  deleteImage(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`);
  }
}