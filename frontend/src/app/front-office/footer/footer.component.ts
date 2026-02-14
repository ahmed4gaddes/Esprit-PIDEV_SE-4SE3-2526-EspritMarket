import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent {
  socialLinks = [
    { icon: 'fab fa-facebook-f', url: '#' },
    { icon: 'fab fa-instagram', url: '#' },
    { icon: 'fab fa-linkedin-in', url: '#' },
    { icon: 'fab fa-twitter', url: '#' }
  ];

  marketplaceLinks = ['All Stores', 'Categories', 'Trending Products', 'New Arrivals'];
  sellerLinks = ['Create a Store', 'Seller Guide', 'Pricing & Commissions', 'Success Stories'];
  supportLinks = ['Help Center', 'Terms of Use', 'Privacy Policy', 'Contact Us'];
}
