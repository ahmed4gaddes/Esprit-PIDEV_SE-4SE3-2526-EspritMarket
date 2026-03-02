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
import { AddstoreComponent } from './front-office/addstore/addstore.component';
import { ProductssComponent } from './front-office/products/productss.component';
import { ProductFormComponent } from './front-office/product-form/product-form.component';
import { StockMovementsComponent } from './front-office/stock-movements/stock-movements.component';
import { StockMovementFormComponent } from './front-office/stock-movement-form/stock-movement-form.component';
import { ProductImagesComponent } from './front-office/product-images/product-images.component';
import { CategoryFormComponent } from './front-office/category-form/category-form.component';
import { CategoryComponent } from './front-office/category/category.component';
import { ProductImageFormComponent } from './front-office/product-image-form/product-image-form.component';
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
      {
        path: 'seller/dashboard',
        loadComponent: () => import('./front-office/store/store.component').then(m => m.StoreComponent),
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
  },
  { path: 'store', component: StoreComponent },
{ path: 'store/add',       component: AddstoreComponent },
  { path: 'store/edit/:id',  component: AddstoreComponent }, 
   
  { path: 'user/products',          component: ProductssComponent },
  { path: 'user/products/add',      component: ProductFormComponent },
  { path: 'user/products/edit/:id', component: ProductFormComponent },
  { path: 'user/stock-movements',          component: StockMovementsComponent },
{ path: 'user/stock-movements/add',      component: StockMovementFormComponent },
{ path: 'user/stock-movements/edit/:id', component: StockMovementFormComponent },
{ path: 'user/product-images',          component: ProductImagesComponent },
{ path: 'user/product-images/add',      component: ProductImageFormComponent },
{ path: 'user/product-images/edit/:id', component: ProductImageFormComponent },
{ path: 'user/category',          component: CategoryComponent },
{ path: 'user/category/add',      component: CategoryFormComponent },
{ path: 'user/category/edit/:id', component: CategoryFormComponent },
];
