import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { FormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let mockAuthSpy: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    mockAuthSpy = jasmine.createSpyObj('AuthService', ['register']);

    await TestBed.configureTestingModule({
      imports: [RegisterComponent, FormsModule, RouterTestingModule],
      providers: [
        { provide: AuthService, useValue: mockAuthSpy }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show error if role is not selected on step 1', () => {
    component.currentStep = 1;
    component.selectedRole = '';
    
    component.nextStep();
    
    expect(component.errorMessage).toBe('Please select a role to continue.');
    expect(component.currentStep).toBe(1);
  });

  it('should allow moving to step 2 if role is selected', () => {
    component.currentStep = 1;
    component.selectedRole = 'CUSTOMER';
    
    component.nextStep();
    
    expect(component.errorMessage).toBe('');
    expect(component.currentStep).toBe(2);
  });

  it('should show error if SELLER does not use @esprit.tn email', () => {
    component.currentStep = 2;
    component.selectedRole = 'SELLER';
    component.name = 'Test Seller';
    component.email = 'seller@gmail.com';
    component.dateOfBirth = '2000-01-01'; // Age 20+

    component.nextStep();
    
    expect(component.errorMessage).toBe('Sellers must use an @esprit.tn email address.');
    expect(component.currentStep).toBe(2);
  });

  it('should allow SELLER to proceed with @esprit.tn email', () => {
    component.currentStep = 2;
    component.selectedRole = 'SELLER';
    component.name = 'Test Seller';
    component.email = 'seller@esprit.tn';
    component.dateOfBirth = '2000-01-01'; // Age 20+

    component.nextStep();
    
    expect(component.errorMessage).toBe('');
    expect(component.currentStep).toBe(3);
  });

  it('should show error if age is less than 16', () => {
    component.currentStep = 2;
    component.selectedRole = 'CUSTOMER';
    component.name = 'Test Cust';
    component.email = 'cust@gmail.com';
    
    // Set DOB to today so age is 0
    component.dateOfBirth = new Date().toISOString().split('T')[0];

    component.nextStep();
    
    expect(component.errorMessage).toContain('You must be between 16 and 100 years old');
  });

  it('should block if passwords do not match', () => {
    component.termsAccepted = true;
    component.password = 'password123';
    component.confirmPassword = 'differentPassword';

    component.onSubmit();
    
    expect(component.errorMessage).toBe('Passwords do not match.');
    expect(mockAuthSpy.register).not.toHaveBeenCalled();
  });

  it('should block if terms are not accepted', () => {
    component.termsAccepted = false;

    component.onSubmit();
    
    expect(component.errorMessage).toBe('You must agree to the Terms of Service and Privacy Policy.');
    expect(mockAuthSpy.register).not.toHaveBeenCalled();
  });

  it('should block if phone number is invalid format', () => {
    component.termsAccepted = true;
    component.password = 'password123';
    component.confirmPassword = 'password123';
    component.phoneNumber = 'abcd'; // Invalid phone string

    component.onSubmit();
    
    expect(component.errorMessage).toBe('Phone number must be valid (8-15 digits).');
    expect(mockAuthSpy.register).not.toHaveBeenCalled();
  });

  it('should call register successfully and navigate', fakeAsync(() => {
    component.selectedRole = 'SELLER';
    component.name = 'Valid Seller';
    component.email = 'seller@esprit.tn';
    component.password = 'password123';
    component.confirmPassword = 'password123';
    component.termsAccepted = true;
    component.dateOfBirth = '2000-01-01';
    component.phoneNumber = '12345678';

    mockAuthSpy.register.and.returnValue(of({ role: 'SELLER' }));

    component.onSubmit();
    tick(50);

    expect(mockAuthSpy.register).toHaveBeenCalledWith({
       name: 'Valid Seller',
       email: 'seller@esprit.tn',
       password: 'password123',
       role: 'SELLER',
       dateOfBirth: '2000-01-01',
       phoneNumber: '12345678'
    });
    expect(router.navigate).toHaveBeenCalledWith(['/seller/dashboard']);
  }));

  it('should grab exact backend error message if registration fails', () => {
    component.termsAccepted = true;
    component.password = 'password123';
    component.confirmPassword = 'password123';

    // Simulate spring validation exception mapped to object { "email": "Already exists" }
    mockAuthSpy.register.and.returnValue(throwError(() => ({ error: { email: 'Email Already exists' } })));

    component.onSubmit();
    
    expect(component.errorMessage).toBe('Email Already exists');
  });
});
