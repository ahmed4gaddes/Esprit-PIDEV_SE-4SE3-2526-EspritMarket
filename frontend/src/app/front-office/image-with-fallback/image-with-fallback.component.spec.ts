import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImageWithFallbackComponent } from './image-with-fallback.component';

describe('ImageWithFallbackComponent', () => {
  let component: ImageWithFallbackComponent;
  let fixture: ComponentFixture<ImageWithFallbackComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ImageWithFallbackComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ImageWithFallbackComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
