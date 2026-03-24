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
      { path: 'select-role', loadComponent: () => import('./auth/select-role/select-role.component').then(m => m.SelectRoleComponent) },
      {
        path: 'seller/dashboard',
        loadComponent: () => import('./back-office/seller-dashboard/seller-dashboard.component').then(m => m.SellerDashboardComponent),
        canActivate: [roleGuard],
        data: { roles: ['SELLER'] }
      },

      {
        path: 'sponsor/dashboard',
        loadComponent: () => import('./back-office/sponsor-dashboard/sponsor-dashboard.component').then(m => m.SponsorDashboardComponent),
        canActivate: [roleGuard],
        data: { roles: ['SPONSOR'] }
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
  },
  {
    path: 'service-backoffice',
    loadComponent: () => import('./back-office/service-layout/service-layout.component').then(m => m.ServiceLayoutComponent),
    canActivate: [roleGuard],
    data: { roles: ['EXPERT', 'COMPANY'] },
    children: [
      { path: '', loadComponent: () => import('./back-office/service-dashboard/service-dashboard.component').then(m => m.ServiceDashboardComponent) },
      { path: 'workshops', loadComponent: () => import('./back-office/workshop-list/workshop-list.component').then(m => m.WorkshopListComponent) },
      { path: 'certificates', loadComponent: () => import('./back-office/certificate-list/certificate-list.component').then(m => m.CertificateListComponent) },
      { path: 'internships', loadComponent: () => import('./back-office/internship-list/internship-list.component').then(m => m.InternshipListComponent) },
      { path: 'courses', loadComponent: () => import('./back-office/course-list/course-list.component').then(m => m.CourseListComponent) },
      { path: 'registrations', loadComponent: () => import('./back-office/registration-list/registration-list.component').then(m => m.RegistrationListComponent) },
      { path: 'gamification', loadComponent: () => import('./back-office/gamification-list/gamification-list.component').then(m => m.GamificationListComponent) },
      { path: 'calendar', loadComponent: () => import('./back-office/calendar-list/calendar-list.component').then(m => m.CalendarListComponent) },
      { path: 'cert-validations', loadComponent: () => import('./back-office/cert-validation-list/cert-validation-list.component').then(m => m.CertValidationListComponent) },
      { path: 'documents', loadComponent: () => import('./back-office/supporting-doc-list/supporting-doc-list.component').then(m => m.SupportingDocListComponent) },
      { path: 'evaluations', loadComponent: () => import('./back-office/workshop-eval-list/workshop-eval-list.component').then(m => m.WorkshopEvalListComponent) },
      { path: 'services', loadComponent: () => import('./back-office/service-list/service-list.component').then(m => m.ServiceListComponent) },
    ]
  }
];
