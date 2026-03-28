import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AddstoreComponent } from './addstore.component';

describe('AddstoreComponent', () => {
  let component: AddstoreComponent;
  let fixture: ComponentFixture<AddstoreComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddstoreComponent, HttpClientTestingModule, RouterTestingModule]
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
