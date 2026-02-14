import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-stores',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './stores.component.html',
  styleUrls: ['./stores.component.css']
})
export class StoresComponent {
  stores = [
    { id: 1, name: 'ArtMaker Studio', owner: 'Amine Slim', category: 'Design', sales: 156, rating: 4.8, status: 'Active', location: 'ESPRIT Ghazela' },
    { id: 2, name: 'DevCraft', owner: 'Yasmine Dridi', category: 'Software', sales: 89, rating: 4.9, status: 'Active', location: 'Online' },
    { id: 3, name: 'HandCrafted', owner: 'Leila Jazi', category: 'Handmade', sales: 42, rating: 4.5, status: 'Active', location: 'ESPRIT Chotrana' },
    { id: 4, name: 'TechHub Pro', owner: 'Kais Ben Ali', category: 'Hardware', sales: 230, rating: 4.7, status: 'Pending', location: 'ESPRIT Ghazela' },
    { id: 5, name: 'Fashion Elite', owner: 'Mouna Trabelsi', category: 'Fashion', sales: 312, rating: 4.6, status: 'Active', location: 'Tunis' }
  ];
}
