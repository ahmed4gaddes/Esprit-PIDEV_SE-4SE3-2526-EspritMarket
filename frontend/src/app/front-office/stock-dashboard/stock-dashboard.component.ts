import { Component, OnInit } from '@angular/core';
import { StockService } from '../../Services/stock.service';
@Component({
  selector: 'app-stock-dashboard',
  templateUrl: './stock-dashboard.component.html'
})
export class StockDashboardComponent implements OnInit {

  outOfStockProducts: any[] = [];
  lowStockProducts: any[] = [];
  successMessage = '';
  errorMessage = '';

  constructor(private stockService: StockService) { }

  ngOnInit(): void {
    this.loadAlerts();
  }

  loadAlerts(): void {
    this.stockService.getOutOfStockProducts().subscribe((data: any[]) => {
      this.outOfStockProducts = data;
    });

    this.stockService.getLowStockProducts().subscribe((data: any[]) => {
      this.lowStockProducts = data;
    });
  }

  decrement(id: number, quantity: number): void {
    this.stockService.decrementStock(id, quantity).subscribe({
      next: () => {
        this.successMessage = 'Stock décrémenté ✅';
        this.loadAlerts(); // refresh
      },
      error: (err: any) => {
        this.errorMessage = err.error || 'Stock insuffisant ❌';
      }
    });
  }

  increment(id: number, quantity: number): void {
    this.stockService.incrementStock(id, quantity).subscribe({
      next: () => {
        this.successMessage = 'Stock incrémenté ✅';
        this.loadAlerts();
      },
      error: (err: any) => {
        this.errorMessage = 'Erreur ❌';
      }
    });
  }
}