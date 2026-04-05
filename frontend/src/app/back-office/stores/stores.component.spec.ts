import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { StoresComponent } from './stores.component';
import { StoreServiceService } from '../../Services/store-service.service';

describe('StoresComponent', () => {
  let component: StoresComponent;
  let fixture: ComponentFixture<StoresComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StoresComponent],
      providers: [
        { provide: StoreServiceService, useValue: { getAllStores: () => of([]) } }
      ]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(StoresComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
