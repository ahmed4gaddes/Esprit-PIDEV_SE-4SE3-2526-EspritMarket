import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-banner-carousel',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './banner-carousel.component.html',
  styleUrls: ['./banner-carousel.component.css']
})
export class BannerCarouselComponent implements OnInit, OnDestroy {
  currentSlide = 0;
  private intervalId: any;

  slides = [
    {
      badge: '🔥 Limited Offer',
      title: 'Winter Sales 2026',
      description: 'Up to 70% off on a selection of student products',
      gradientClass: 'gradient-red',
      btnText: 'Discover Offers'
    },
    {
      badge: '✨ New',
      title: 'New Sellers of the Month',
      description: 'Discover the latest stores created by your ESPRIT classmates',
      gradientClass: 'gradient-gray',
      btnText: 'Explore Stores'
    },
    {
      badge: '🚚 Free',
      title: 'Free Delivery',
      description: 'On all orders over 100 TND this month',
      gradientClass: 'gradient-mixed',
      btnText: 'Get It Now'
    }
  ];

  ngOnInit() {
    this.startAutoPlay();
  }

  ngOnDestroy() {
    this.stopAutoPlay();
  }

  startAutoPlay() {
    this.intervalId = setInterval(() => {
      this.nextSlide();
    }, 5000);
  }

  stopAutoPlay() {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }

  nextSlide() {
    this.currentSlide = (this.currentSlide + 1) % this.slides.length;
  }

  prevSlide() {
    this.currentSlide = (this.currentSlide - 1 + this.slides.length) % this.slides.length;
  }

  goToSlide(index: number) {
    this.currentSlide = index;
  }
}
