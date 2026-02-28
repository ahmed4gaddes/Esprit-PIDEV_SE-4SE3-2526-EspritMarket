import { Routes } from '@angular/router';
import { LandingPageComponent } from './front-office/landing-page/landing-page.component';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { AdminLayoutComponent } from './back-office/admin-layout/admin-layout.component';
import { DashboardComponent } from './back-office/dashboard/dashboard.component';
import { ProductsComponent } from './back-office/products/products.component';
import { UsersComponent } from './back-office/users/users.component';
import { StoresComponent } from './back-office/stores/stores.component';
import { CategoriesComponent } from './back-office/categories/categories.component';
import { ReportsComponent } from './back-office/reports/reports.component';
import { SettingsComponent } from './back-office/settings/settings.component';
import { MainLayoutComponent } from './front-office/main-layout/main-layout.component';

import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./front-office/main-layout/main-layout.component').then(m => m.MainLayoutComponent),
    children: [
      { path: '', loadComponent: () => import('./front-office/landing-page/landing-page.component').then(m => m.LandingPageComponent) },
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      { path: 'forgot-password', loadComponent: () => import('./auth/forgot-password/forgot-password.component').then(m => m.ForgotPasswordComponent) },
      { path: 'reset-password', loadComponent: () => import('./auth/reset-password/reset-password.component').then(m => m.ResetPasswordComponent) },
      { path: 'complete-profile', loadComponent: () => import('./auth/complete-profile/complete-profile.component').then(m => m.CompleteProfileComponent) },
      { path: 'select-role', loadComponent: () => import('./auth/select-role/select-role.component').then(m => m.SelectRoleComponent) },

      // ===== TEST BRANCH: Event Module Routes =====
      // Front-Office (public)
      { path: 'events', loadComponent: () => import('./front-office/events-list/events-list.component').then(m => m.EventsListComponent) },
      { path: 'events/:id', loadComponent: () => import('./front-office/event-detail/event-detail.component').then(m => m.EventDetailComponent) },
      { path: 'lives', loadComponent: () => import('./front-office/lives-list/lives-list.component').then(m => m.LivesListComponent) },
      { path: 'lives/:id', loadComponent: () => import('./front-office/live-detail/live-detail.component').then(m => m.LiveDetailComponent) },

      // Back-Office Dashboards
      { path: 'dashboard/seller/lives', loadComponent: () => import('./back-office/seller-lives/seller-lives.component').then(m => m.SellerLivesComponent) },
      { path: 'dashboard/company/events', loadComponent: () => import('./back-office/company-events/company-events.component').then(m => m.CompanyEventsComponent) },
      { path: 'dashboard/expert/events', loadComponent: () => import('./back-office/expert-events/expert-events.component').then(m => m.ExpertEventsComponent) },
      { path: 'dashboard/customer/tickets', loadComponent: () => import('./back-office/customer-tickets/customer-tickets.component').then(m => m.CustomerTicketsComponent) },
      { path: 'admin/events', loadComponent: () => import('./back-office/admin-events/admin-events.component').then(m => m.AdminEventsComponent) },
      { path: 'admin/lives', loadComponent: () => import('./back-office/admin-lives/admin-lives.component').then(m => m.AdminLivesComponent) },
      // ===== END TEST BRANCH =====
      {
        path: 'seller/dashboard',
        loadComponent: () => import('./back-office/seller-dashboard/seller-dashboard.component').then(m => m.SellerDashboardComponent),
        canActivate: [roleGuard],
        data: { roles: ['SELLER'] }
      },
      {
        path: 'expert/dashboard',
        loadComponent: () => import('./back-office/expert-dashboard/expert-dashboard.component').then(m => m.ExpertDashboardComponent),
        canActivate: [roleGuard],
        data: { roles: ['EXPERT'] }
      },
      {
        path: 'company/dashboard',
        loadComponent: () => import('./back-office/company-dashboard/company-dashboard.component').then(m => m.CompanyDashboardComponent),
        canActivate: [roleGuard],
        data: { roles: ['COMPANY'] }
      },
      {
        path: 'sponsor/dashboard',
        loadComponent: () => import('./back-office/sponsor-dashboard/sponsor-dashboard.component').then(m => m.SponsorDashboardComponent),
        canActivate: [roleGuard],
        data: { roles: ['SPONSOR'] }
      },
      {
        path: 'customer/dashboard',
        loadComponent: () => import('./back-office/customer-dashboard/customer-dashboard.component').then(m => m.CustomerDashboardComponent),
        canActivate: [roleGuard],
        data: { roles: ['CUSTOMER'] }
      },
    ]
  },
  {
    path: 'admin',
    component: AdminLayoutComponent,
    canActivate: [roleGuard],
    data: { roles: ['ADMIN'] },
    children: [
      { path: '', component: DashboardComponent },
      { path: 'products', component: ProductsComponent },
      { path: 'users', component: UsersComponent },
      { path: 'stores', component: StoresComponent },
      { path: 'categories', component: CategoriesComponent },
      { path: 'reports', component: ReportsComponent },
      { path: 'settings', component: SettingsComponent },
    ]
  }
];
