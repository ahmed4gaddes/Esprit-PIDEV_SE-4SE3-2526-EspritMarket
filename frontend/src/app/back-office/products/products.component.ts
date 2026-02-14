import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.css']
})
export class ProductsComponent {
  products = [
    { id: 1, name: 'Logo Design Pack', category: 'Design', price: '150 TND', stock: 45, status: 'Active', seller: 'ArtMaker Studio' },
    { id: 2, name: 'React Dashboard', category: 'Dev', price: '85 TND', stock: 12, status: 'Active', seller: 'DevCraft' },
    { id: 3, name: 'Silver Necklace', category: 'Handmade', price: '120 TND', stock: 8, status: 'Out of Stock', seller: 'HandCrafted' },
    { id: 4, name: 'Tech Gadget', category: 'Tech', price: '320 TND', stock: 24, status: 'Active', seller: 'TechHub Pro' },
    { id: 5, name: 'Minimalist T-Shirt', category: 'Fashion', price: '45 TND', stock: 100, status: 'Active', seller: 'Fashion Elite' }
  ];
}
