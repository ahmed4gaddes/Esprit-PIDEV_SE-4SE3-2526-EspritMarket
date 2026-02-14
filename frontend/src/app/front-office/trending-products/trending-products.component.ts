import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-trending-products',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './trending-products.component.html',
  styleUrls: ['./trending-products.component.css']
})
export class TrendingProductsComponent {
  activeTab = 'all';

  products = [
    {
      id: 1,
      name: 'Logo Design Pack',
      price: 150,
      category: 'design',
      store: 'ArtMaker Studio',
      rating: 4.9,
      image: 'https://images.unsplash.com/photo-1644375391935-908706dfcae6?auto=format&fit=crop&q=80&w=400'
    },
    {
      id: 2,
      name: 'React Dashboard Template',
      price: 85,
      category: 'dev',
      store: 'DevCraft Solutions',
      rating: 4.8,
      image: 'https://images.unsplash.com/photo-1637937459053-c788742455be?auto=format&fit=crop&q=80&w=400'
    },
    {
      id: 3,
      name: 'Handmade Silver Necklace',
      price: 120,
      category: 'handmade',
      store: 'HandCrafted Treasures',
      rating: 5.0,
      image: 'https://images.unsplash.com/photo-1573227890085-12ab5d68a170?auto=format&fit=crop&q=80&w=400'
    },
    {
      id: 4,
      name: 'Tech Gadget Bundle',
      price: 320,
      category: 'tech',
      store: 'TechHub Pro',
      rating: 4.7,
      image: 'https://images.unsplash.com/photo-1717295248521-4c1f2b6bcd6e?auto=format&fit=crop&q=80&w=400'
    },
    {
      id: 5,
      name: 'Minimalist T-Shirt',
      price: 45,
      category: 'fashion',
      store: 'Fashion Elite',
      rating: 4.9,
      image: 'https://images.unsplash.com/photo-1767334010488-83cdb8539273?auto=format&fit=crop&q=80&w=400'
    },
    {
      id: 6,
      name: 'Abstract Canvas Art',
      price: 210,
      category: 'design',
      store: 'ArtMaker Studio',
      rating: 4.8,
      image: 'https://images.unsplash.com/photo-1567016546367-c27a0d56712e?auto=format&fit=crop&q=80&w=400'
    }
  ];

  categories = [
    { id: 'all', label: 'All' },
    { id: 'design', label: 'Design' },
    { id: 'dev', label: 'Dev' },
    { id: 'handmade', label: 'Handmade' },
    { id: 'tech', label: 'Tech' }
  ];

  get filteredProducts() {
    if (this.activeTab === 'all') {
      return this.products;
    }
    return this.products.filter(p => p.category === this.activeTab);
  }

  setActiveTab(id: string) {
    this.activeTab = id;
  }

  onImageError(event: Event) {
    const img = event.target as HTMLImageElement;
    img.src = 'https://via.placeholder.com/400x300?text=Image+Not+Available';
  }
}
