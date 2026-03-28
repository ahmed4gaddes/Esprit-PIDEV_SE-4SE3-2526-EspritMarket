import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { DashboardComponent } from './dashboard.component';
import { AdminService } from '../../core/services/admin.service';
import { StoreServiceService } from '../../Services/store-service.service';
import { ProductService } from '../../Services/product.service';
import { CategoryService } from '../../Services/category.service';

const emptyKpis = {
  totalUsers: 0,
  activeUsers: 0,
  totalStores: 0,
  totalProducts: 0,
  totalEvents: 0,
  totalLives: 0,
  totalInternships: 0,
  pendingInternshipApplications: 0,
  ticketsSold: 0,
  ticketRevenue: 0
};

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardComponent, HttpClientTestingModule, RouterTestingModule],
      providers: [
        { provide: AdminService, useValue: { getDashboard: () => of({ kpis: emptyKpis, activity: [] }) } },
        { provide: StoreServiceService, useValue: { getAllStores: () => of([]) } },
        { provide: ProductService, useValue: { getAllProducts: () => of([]) } },
        { provide: CategoryService, useValue: { getAllCategories: () => of([]) } },
      ]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
