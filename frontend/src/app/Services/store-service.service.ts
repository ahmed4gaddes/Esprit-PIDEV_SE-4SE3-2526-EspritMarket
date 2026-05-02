import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Store } from '../models/store';
import { environment } from '../../environments/environment';
@Injectable({
  providedIn: 'root'
})
export class StoreServiceService {

   private apiUrl = `${environment.apiUrl}/Store`;

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

  addStore(store: Store): Observable<Store> {
    return this.http.post<Store>(`${this.apiUrl}/addstore`, store);
  }

  updateStore(store: Store, id:number): Observable<Store> {
    return this.http.put<Store>(`${this.apiUrl}/update`, store);
  }

  deleteStore(id: number): Observable<void> {
return this.http.delete<void>(`${this.apiUrl}/delete/${id}`);    

  }}
