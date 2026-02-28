import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { StockMovementService } from '../../Services/stock-movement.service'; // ✅ adapter
import { StockMovement } from '../../models/stock-movement';
import { ProductService } from '../../Services/product.service';               // ✅ adapter

@Component({
  selector: 'app-stock-movement-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './stock-movement-form.component.html',
  styleUrl: './stock-movement-form.component.css'
})
export class StockMovementFormComponent implements OnInit {

  movementForm!: FormGroup;
  movement!: StockMovement;
  id!: number;
  today: Date = new Date();

  // Valeurs de l'enum MovementType — à synchroniser avec le backend
  movementTypes: string[] = ['IN', 'OUT', 'ADJUSTMENT'];

  // Liste des produits pour le select
  products: any[] = [];

  constructor(
    private movementService: StockMovementService,
    private productService: ProductService,
    private act: ActivatedRoute,
    private router: Router
  ) {
    this.movementForm = new FormGroup({
      quantity: new FormControl(0, [
        Validators.required,
        Validators.min(1)
      ]),
      type: new FormControl(null, [
        Validators.required
      ]),
      productId: new FormControl(null, [
        Validators.required
      ])
      // date géré par @PrePersist côté backend
    });

    this.id = this.act.snapshot.params['id'];

    if (this.id) {
      this.movementService.getMovementById(this.id).subscribe((result: StockMovement) => {
        this.movement = result;
        this.movementForm.patchValue({
          quantity:  result.quantity,
          type:      result.type,
          productId: result.productId ?? (result as any).product?.id
        });
        this.today = result.date ? new Date(result.date) : new Date();
      });
    }
  }

  ngOnInit(): void {
    this.productService.getAllProducts().subscribe((data: any[]) => {
      this.products = data;
    });
  }

  // ── Getters ────────────────────────────────────
  get quantity()  { return this.movementForm.get('quantity'); }
  get type()      { return this.movementForm.get('type'); }
  get productId() { return this.movementForm.get('productId'); }

  goBack(): void {
    this.router.navigate(['/admin/stock-movements']);
  }

  onSubmit(): void {
    if (this.movementForm.valid) {
      if (this.id) {
        // UPDATE
        this.movementService.updateMovement(this.movementForm.value, this.id).subscribe(() => {
          this.router.navigateByUrl('/admin/stock-movements');
        });
      } else {
        // ADD
        const data = { ...this.movementForm.value };
        this.movementService.addMovement(data).subscribe(() => {
          alert('✅ Movement added successfully!');
          this.movementForm.reset({ quantity: 0 });
        });
      }
    }
  }
}