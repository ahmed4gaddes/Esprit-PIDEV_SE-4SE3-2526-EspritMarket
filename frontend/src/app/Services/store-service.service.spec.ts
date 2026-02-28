// src/app/services/store.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Store } from '../models/store';

@Injectable({
  providedIn: 'root'
})
export class StoreService {

  private apiUrl = 'http://localhost:8080/Store'; // ✅ URL Backend

  constructor(private http: HttpClient) {}

  // ✅ GET ALL
  getAllStores(): Observable<Store[]> {
    return this.http.get<Store[]>(`${this.apiUrl}/getall`);
  }

  // ✅ GET BY ID
  getStoreById(id: number): Observable<Store> {
    return this.http.get<Store>(`${this.apiUrl}/get/${id}`);
  }

  // ✅ POST
  addStore(store: Store): Observable<Store> {
    return this.http.post<Store>(`${this.apiUrl}/addstore`, store);
  }

  // ✅ PUT
  updateStore(store: Store): Observable<Store> {
    return this.http.put<Store>(`${this.apiUrl}/update`, store);
  }

  // ✅ DELETE
  deleteStore(store: Store): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete`, { body: store });
  }
}