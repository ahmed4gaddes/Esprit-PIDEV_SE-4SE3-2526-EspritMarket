import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../Services/product.service';
import { Product } from '../../models/product';
import { StoreService } from '../../Services/store-service';
import { CategoryService } from '../../Services/category.service';

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './product-form.component.html',
  styleUrl: './product-form.component.css'
})
export class ProductFormComponent implements OnInit {

  uploadingImage: boolean = false;
  imagePreview: string | null = null;
  selectedFile: File | null = null;
  previewUrl: string = '';
  productForm!: FormGroup;
  product!: Product;
  id!: number;
  today: Date = new Date();
  stores: any[] = [];
  categories: any[] = [];

  constructor(
    private productService: ProductService,
    private storeService: StoreService,
    private categoryService: CategoryService,
    private act: ActivatedRoute,
    private router: Router
  ) {
    this.productForm = new FormGroup({
      name: new FormControl('', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]),
      description: new FormControl(''),
      price: new FormControl(0, [Validators.required, Validators.min(0)]),
      stock: new FormControl(0, [Validators.required, Validators.min(0)]),
      active: new FormControl(true),
      storeId: new FormControl(null, Validators.required),
      categoryId: new FormControl(null, Validators.required)
    });

    this.id = this.act.snapshot.params['id'];

    if (this.id) {
      this.productService.getProductById(this.id).subscribe((result: Product) => {
        this.product = result;
        this.productForm.patchValue({
          name: result.name,
          description: result.description,
          price: result.price,
          stock: result.stock,
          active: result.active,
          storeId: result.storeId ?? (result as any).store?.id,
          categoryId: result.categoryId ?? (result as any).category?.id
        });
        if (result.imageUrl) {
          this.imagePreview = result.imageUrl;
        }
        this.today = result.createdAt ? new Date(result.createdAt) : new Date();
      });
    }
  }

  ngOnInit(): void {
    this.storeService.getAllStores().subscribe({
      next: (data: any[]) => {
        this.stores = data;
        console.log('✅ Stores:', data);
      },
      error: (err: any) => console.error('❌ Stores error:', err.status, err.url)
    });

    this.categoryService.getAllCategories().subscribe({
      next: (data: any[]) => {
        this.categories = data;
        console.log('✅ Categories:', data);
      },
      error: (err: any) => console.error('❌ Categories error:', err.status, err.url)
    });
  }

  get name() { return this.productForm.get('name'); }
  get description() { return this.productForm.get('description'); }
  get price() { return this.productForm.get('price'); }
  get stock() { return this.productForm.get('stock'); }
  get storeId() { return this.productForm.get('storeId'); }
  get categoryId() { return this.productForm.get('categoryId'); }

  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.selectedFile = input.files[0];
      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result as string;
        this.previewUrl = reader.result as string;
      };
      reader.readAsDataURL(this.selectedFile);
    }
  }

  clearImage(): void {
    this.imagePreview = null;
    this.selectedFile = null;
    this.previewUrl = '';
    this.uploadingImage = false;
  }

  goBack(): void {
    this.router.navigate(['/admin/products']);
  }

  onSubmit(): void {
    if (this.productForm.valid) {
      this.uploadingImage = true;
      const productData = {
        ...this.productForm.value,
        imageUrl: this.previewUrl || this.imagePreview // Include the base64 string
      };

      if (this.id) {
        this.productService.updateProduct(productData, this.id).subscribe({
          next: (res: any) => {
            console.log('✅ Produit modifié:', res);
            alert('✅ Produit modifié avec succès !');
            window.history.back();
            this.uploadingImage = false;
          },
          error: (err: any) => {
            console.error('❌ Erreur update:', err);
            alert('❌ Erreur: ' + err.status + ' - ' + err.message);
            this.uploadingImage = false;
          }
        });
      } else {
        this.productService.addProduct(productData).subscribe({
          next: (res: any) => {
            console.log('✅ Produit ajouté:', res);
            alert('✅ Produit ajouté avec succès !');
            window.history.back();
            this.uploadingImage = false;
          },
          error: (err: any) => {
            console.error('❌ Erreur add:', err);
            alert('❌ Erreur: ' + err.status + ' - ' + err.message);
            this.uploadingImage = false;
          }
        });
      }
    } else {
      Object.values(this.productForm.controls).forEach(c => c.markAsTouched());
    }
  }

}