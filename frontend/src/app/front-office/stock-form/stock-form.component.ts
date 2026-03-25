import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { StockService } from '../../Services/stock.service'; // ✅ adapter
import { Stock} from '../../models/stock';
import { ProductService } from '../../Services/product.service';               // ✅ adapter

@Component({
  selector: 'app-stock-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './stock-form.component.html',
  styleUrl: './stock-form.component.css'
})
export class StockFormComponent implements OnInit {

  stockForm!: FormGroup;
  stock!: Stock;
  id!: number;
  today: Date = new Date();

  // Valeurs de l'enum MovementType — à synchroniser avec le backend
  movementTypes: string[] = ['IN', 'OUT', 'ADJUSTMENT'];

  // Liste des produits pour le select
  products: any[] = [];

  constructor(
    private stockService: StockService,
    private productService: ProductService,
    private act: ActivatedRoute,
    private router: Router
  ) {
    this.stockForm = new FormGroup({
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
      this.stockService.getStockById(this.id).subscribe((result: Stock) => {
        this.stock = result;
        this.stockForm.patchValue({
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
  get quantity()  { return this.stockForm.get('quantity'); }
  get type()      { return this.stockForm.get('type'); }
  get productId() { return this.stockForm.get('productId'); }

  goBack(): void {
    this.router.navigate(['/admin/stock-movements']);
  }

 onSubmit(): void {
  console.log('🔥 onSubmit called');
  console.log('Form valid:', this.stockForm.valid);

  if (this.stockForm.valid) {
    const data = { ...this.stockForm.value };

    if (this.id) {
      // ✅ UPDATE
      this.stockService.updateStock(data, this.id).subscribe({
        next: (res) => {
          console.log('stock modifié:', res);
          alert('stock modifié avec succès !');
          this.router.navigateByUrl('/user/stock-movements');
        },
        error: (err) => {
          console.error('Erreur update:', err);
          alert(' Erreur: ' + err.status + ' - ' + err.message);
        }
      });
    } else {
      // ✅ ADD
      this.stockService.addStock(data).subscribe({
        next: (res) => {
          console.log('✅ Movement ajouté:', res);
          alert('✅ Movement added successfully!');
          this.stockForm.reset({ quantity: 0 });
        },
        error: (err) => {
          console.error('Erreur add:', err);
          alert('Erreur: ' + err.status + ' - ' + err.message);
        }
      });
    }

  } else {
    console.log('❌ Formulaire invalide');
    Object.values(this.stockForm.controls).forEach(c => c.markAsTouched());
  }
}
}