import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { StockMovementService } from '../../Services/stock-movement.service'; // ✅ adapter
import { StockMovement } from '../../models/stock-movement';
@Component({
  selector: 'app-stock-movements',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './stock-movements.component.html',
  styleUrl: './stock-movements.component.css'
})
export class StockMovementsComponent implements OnInit {

  movements: StockMovement[] = [];

  constructor(
    private movementService: StockMovementService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadMovements();
  }

  loadMovements(): void {
    this.movementService.getAllMovements().subscribe({
      next: (data) => this.movements = data,
      error: (err) => console.error('❌ Error loading movements:', err)
    });
  }

  deleteMovement(id: number): void {
    if (confirm('Delete this movement?')) {
      this.movementService.deleteMovement(id).subscribe({
        next: () => this.movements = this.movements.filter(m => m.id !== id),
        error: (err) => console.error('❌ Delete error:', err)
      });
    }
  }

  editMovement(id: number): void {
    this.router.navigate(['/admin/stock-movements/edit', id]);
  }

  getInCount(): number  { return this.movements.filter(m => m.type === 'IN').length; }
  getOutCount(): number { return this.movements.filter(m => m.type === 'OUT').length; }
}