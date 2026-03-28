import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ProductssComponent } from './productss.component';

describe('ProductsComponent', () => {
  let component: ProductssComponent;
  let fixture: ComponentFixture<ProductssComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductssComponent, HttpClientTestingModule, RouterTestingModule]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ProductssComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
