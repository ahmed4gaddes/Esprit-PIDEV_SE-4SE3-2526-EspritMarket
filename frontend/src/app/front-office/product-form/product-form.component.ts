import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { catchError, of, switchMap } from 'rxjs';
import { ProductService } from '../../Services/product.service';   // aligned import
import { Product } from '../../models/product';                     // aligned import
import { StoreServiceService } from '../../Services/store-service.service'; // aligned import
import { CategoryService } from '../../Services/category.service'; // aligned import
import { UploadService } from '../../core/services/upload.service';
import { ProductImageService } from '../../Services/product-image.service';

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

  // Lists used by select inputs
  stores: any[]     = [];
  categories: any[] = [];

  /** Optional main image (uploaded after product is saved). */
  selectedFile: File | null = null;
  imagePreview: string | null = null;
  uploadingImage = false;

  constructor(
    private productService: ProductService,
    private storeService: StoreServiceService,
    private categoryService: CategoryService,
    private uploadService: UploadService,
    private productImageService: ProductImageService,
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
      // createdAt is handled by @PrePersist on backend -> not in form
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

  
 // Load stores and categories for select inputs
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
    this.router.navigate(['/seller/dashboard/products']);
  }

  onImageSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file || !file.type.startsWith('image/')) {
      this.clearImage();
      return;
    }
    this.selectedFile = file;
    const reader = new FileReader();
    reader.onload = () => (this.imagePreview = reader.result as string);
    reader.readAsDataURL(file);
  }

  clearImage(): void {
    this.selectedFile = null;
    this.imagePreview = null;
  }

  // ── Submit Add / Update ────────────────────────
  onSubmit(): void {
    if (!this.productForm.valid) {
      Object.values(this.productForm.controls).forEach((c) => c.markAsTouched());
      return;
    }

    const productData = { ...this.productForm.value };
    const nameStr = String(productData.name || '');

    if (this.id) {
      this.productService
        .updateProduct(productData, this.id)
        .pipe(
          switchMap(() => {
            if (!this.selectedFile) {
              return of(null);
            }
            this.uploadingImage = true;
            return this.uploadService.uploadImage(this.selectedFile).pipe(
              switchMap((url) =>
                this.productImageService.addImageForProduct(this.id, url, nameStr)
              ),
              catchError((err) => {
                console.error('Image upload failed', err);
                alert('Product updated, but image upload failed.');
                return of(null);
              })
            );
          })
        )
        .subscribe({
          next: () => {
            this.uploadingImage = false;
            alert('✅ Produit modifié avec succès !');
            this.clearImage();
            this.router.navigateByUrl('/seller/dashboard/products');
          },
          error: (err) => {
            this.uploadingImage = false;
            console.error('❌ Erreur update:', err);
            alert('❌ Erreur: ' + err.status + ' - ' + err.message);
          }
        });
      return;
    }

    // CREATE
    this.productService
      .addProduct(productData)
      .pipe(
        switchMap((res) => {
          const pid = res?.id;
          if (!this.selectedFile || pid == null) {
            return of(res);
          }
          this.uploadingImage = true;
          return this.uploadService.uploadImage(this.selectedFile).pipe(
            switchMap((url) => this.productImageService.addImageForProduct(pid, url, nameStr)),
            switchMap(() => of(res)),
            catchError((err) => {
              console.error('Image upload failed', err);
              alert('Product created, but image upload failed.');
              return of(res);
            })
          );
        })
      )
      .subscribe({
        next: (res) => {
          this.uploadingImage = false;
          alert('✅ Produit ajouté avec succès !');
          this.productForm.reset({ active: true, price: 0, stock: 0 });
          this.clearImage();
          this.router.navigateByUrl('/seller/dashboard/products');
        },
        error: (err) => {
          this.uploadingImage = false;
          console.error('❌ Erreur add:', err);
          alert('❌ Erreur: ' + err.status + ' - ' + err.message);
        }
      });
  }
}