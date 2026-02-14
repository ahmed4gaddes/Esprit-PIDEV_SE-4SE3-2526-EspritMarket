import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-sponsored-section',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sponsored-section.component.html',
  styleUrls: ['./sponsored-section.component.css']
})
export class SponsoredSectionComponent {
  stores = [
    {
      name: 'TechHub Pro',
      avatar: 'TH',
      avatarClass: 'avatar-red',
      rating: 4.9,
      sales: '500+',
      delivery: '24h',
      description: 'Your destination for the latest technologies and electronic gadgets. Authentic products with warranty.',
      tags: ['Tech', 'Gadgets', 'Electronics'],
      offer: '-15% with code ESPRIT15'
    },
    {
      name: 'Fashion Elite',
      avatar: 'FE',
      avatarClass: 'avatar-gray',
      rating: 5.0,
      sales: '800+',
      delivery: '48h',
      description: 'Trendy fashion and luxury accessories for all styles. Exclusive collections for students.',
      tags: ['Fashion', 'Accessories', 'Luxury'],
      offer: 'Free delivery from 50 TND'
    },
    {
      name: 'Home & Deco',
      avatar: 'HD',
      avatarClass: 'avatar-mixed',
      rating: 4.8,
      sales: '350+',
      delivery: '72h',
      description: 'Transform your space with our unique and affordable decoration items for students.',
      tags: ['Decor', 'Home', 'Design'],
      offer: '2nd item at -50%'
    }
  ];
}
