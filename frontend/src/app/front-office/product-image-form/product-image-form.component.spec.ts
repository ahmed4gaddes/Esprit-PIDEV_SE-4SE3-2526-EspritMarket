import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ProductImageFormComponent } from './product-image-form.component';

describe('ProductImageFormComponent', () => {
  let component: ProductImageFormComponent;
  let fixture: ComponentFixture<ProductImageFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductImageFormComponent, HttpClientTestingModule, RouterTestingModule]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ProductImageFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
