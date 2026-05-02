import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProductImage } from '../models/product-image';
import { environment } from '../../environments/environment';
@Injectable({
  providedIn: 'root'
})
export class ProductImageService {

  private apiUrl = `${environment.apiUrl}/ProductImage`;

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

  /** Body shape matches backend ProductImageDTO (field name `order`, not imageOrder). */
  addImageForProduct(productId: number, url: string, altText?: string): Observable<ProductImage> {
    return this.http.post<ProductImage>(`${this.apiUrl}/addproductimage`, {
      productId,
      url,
      altText: altText ?? '',
      order: 0
    });
  }

  updateImage(image: ProductImage, id: number): Observable<ProductImage> {
    return this.http.put<ProductImage>(`${this.apiUrl}/updateProductImage/${id}`, image);
  }

  deleteImage(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`);
  }
}