import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-categories-section',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './categories-section.component.html',
  styleUrls: ['./categories-section.component.css']
})
export class CategoriesSectionComponent {
  categories = [
    {
      icon: 'fas fa-palette',
      title: 'Design & Art',
      description: 'Logos, illustrations, graphics',
      count: '250+ products'
    },
    {
      icon: 'fas fa-code',
      title: 'Development',
      description: 'Websites, applications, scripts',
      count: '180+ products'
    },
    {
      icon: 'fas fa-graduation-cap',
      title: 'Courses & Tutoring',
      description: 'Academic support, training',
      count: '120+ services'
    },
    {
      icon: 'fas fa-hand-holding-heart',
      title: 'Handmade',
      description: 'Accessories, jewelry, decor',
      count: '300+ products'
    },
    {
      icon: 'fas fa-laptop',
      title: 'Tech & Gadgets',
      description: 'Electronics, accessories',
      count: '90+ products'
    },
    {
      icon: 'fas fa-briefcase',
      title: 'Services',
      description: 'Consulting, marketing, writing',
      count: '150+ services'
    }
  ];
}
