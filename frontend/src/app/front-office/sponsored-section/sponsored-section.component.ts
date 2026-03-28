import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-sponsored-section',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sponsored-section.component.html',
  styleUrls: ['./sponsored-section.component.css']
})
export class SponsoredSectionComponent implements OnInit, OnDestroy {
  ads = [
    {
      title: 'DesignEvent',
      subtitle: 'Professional event design by mouadh.fersi@esprit.tn',
      description: 'Boost your event visibility with premium sponsored design.',
      price: '$100.00',
      cta: 'View Sponsored Design',
      themeClass: 'theme-red'
    },
    {
      title: 'Design Event',
      subtitle: 'Featured by mouadh.fersi@esprit.tn',
      description: 'Creative branding pack for student communities and clubs.',
      price: '$10.00',
      cta: 'View Sponsored Design',
      themeClass: 'theme-purple'
    },
    {
      title: 'HackatonEvent',
      subtitle: 'Sponsored by mouadh.fersi@esprit.tn',
      description: 'Discover the official hackathon campaign and limited offers.',
      price: '$100.00',
      cta: 'View Sponsored Design',
      themeClass: 'theme-black'
    }
  ];

  currentIndex = 0;
  private autoSlideTimerId?: ReturnType<typeof setInterval>;

  ngOnInit(): void {
    this.startAutoSlide();
  }

  ngOnDestroy(): void {
    this.stopAutoSlide();
  }

  get currentAd() {
    return this.ads[this.currentIndex];
  }

  nextSlide(): void {
    this.currentIndex = (this.currentIndex + 1) % this.ads.length;
    this.restartAutoSlide();
  }

  prevSlide(): void {
    this.currentIndex = (this.currentIndex - 1 + this.ads.length) % this.ads.length;
    this.restartAutoSlide();
  }

  goToSlide(index: number): void {
    if (index < 0 || index >= this.ads.length) {
      return;
    }
    this.currentIndex = index;
    this.restartAutoSlide();
  }

  private startAutoSlide(): void {
    this.stopAutoSlide();
    this.autoSlideTimerId = setInterval(() => {
      this.currentIndex = (this.currentIndex + 1) % this.ads.length;
    }, 5000);
  }

  private stopAutoSlide(): void {
    if (this.autoSlideTimerId) {
      clearInterval(this.autoSlideTimerId);
      this.autoSlideTimerId = undefined;
    }
  }

  private restartAutoSlide(): void {
    this.startAutoSlide();
  }
}
