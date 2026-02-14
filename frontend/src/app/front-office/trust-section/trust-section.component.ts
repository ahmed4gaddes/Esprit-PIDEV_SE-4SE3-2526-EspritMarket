import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-trust-section',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './trust-section.component.html',
  styleUrls: ['./trust-section.component.css']
})
export class TrustSectionComponent {
  trustItems = [
    {
      icon: 'fas fa-shield-alt',
      title: 'Secure Payments',
      description: 'Protected transactions with SSL encryption and reliable payment system'
    },
    {
      icon: 'fas fa-headset',
      title: 'Support 24/7',
      description: 'Dedicated team to support you at every step of your journey'
    },
    {
      icon: 'fas fa-award',
      title: 'Guaranteed Quality',
      description: 'Rating system and verified reviews for your trust'
    },
    {
      icon: 'fas fa-users',
      title: 'ESPRIT Community',
      description: 'Exclusively for ESPRIT students and alumni'
    }
  ];
}
