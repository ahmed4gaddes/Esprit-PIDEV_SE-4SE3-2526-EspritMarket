import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { StockMovementService } from '../../Services/stock-movement.service'; // aligned import
import { StockMovement } from '../../models/stock-movement';
import { ProductService } from '../../Services/product.service';               // aligned import

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

  // MovementType enum values - keep in sync with backend
  movementTypes: string[] = ['IN', 'OUT', 'ADJUSTMENT'];

  // Product list used by the select
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
      // date handled by @PrePersist on backend side
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
    this.router.navigate(['/seller/dashboard/stock']);
  }

 onSubmit(): void {
  console.log('🔥 onSubmit called');
  console.log('Form valid:', this.movementForm.valid);

  if (this.movementForm.valid) {
    const data = { ...this.movementForm.value };

    if (this.id) {
      // ✅ UPDATE
      this.movementService.updateMovement(data, this.id).subscribe({
        next: (res) => {
          console.log('✅ Movement modifié:', res);
          alert('✅ Movement modifié avec succès !');
          this.router.navigateByUrl('/seller/dashboard/stock');
        },
        error: (err) => {
          console.error('❌ Erreur update:', err);
          alert('❌ Erreur: ' + err.status + ' - ' + err.message);
        }
      });
    } else {
      // ✅ ADD
      this.movementService.addMovement(data).subscribe({
        next: (res) => {
          console.log('✅ Movement ajouté:', res);
          alert('✅ Movement added successfully!');
          this.movementForm.reset({ quantity: 0 });
        },
        error: (err) => {
          console.error('❌ Erreur add:', err);
          alert('❌ Erreur: ' + err.status + ' - ' + err.message);
        }
      });
    }

  } else {
    console.log('❌ Formulaire invalide');
    Object.values(this.movementForm.controls).forEach(c => c.markAsTouched());
  }
}
}