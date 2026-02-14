import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-featured-stores',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './featured-stores.component.html',
  styleUrls: ['./featured-stores.component.css']
})
export class FeaturedStoresComponent {
  stores = [
    {
      name: 'ArtMaker Studio',
      avatar: 'AM',
      avatarClass: 'avatar-red',
      rating: 4.9,
      sales: '250+',
      delivery: '48h',
      description: 'Custom graphic designs, professional logos and digital illustrations for your projects.',
      tags: ['Design', 'Logos', 'Illustration'],
      isTopSeller: true
    },
    {
      name: 'DevCraft Solutions',
      avatar: 'DC',
      avatarClass: 'avatar-gray',
      rating: 4.8,
      sales: '180+',
      delivery: '72h',
      description: 'Web and mobile development, custom application creation and innovative technical solutions.',
      tags: ['Web Dev', 'Mobile', 'API'],
      isTopSeller: false
    },
    {
      name: 'HandCrafted Treasures',
      avatar: 'HC',
      avatarClass: 'avatar-mixed',
      rating: 5.0,
      sales: '320+',
      delivery: '24h',
      description: 'Unique handmade creations: jewelry, accessories and decorative objects made by hand with passion.',
      tags: ['Jewelry', 'Crafts', 'Decor'],
      isTopSeller: false
    }
  ];
}
