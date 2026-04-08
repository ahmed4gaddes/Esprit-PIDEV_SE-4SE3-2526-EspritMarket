import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { StockService } from '../../Services/stock.service';
import { Stock } from '../../models/stock';
import { ProductService } from '../../Services/product.service';

@Component({
  selector: 'app-stock-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './stock-form.component.html',
  styleUrl: './stock-form.component.css'
})
export class StockFormComponent implements OnInit {

  movementForm!: FormGroup;
  movement!: Stock;
  id!: number;
  today: Date = new Date();
  movementTypes: string[] = ['IN', 'OUT', 'ADJUSTMENT'];
  products: any[] = [];

  constructor(
    private movementService: StockService,
    private productService: ProductService,
    private act: ActivatedRoute,
    private router: Router
  ) {
    this.movementForm = new FormGroup({
      quantity:  new FormControl(0,    [Validators.required, Validators.min(1)]),
      type:      new FormControl(null, [Validators.required]),
      productId: new FormControl(null, [Validators.required])
    });

    this.id = this.act.snapshot.params['id'];

    if (this.id) {
      this.movementService.getStockById(this.id).subscribe((result: Stock) => {
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

  get quantity()  { return this.movementForm.get('quantity'); }
  get type()      { return this.movementForm.get('type'); }
  get productId() { return this.movementForm.get('productId'); }

  goBack(): void {
    this.router.navigate(['/seller/dashboard/stock']);
  }

  onSubmit(): void {
    if (this.movementForm.valid) {
      const data = { ...this.movementForm.value };

      if (this.id) {
        this.movementService.updateStock(data, this.id).subscribe({
          next: (res: any) => {
            console.log('✅ Movement modifié:', res);
            alert('✅ Movement modifié avec succès !');
            this.router.navigateByUrl('/seller/dashboard/stock');
          },
          error: (err: any) => {
            console.error('❌ Erreur update:', err);
            alert('❌ Erreur: ' + err.status + ' - ' + err.message);
          }
        });
      } else {
        this.movementService.addStock(data).subscribe({
          next: (res: any) => {
            console.log('✅ Movement ajouté:', res);
            alert('✅ Movement added successfully!');
            this.movementForm.reset({ quantity: 0 });
          },
          error: (err: any) => {
            console.error('❌ Erreur add:', err);
            alert('❌ Erreur: ' + err.status + ' - ' + err.message);
          }
        });
      }
    } else {
      Object.values(this.movementForm.controls).forEach(c => c.markAsTouched());
    }
  }
}