import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-how-it-works',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './how-it-works.component.html',
  styleUrls: ['./how-it-works.component.css']
})
export class HowItWorksComponent {
  steps = [
    {
      number: '1',
      icon: 'fas fa-user-plus',
      title: 'Create Your Account',
      description: 'Sign up for free with your ESPRIT email and set up your seller profile in minutes.'
    },
    {
      number: '2',
      icon: 'fas fa-store',
      title: 'Set Up Your Store',
      description: 'Customize your store, add your products or services with photos and detailed descriptions.'
    },
    {
      number: '3',
      icon: 'fas fa-rocket',
      title: 'Start Selling',
      description: 'Share your store, receive orders and manage your sales from your dashboard.'
    }
  ];
}
