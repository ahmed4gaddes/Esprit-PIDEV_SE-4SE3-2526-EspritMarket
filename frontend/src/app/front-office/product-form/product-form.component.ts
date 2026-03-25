import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../Services/product.service';   // ✅ adapter
import { Product } from '../../models/product';                     // ✅ adapter
import { StoreService } from '../../Services/store-service'; // ✅ adapter
import { CategoryService } from '../../Services/category.service'; // ✅ adapter

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './product-form.component.html',
  styleUrl: './product-form.component.css'
})
export class ProductFormComponent implements OnInit {

  productForm!: FormGroup;
  product!: Product;
  id!: number;
  today: Date = new Date();
 previewUrl: string = '';
  // Listes pour les selects
  stores: any[]     = [];
  categories: any[] = [];

  constructor(
    private productService: ProductService,
    private storeService: StoreService,
    private categoryService: CategoryService,
    private act: ActivatedRoute,
    private router: Router
  ) {
    this.productForm = new FormGroup({
      name: new FormControl('', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(100)
      ]),
      description: new FormControl(''),
      price: new FormControl(0, [
        Validators.required,
        Validators.min(0)
      ]),
      stock: new FormControl(0, [
        Validators.required,
        Validators.min(0)
      ]),
      active:     new FormControl(true),
      storeId:    new FormControl(null, Validators.required),
      categoryId: new FormControl(null, Validators.required)
      // createdAt géré par @PrePersist côté backend → pas dans le form
    });

    this.id = this.act.snapshot.params['id'];

    if (this.id) {
      this.productService.getProductById(this.id).subscribe((result: Product) => {
        this.product = result;
        this.productForm.patchValue({
          name:        result.name,
          description: result.description,
          price:       result.price,
          stock:       result.stock,
          active:      result.active,
          storeId:     result.storeId    ?? (result as any).store?.id,
          categoryId:  result.categoryId ?? (result as any).category?.id
        });
        this.today = result.createdAt ? new Date(result.createdAt) : new Date();
      });
    }
  }

  
 //Charger stores et catégories pour les selects
    ngOnInit(): void {
  this.storeService.getAllStores().subscribe({
    next: (data) => {
      this.stores = data;
      console.log('✅ Stores:', data);
    },
    error: (err) => console.error('❌ Stores error:', err.status, err.url)
  });

  this.categoryService.getAllCategories().subscribe({
    next: (data) => {
      this.categories = data;
      console.log('✅ Categories:', data);
    },
    error: (err) => console.error('❌ Categories error:', err.status, err.url)
  });
}
  

  // ── Getters ────────────────────────────────────
  get name()        { return this.productForm.get('name'); }
  get description() { return this.productForm.get('description'); }
  get price()       { return this.productForm.get('price'); }
  get stock()       { return this.productForm.get('stock'); }
  get storeId()     { return this.productForm.get('storeId'); }
  get categoryId()  { return this.productForm.get('categoryId'); }

  // ── Navigation ─────────────────────────────────
  goBack(): void {
    this.router.navigate(['/admin/products']);
  }

  // ── Submit Add / Update ────────────────────────
  onSubmit(): void {
  console.log('🔥 onSubmit called');
  console.log('Form valid:', this.productForm.valid);
  console.log('Form value:', this.productForm.value);

  if (this.productForm.valid) {
    const productData = { ...this.productForm.value };
    console.log('📦 Sending:', productData);

    if (this.id) {
      // ✅ UPDATE
      this.productService.updateProduct(productData, this.id).subscribe({
        next: (res) => {
          console.log('✅ Produit modifié:', res);
          alert('✅ Produit modifié avec succès !');
          this.router.navigateByUrl('/user/products');
        },
        error: (err) => {
          console.error('❌ Erreur update:', err);
          alert('❌ Erreur: ' + err.status + ' - ' + err.message);
        }
      });
    } else {
      // ✅ ADD
      this.productService.addProduct(productData).subscribe({
        next: (res) => {
          console.log('✅ Produit ajouté:', res);
          alert('✅ Produit ajouté avec succès !');
          this.productForm.reset({ active: true, price: 0, stock: 0 });
        },
        error: (err) => {
          console.error('❌ Erreur add:', err);
          alert('❌ Erreur: ' + err.status + ' - ' + err.message);
        }
      });
    }

  } else {
    console.log('❌ Formulaire invalide');
    Object.values(this.productForm.controls).forEach(c => c.markAsTouched());
  }
}
onFileSelected(event: Event): void {
  const input = event.target as HTMLInputElement;
  if (input.files && input.files[0]) {
    const file = input.files[0];
    const objectUrl = URL.createObjectURL(file);
    this.previewUrl = objectUrl;
    this.productForm.patchValue({ url: objectUrl });
  }
}
}