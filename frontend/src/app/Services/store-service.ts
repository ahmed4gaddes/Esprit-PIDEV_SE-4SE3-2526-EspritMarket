import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Store } from '../models/store';
@Injectable({
  providedIn: 'root'
})
export class StoreService {

   private apiUrl = 'http://localhost:8081/Store';

  constructor(private http: HttpClient) {}

  getAllStores(): Observable<Store[]> {
    return this.http.get<Store[]>(`${this.apiUrl}/getall`);
  }

  getMyStores(): Observable<Store[]> {
    return this.http.get<Store[]>(`${this.apiUrl}/my-stores`);
  }

  getStoreById(id: number): Observable<Store> {
    return this.http.get<Store>(`${this.apiUrl}/get/${id}`);
  }

  getStoreAnalytics(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}/analytics`);
  }

  addStore(store: Store): Observable<Store> {
    return this.http.post<Store>(`${this.apiUrl}/addstore`, store);
  }

  updateStore(store: Store, id:number): Observable<Store> {
    return this.http.put<Store>(`${this.apiUrl}/update`, store);
  }

  deleteStore(id: number): Observable<void> {
return this.http.delete<void>(`${this.apiUrl}/delete/${id}`);    

  }}
