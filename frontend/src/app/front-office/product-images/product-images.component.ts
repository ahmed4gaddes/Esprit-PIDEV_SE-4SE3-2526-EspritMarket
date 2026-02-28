import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProductImageService } from '../../Services/product-image.service'; // ✅ adapter
import { ProductImage } from '../../models/product-image';  
//D:\EspritMarket\S.A.EspritMarket\frontend\src\app\front-office\product-images\product-images.component.ts
@Component({
  selector: 'app-product-images',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './product-images.component.html',
  styleUrl: './product-images.component.css'
})
export class ProductImagesComponent implements OnInit {

  images: ProductImage[] = [];

  constructor(
    private imageService: ProductImageService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadImages();
  }

  loadImages(): void {
    this.imageService.getAllImages().subscribe({
      next: (data) => {
        this.images = data;
        console.log('✅ Images loaded:', data);
      },
      error: (err) => console.error('❌ Error:', err.status, err.url)
    });
  }

  deleteImage(id: number): void {
    if (confirm('Delete this image?')) {
      this.imageService.deleteImage(id).subscribe({
        next: () => this.images = this.images.filter(i => i.id !== id),
        error: (err) => console.error('❌ Delete error:', err)
      });
    }
  }

  editImage(id: number): void {
    this.router.navigate(['/admin/product-images/edit', id]);
  }
  onImgError(event: Event) {
  const element = event.target as HTMLImageElement;
  element.src = 'assets/default-image.png'; // image par défaut
}
}
