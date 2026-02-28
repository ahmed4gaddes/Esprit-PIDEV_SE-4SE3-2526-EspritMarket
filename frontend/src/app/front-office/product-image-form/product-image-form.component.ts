import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProductImageService } from '../../Services/product-image.service'; // ✅ adapter
import { ProductImage } from '../../models/product-image';
import { ProductService } from '../../Services/product.service';             // ✅ adapter

@Component({
  selector: 'app-product-image-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './product-image-form.component.html',
  styleUrl: './product-image-form.component.css'
})
export class ProductImageFormComponent implements OnInit {

  imageForm!: FormGroup;
  image!: ProductImage;
  id!: number;

  products: any[] = [];
  previewUrl: string = '';

  constructor(
    private imageService: ProductImageService,
    private productService: ProductService,
    private act: ActivatedRoute,
    private router: Router
  ) {
    this.imageForm = new FormGroup({
      url: new FormControl('', [
        Validators.required,
        Validators.maxLength(500)
      ]),
      altText: new FormControl('', [
        Validators.maxLength(200)
      ]),
      imageOrder: new FormControl(0, [
        Validators.required,
        Validators.min(0)
      ]),
      productId: new FormControl(null, [
        Validators.required
      ])
    });

    this.id = this.act.snapshot.params['id'];

    if (this.id) {
      this.imageService.getImageById(this.id).subscribe((result: ProductImage) => {
        this.image = result;
        this.imageForm.patchValue({
          url:        result.url,
          altText:    result.altText,
          imageOrder: result.imageOrder,
          productId:  result.productId ?? (result as any).product?.id
        });
        this.previewUrl = result.url;
      });
    }

    // Update preview on URL change
    this.imageForm.get('url')?.valueChanges.subscribe((val: string) => {
      this.previewUrl = val;
    });
  }

  ngOnInit(): void {
    this.productService.getAllProducts().subscribe((data: any[]) => {
      this.products = data;
    });
  }

  // ── Getters ─────────────────────────────────────
  get url()        { return this.imageForm.get('url'); }
  get altText()    { return this.imageForm.get('altText'); }
  get imageOrder() { return this.imageForm.get('imageOrder'); }
  get productId()  { return this.imageForm.get('productId'); }

  goBack(): void {
    this.router.navigate(['/admin/product-images']);
  }

  onImgError(event: any): void {
    event.target.style.display = 'none';
  }

  onSubmit(): void {
    if (this.imageForm.valid) {
      if (this.id) {
        this.imageService.updateImage(this.imageForm.value, this.id).subscribe(() => {
          this.router.navigateByUrl('/admin/product-images');
        });
      } else {
        this.imageService.addImage(this.imageForm.value).subscribe(() => {
          alert('✅ Image added successfully!');
          this.imageForm.reset({ imageOrder: 0 });
          this.previewUrl = '';
        });
      }
    }
  }
}