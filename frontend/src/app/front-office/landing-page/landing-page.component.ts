import { Component } from '@angular/core';
import { NavigationComponent } from '../navigation/navigation.component';
import { HeroSectionComponent } from '../hero-section/hero-section.component';
import { BannerCarouselComponent } from '../banner-carousel/banner-carousel.component';
import { CategoriesSectionComponent } from '../categories-section/categories-section.component';
import { TrendingProductsComponent } from '../trending-products/trending-products.component';
import { FeaturedStoresComponent } from '../featured-stores/featured-stores.component';
import { SponsoredSectionComponent } from '../sponsored-section/sponsored-section.component';
import { HowItWorksComponent } from '../how-it-works/how-it-works.component';
import { TrustSectionComponent } from '../trust-section/trust-section.component';
import { FooterComponent } from '../footer/footer.component';

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [
    NavigationComponent,
    HeroSectionComponent,
    BannerCarouselComponent,
    CategoriesSectionComponent,
    TrendingProductsComponent,
    FeaturedStoresComponent,
    SponsoredSectionComponent,
    HowItWorksComponent,
    TrustSectionComponent,
    FooterComponent
  ],
  templateUrl: './landing-page.component.html',
  styleUrls: ['./landing-page.component.css']
})
export class LandingPageComponent { }
