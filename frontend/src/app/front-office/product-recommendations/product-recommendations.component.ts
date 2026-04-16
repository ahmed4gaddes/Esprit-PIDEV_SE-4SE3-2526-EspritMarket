import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule, Location } from '@angular/common';
import { ProductService } from '../../Services/product.service';
import { Product } from '../../models/product';

@Component({
  selector: 'app-product-recommendations',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './product-recommendations.component.html',
  styleUrl: './product-recommendations.component.css'
})
export class ProductRecommendationsComponent implements OnInit {
  productId!: number;
  product: Product | null = null;
  recommendations: any = null;
  isLoading = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private location: Location
  ) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.productId = +id;
        this.loadProductAndRecommendations();
      }
    });
  }

  loadProductAndRecommendations(): void {
    this.isLoading = true;
    
    // Get the product details
    this.productService.getProductById(this.productId).subscribe({
      next: (product) => {
        this.product = product;
      },
      error: (err) => console.error('Failed to load product', err)
    });

    // Get recommendations
    this.productService.getProductRecommendations(this.productId).subscribe({
      next: (data) => {
        this.recommendations = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Failed to load recommendations', err);
        this.recommendations = { sameCategory: [], similarPrice: [], bestSellers: [] };
        this.isLoading = false;
      }
    });
  }

  goBack(): void {
    this.location.back();
  }
}
