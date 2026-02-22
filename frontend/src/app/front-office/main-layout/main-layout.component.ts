import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NavigationComponent } from '../navigation/navigation.component';
import { FooterComponent } from '../footer/footer.component';

@Component({
    selector: 'app-main-layout',
    standalone: true,
    imports: [CommonModule, RouterModule, NavigationComponent, FooterComponent],
    template: `
    <app-navigation></app-navigation>
    <main>
      <router-outlet></router-outlet>
    </main>
    <app-footer></app-footer>
  `,
    styles: [`
    main {
      min-height: 60vh;
      padding-top: 80px; /* Adjust based on navbar height */
    }
  `]
})
export class MainLayoutComponent { }
