import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Stock } from '../models/stock';
@Injectable({
  providedIn: 'root'
})
export class StockService {

  private apiUrl = 'http://localhost:8081/Stock'; // ✅ adapter URL
  //localhost:8081/Stock
  //http://localhost:8081/Stock/addstock

  constructor(private http: HttpClient) {}

  getAllStocks(): Observable<Stock[]> {
    return this.http.get<Stock[]>(`${this.apiUrl}/getall`);
  }

  getStockById(id: number): Observable<Stock> {
    return this.http.get<Stock>(`${this.apiUrl}/get/${id}`);
  }

  addStock(movement: Stock): Observable<Stock> {
    return this.http.post<Stock>(`${this.apiUrl}/addstock`, movement);
  }

  updateStock(movement: Stock, id: number): Observable<Stock> {
    return this.http.put<Stock>(`${this.apiUrl}/update/${id}`, movement);
  }

  deleteStock(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`);
  }
}