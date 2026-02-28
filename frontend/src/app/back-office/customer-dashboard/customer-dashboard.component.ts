import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-customer-dashboard',
    standalone: true,
    imports: [CommonModule],
    template: `
    <div class="dashboard-container">
      <h1>Espace Client</h1>
      <p>Bienvenue dans votre tableau de bord. Cette page est en cours de construction.</p>
    </div>
  `,
    styles: [`
    .dashboard-container {
      padding: 2rem;
      text-align: center;
      margin-top: 5rem;
    }
  `]
})
export class CustomerDashboardComponent { }
