import { Component, OnInit, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-recommendations',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './recommendations.component.html',
  styleUrls:[ './recommendations.component.css']
})
export class RecommendationsComponent implements OnInit {

  @Input() productId!: number;

  sameCategory: any[] = [];
  similarPrice: any[]  = [];
  bestSellers: any[]   = [];

  constructor(
    private http: HttpClient,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    // Si utilisé comme route directe → lire l'id depuis l'URL
    if (!this.productId) {
      this.productId = +this.route.snapshot.paramMap.get('id')!;
    }
    if (this.productId) {
      this.loadRecommendations();
    }
  }

  loadRecommendations(): void {
    this.http.get<any>(
      `http://localhost:8081/api/recommendations/product/${this.productId}`,
      { withCredentials: true }
    ).subscribe({
      next: (data) => {
        this.sameCategory = data.sameCategory;
        this.similarPrice  = data.similarPrice;
        this.bestSellers   = data.bestSellers;
      },
      error: (err) => console.error('Erreur recommandations :', err)
    });
  }
}