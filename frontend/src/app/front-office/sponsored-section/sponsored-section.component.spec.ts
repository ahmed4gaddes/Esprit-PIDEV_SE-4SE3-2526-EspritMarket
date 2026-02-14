import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SponsoredSectionComponent } from './sponsored-section.component';

describe('SponsoredSectionComponent', () => {
  let component: SponsoredSectionComponent;
  let fixture: ComponentFixture<SponsoredSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SponsoredSectionComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SponsoredSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
