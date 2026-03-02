import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddstoreComponent } from './addstore.component';

describe('AddstoreComponent', () => {
  let component: AddstoreComponent;
  let fixture: ComponentFixture<AddstoreComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddstoreComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AddstoreComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
