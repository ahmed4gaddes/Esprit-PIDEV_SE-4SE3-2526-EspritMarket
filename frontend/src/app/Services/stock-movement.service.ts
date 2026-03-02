import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StockMovement } from '../models/stock-movement';
@Injectable({
  providedIn: 'root'
})
export class StockMovementService {

  private apiUrl = 'http://localhost:8081/Stock'; // ✅ adapter URL
  //localhost:8081/Stock
  //http://localhost:8081/Stock/addstock

  constructor(private http: HttpClient) {}

  getAllMovements(): Observable<StockMovement[]> {
    return this.http.get<StockMovement[]>(`${this.apiUrl}/getall`);
  }

  getMovementById(id: number): Observable<StockMovement> {
    return this.http.get<StockMovement>(`${this.apiUrl}/get/${id}`);
  }

  addMovement(movement: StockMovement): Observable<StockMovement> {
    return this.http.post<StockMovement>(`${this.apiUrl}/addstock`, movement);
  }

  updateMovement(movement: StockMovement, id: number): Observable<StockMovement> {
    return this.http.put<StockMovement>(`${this.apiUrl}/update/${id}`, movement);
  }

  deleteMovement(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`);
  }
}