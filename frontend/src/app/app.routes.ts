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
import { AdminStageApplicationsComponent } from './back-office/admin-stage-applications/admin-stage-applications.component';
import { AdminAuditLogsComponent } from './back-office/admin-audit-logs/admin-audit-logs.component';
import { MainLayoutComponent } from './front-office/main-layout/main-layout.component';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { AddstoreComponent } from './front-office/addstore/addstore.component';
import { ProductssComponent } from './front-office/products/productss.component';
import { ProductFormComponent } from './front-office/product-form/product-form.component';
import { StockComponent } from './front-office/stock/stock.component';
import { StockFormComponent } from './front-office/stock-form/stock-form.component';
import { CategoryFormComponent } from './front-office/category-form/category-form.component';
import { CategoryComponent } from './front-office/category/category.component';
import { StoreComponent } from './front-office/store/store.component';

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

      // ===== Module Event =====
      { path: 'events', loadComponent: () => import('./front-office/events-list/events-list.component').then(m => m.EventsListComponent) },
      { path: 'events/:id', loadComponent: () => import('./front-office/event-detail/event-detail.component').then(m => m.EventDetailComponent) },
      { path: 'lives', loadComponent: () => import('./front-office/lives-list/lives-list.component').then(m => m.LivesListComponent) },
      { path: 'lives/:id', loadComponent: () => import('./front-office/live-detail/live-detail.component').then(m => m.LiveDetailComponent) },
      { path: 'live/local/:id', loadComponent: () => import('./front-office/local-live/local-live.component').then(m => m.LocalLiveComponent) },
      {
        path: 'company/dashboard',
        loadComponent: () => import('./back-office/company-dashboard/company-dashboard.component').then(m => m.CompanyDashboardComponent),
        canActivate: [roleGuard], data: { roles: ['COMPANY'] }
      },
      {
        path: 'company/dashboard/events',
        loadComponent: () => import('./back-office/company-events/company-events.component').then(m => m.CompanyEventsComponent),
        canActivate: [roleGuard], data: { roles: ['COMPANY'] }
      },
      {
        path: 'expert/dashboard/events',
        loadComponent: () => import('./back-office/expert-events/expert-events.component').then(m => m.ExpertEventsComponent),
        canActivate: [roleGuard], data: { roles: ['EXPERT'] }
      },
      {
        path: 'customer/dashboard/tickets',
        loadComponent: () => import('./back-office/customer-tickets/customer-tickets.component').then(m => m.CustomerTicketsComponent),
        canActivate: [roleGuard], data: { roles: ['CUSTOMER'] }
      },
      { path: 'admin/events', loadComponent: () => import('./back-office/admin-events/admin-events.component').then(m => m.AdminEventsComponent) },
      { path: 'admin/lives', loadComponent: () => import('./back-office/admin-lives/admin-lives.component').then(m => m.AdminLivesComponent) },
      
      // ===== Dashboards Demo (Cross-Module) =====
      // Legacy aliases kept as redirects for backward compatibility
      { path: 'demo/seller-dashboard', redirectTo: 'seller/dashboard', pathMatch: 'full' },
      { path: 'demo/expert-dashboard', redirectTo: 'service-backoffice', pathMatch: 'full' },
      { path: 'expert/dashboard', redirectTo: '/service-backoffice', pathMatch: 'full' },

      // ===== Module Store =====
      {
        path: 'seller/dashboard',
        loadComponent: () => import('./back-office/seller-layout/seller-layout.component').then(m => m.SellerLayoutComponent),
        canActivate: [roleGuard], data: { roles: ['SELLER'] },
        children: [
          { path: '', loadComponent: () => import('./front-office/store/store.component').then(m => m.StoreComponent) },
          { path: 'lives', loadComponent: () => import('./back-office/seller-lives/seller-lives.component').then(m => m.SellerLivesComponent) },
          { path: 'products', loadComponent: () => import('./front-office/products/productss.component').then(m => m.ProductssComponent) },
          { path: 'categories', loadComponent: () => import('./front-office/category/category.component').then(m => m.CategoryComponent) },
          { path: 'stock', loadComponent: () => import('./front-office/stock/stock.component').then(m => m.StockComponent) },
        ]
      },
      {
        path: 'sponsor/dashboard',
        loadComponent: () => import('./back-office/sponsor-dashboard/sponsor-dashboard.component').then(m => m.SponsorDashboardComponent),
        canActivate: [roleGuard], data: { roles: ['SPONSOR'] }
      },
      {
        path: 'customer/dashboard',
        loadComponent: () => import('./back-office/customer-dashboard/customer-dashboard.component').then(m => m.CustomerDashboardComponent),
        canActivate: [roleGuard], data: { roles: ['CUSTOMER'] }
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
      { path: 'audit-logs', component: AdminAuditLogsComponent },
      { path: 'stage-applications', component: AdminStageApplicationsComponent },
      { path: 'settings', component: SettingsComponent },
    ]
  },

  // ===== Module Service (son module) =====
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
  },

  // ===== Routes Store directes =====
  { path: 'store', component: StoreComponent },
  { path: 'store/add', component: AddstoreComponent },
  { path: 'store/edit/:id', component: AddstoreComponent },
  { path: 'user/products', component: ProductssComponent },
  { path: 'user/products/add', component: ProductFormComponent },
  { path: 'user/products/onSearch', component: ProductssComponent },
  { path: 'user/products/edit/:id', component: ProductFormComponent },
  { path: 'user/stock-movements', component: StockComponent },
  { path: 'user/stock-movements/add', component: StockFormComponent },
  { path: 'user/stock-movements/edit/:id', component: StockFormComponent },
  { path: 'user/category', component: CategoryComponent },
  { path: 'user/category/add', component: CategoryFormComponent },
  { path: 'user/category/edit/:id', component: CategoryFormComponent },
];
