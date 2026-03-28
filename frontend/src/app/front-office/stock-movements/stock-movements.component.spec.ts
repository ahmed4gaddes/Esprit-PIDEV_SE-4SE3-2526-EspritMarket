import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { StockMovementsComponent } from './stock-movements.component';

describe('StockMovementsComponent', () => {
  let component: StockMovementsComponent;
  let fixture: ComponentFixture<StockMovementsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StockMovementsComponent, HttpClientTestingModule, RouterTestingModule]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(StockMovementsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
