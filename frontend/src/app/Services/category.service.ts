import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Category } from '../models/category'; // ✅ adapter chemin

@Injectable({
  providedIn: 'root'
})
export class CategoryService {

  private apiUrl = 'http://localhost:8081/category'; // ✅ adapter URL

  constructor(private http: HttpClient) {}

  getAllCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.apiUrl}/getall`);
  }

  getCategoriesByStore(storeId: number): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.apiUrl}/by-store/${storeId}`);
  }

  getCategoryById(id: number): Observable<Category> {
    return this.http.get<Category>(`${this.apiUrl}/get/${id}`);
  }

  addCategory(category: Category): Observable<Category> {
    return this.http.post<Category>(`${this.apiUrl}/addcategory`, category);
  }

  updateCategory(category: Category, id: number): Observable<Category> {
    return this.http.put<Category>(`${this.apiUrl}/updateCategory/${id}`, category);
  }

  deleteCategory(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`);
  }
}