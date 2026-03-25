import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { StockService } from '../../Services/stock.service'; // ✅ adapter
import { Stock } from '../../models/stock';
@Component({
  selector: 'app-stock',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './stock.component.html',
  styleUrl: './stock.component.css'
})
export class StockComponent implements OnInit {

  stock: Stock[] = [];

  constructor(
    private stockService: StockService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadStock();
  }

  loadStock(): void {
    this.stockService.getAllStocks().subscribe({
      next: (data) => this.stock = data,
      error: (err) => console.error(' Error loading movements:', err)
    });
  }

  deleteMovement(id: number): void {
    if (confirm('Delete this stock?')) {
      this.stockService.deleteStock(id).subscribe({
        next: () => this.stock = this.stock.filter(m => m.id !== id),
        error: (err) => console.error(' Delete error:', err)
      });
    }
  }

  editMovement(id: number): void {
    this.router.navigate(['/admin/stock-movements/edit', id]);
  }

  getInCount(): number  { return this.stock.filter(m => m.type === 'IN').length; }
  getOutCount(): number { return this.stock.filter(m => m.type === 'OUT').length; }
}