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
        Validators.required
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
    this.router.navigate(['/seller/dashboard/images']);
  }

  onImgError(event: any): void {
    event.target.style.display = 'none';
  }

  onSubmit(): void {
  console.log('🔥 onSubmit called');
  console.log('Form valid:', this.imageForm.valid);

  if (this.imageForm.valid) {
    const rawData = { ...this.imageForm.value };
    // backend DTO attend 'order' au lieu de 'imageOrder'
    const data: any = {
      url: rawData.url,
      altText: rawData.altText,
      order: rawData.imageOrder,
      productId: rawData.productId
    };

    if (this.id) {
      // ✅ UPDATE
      this.imageService.updateImage(data, this.id).subscribe({
        next: (res) => {
          console.log('✅ Image modifiée:', res);
          alert('✅ Image modifiée avec succès !');
          this.router.navigateByUrl('/seller/dashboard/images');
        },
        error: (err) => {
          console.error('❌ Erreur update:', err);
          alert('❌ Erreur: ' + err.status + ' - ' + err.message);
        }
      });
    } else {
      // ✅ ADD
      this.imageService.addImage(data).subscribe({
        next: (res) => {
          console.log('✅ Image ajoutée:', res);
          alert('✅ Image added successfully!');
          this.imageForm.reset({ imageOrder: 0 });
          this.previewUrl = '';
        },
        error: (err) => {
          console.error('❌ Erreur add:', err);
          alert('❌ Erreur: ' + err.status + ' - ' + err.message);
        }
      });
    }

  } else {
    console.log('❌ Formulaire invalide');
    Object.values(this.imageForm.controls).forEach(c => c.markAsTouched());
  }
}
onFileSelected(event: Event): void {
  const input = event.target as HTMLInputElement;
  if (input.files && input.files[0]) {
    const file = input.files[0];
    const objectUrl = URL.createObjectURL(file);
    this.previewUrl = objectUrl;
    this.imageForm.patchValue({ url: objectUrl });
  }
}
}